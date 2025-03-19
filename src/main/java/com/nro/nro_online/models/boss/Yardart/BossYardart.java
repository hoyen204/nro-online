package com.nro.nro_online.models.boss.Yardart;

import com.nro.nro_online.consts.ConstRatio;
import com.nro.nro_online.models.boss.Boss;
import com.nro.nro_online.models.boss.BossData;
import com.nro.nro_online.models.item.ItemOption;
import com.nro.nro_online.models.map.ItemMap;
import com.nro.nro_online.models.map.Zone;
import com.nro.nro_online.models.player.Pet;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.models.skill.Skill;
import com.nro.nro_online.services.EffectSkillService;
import com.nro.nro_online.services.MapService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.SkillService;
import com.nro.nro_online.services.func.ChangeMapService;
import com.nro.nro_online.utils.Log;
import com.nro.nro_online.utils.SkillUtil;
import com.nro.nro_online.utils.Util;

public class BossYardart extends Boss {

    private static final int DROP_ITEM_DELAY = 5000;
    private static final int DROP_ITEM_ID = 590;
    private static final int DROP_ITEM_QUANTITY = 2;

    private final int xMoveMin;
    private final int xMoveMax;
    private int dir = 1;
    private long lastTimeDropItem;

    public BossYardart(Zone zoneDefault, byte bossId, BossData data, int xMoveMin, int xMoveMax) {
        super(bossId, data);
        this.zone = zoneDefault;
        this.xMoveMin = xMoveMin;
        this.xMoveMax = xMoveMax;
        this.location.x = xMoveMin;
        this.location.y = zoneDefault.map.yPhysicInTop(xMoveMin, 5);
    }

    @Override
    public void joinMapByZone(Zone zone, int x) {
        ChangeMapService.gI().changeMap(this, zone, xMoveMin + (xMoveMax - xMoveMin) / 2, location.y);
    }

    @Override
    public synchronized int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        if (isDie())
            return 0;

        if (!piercing && Util.isTrue(0, 100)) {
            chat("Xí hụt");
            return 0;
        }

        damage = Math.min(damage, nPoint.hpg / 200);
        if (System.currentTimeMillis() - lastTimeDropItem > DROP_ITEM_DELAY) {
            rewards(plAtt);
            lastTimeDropItem = System.currentTimeMillis();
        }

        nPoint.subHP(damage);
        if (isDie()) {
            rewards(plAtt);
            die();
        }
        return damage;
    }

    @Override
    public void rewards(Player plKill) {
        if (plKill == null || Util.isTrue(55, 100))
            return;

        int dropX = (plKill.isPet ? ((Pet) plKill).master.location.x : plKill.location.x);
        for (int i = 0; i < DROP_ITEM_QUANTITY; i++) {
            ItemMap item = new ItemMap(zone, DROP_ITEM_ID, 1, dropX + Util.nextInt(-20, 20),
                    zone.map.yPhysicInTop(location.x, location.y - 24), (int) plKill.id);
            item.options.add(new ItemOption(30, 0));
            Service.getInstance().dropItemMap(zone, item);
        }
    }

    @Override
    public void attack() {
        try {
            if (charge())
                return;

            Player pl = getPlayerAttack();
            if (pl == null || pl.isDie() || pl.isMiniPet)
                return;

            playerSkill.skillSelect = getSkillAttack();
            if (Util.getDistance(this, pl) <= getRangeCanAttackWithSkillSelect()) {
                moveNearPlayer(pl);
                effectCharger();
                SkillService.gI().useSkill(this, pl, null);
                checkPlayerDie(pl);
            } else {
                goToPlayer();
            }
        } catch (Exception ex) {
            Log.error(BossYardart.class, ex);
        }
    }

    @Override
    public void checkPlayerDie(Player pl) {
        if (pl != null && pl.isDie()) {
            Service.getInstance().chat(this,
                    "Bí thuật này không thể truyền cho kẻ yếu đuối như ngươi đâu " + pl.name + " à!");
            plAttack = null;
        }
    }

    private void moveNearPlayer(Player pl) {
        if (Util.isTrue(15, ConstRatio.PER100)) {
            int offsetX = Util.getOne(-1, 1)
                    * (SkillUtil.isUseSkillChuong(this) ? Util.nextInt(20, 80) : Util.nextInt(10, 30));
            int offsetY = Util.nextInt(10) % 2 == 0 ? 0 : -Util.nextInt(0, 50);
            goToXY(pl.location.x + offsetX, pl.location.y + offsetY, false);
        }
    }

    private void effectCharger() {
        if (Util.isTrue(15, ConstRatio.PER100)) {
            EffectSkillService.gI().sendEffectCharge(this);
        }
    }

    private boolean charge() {
        if (effectSkill.isCharging && Util.isTrue(50, 100)) {
            effectSkill.isCharging = false;
            return false;
        }
        if (Util.isTrue(1, 20) && nPoint.getCurrPercentHP() < 100) {
            for (Skill skill : playerSkill.skills) {
                if (skill.template.id == Skill.TAI_TAO_NANG_LUONG && SkillService.gI().canUseSkillWithCooldown(this)) {
                    playerSkill.skillSelect = skill;
                    return SkillService.gI().useSkill(this, null, null);
                }
            }
        }
        return false;
    }

    private void goToPlayer() {
        if (Util.isTrue(1, 10))
            return;

        dir = (location.x >= xMoveMax) ? -1 : (location.x <= xMoveMin) ? 1 : dir;
        int x = location.x + Util.nextInt(20, 40) * dir;
        int y = zone.map.yPhysicInTop(x, Math.max(location.y - 50, 0));
        goToXY(x, y, false);
    }

    @Override
    public Player getPlayerAttack() {
        if (countChangePlayerAttack < targetCountChangePlayerAttack && plAttack != null
                && plAttack.zone != null && plAttack.zone.equals(zone)
                && !plAttack.isDie() && !plAttack.effectSkin.isVoHinh && !plAttack.isMiniPet) {
            countChangePlayerAttack++;
            return plAttack;
        }
        targetCountChangePlayerAttack = Util.nextInt(10, 20);
        countChangePlayerAttack = 0;
        plAttack = zone.getRandomPlayerInMap();
        return (plAttack != null && !plAttack.effectSkin.isVoHinh) ? plAttack : null;
    }

    @Override
    public void joinMap() {
        if (zone != null) {
            ChangeMapService.gI().changeMap(this, zone, xMoveMin, zone.map.yPhysicInTop(xMoveMin, 5));
        }
    }

    @Override
    public void leaveMap() {
        MapService.gI().exitMap(this);
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }

    @Override
    public void idle() {
    }

    @Override
    public void initTalk() {
        this.textTalkBefore = new String[] { "Ngươi muốn học phép dịch chuyển tới thời ư?" };
        this.textTalkMidle = new String[] { "Tập nữa, tập mãi, tập mãi mãi!",
                "Ta sẽ dậy cho con công phu dịch chuyển" };
        this.textTalkAfter = new String[] { "Bí thuật này không thể truyền cho kẻ không có lòng kiên trì như người" };
    }
}