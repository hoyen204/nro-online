package com.nro.nro_online.models.boss.Yardart;

import nro.consts.ConstRatio;
import nro.models.boss.Boss;
import nro.models.boss.BossData;
import nro.models.item.ItemOption;
import nro.models.map.ItemMap;
import nro.models.map.Zone;
import nro.models.player.Pet;
import nro.models.player.Player;
import nro.models.skill.Skill;
import nro.services.EffectSkillService;
import nro.services.MapService;
import nro.services.Service;
import nro.services.SkillService;
import nro.services.func.ChangeMapService;
import nro.utils.Log;
import nro.utils.SkillUtil;
import nro.utils.Util;

/**
 *
 * @author Arriety
 */
public class BossYardart extends Boss {

    private int xMoveMin;
    private int xMoveMax;
    private int rangeAttack;
    private int dir = 1;

    public BossYardart(Zone zoneDefault, byte bossId, BossData data, int xMoveMin, int xMoveMax) throws Exception {
        super(bossId, data);
        this.zone = zoneDefault;
        this.zone = zoneDefault;
        this.xMoveMin = xMoveMin;
        this.xMoveMax = xMoveMax;
        this.location.x = this.xMoveMin;
        this.location.y = this.zone.map.yPhysicInTop(this.location.x, 5);
//        this.timeUpdateActive = 250;
        this.rangeAttack = 200;
    }

    @Override
    public void joinMapByZone(Zone zone, int x) {
        ChangeMapService.gI().changeMap((Player) this, zone, this.xMoveMin + (this.xMoveMax - this.xMoveMin) / 2, this.location.y);
    }
    protected long lastTimeDropItem;

    @Override
    public synchronized int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        if (isDie()) {
            return 0;
        }
        if (!piercing && Util.isTrue(0, 100)) {
            chat("Xí hụt");
            return 0;
        }
        if (damage > this.nPoint.hpg / 200) {
            damage = this.nPoint.hpg / 200;
        }
        if (System.currentTimeMillis() - lastTimeDropItem > 5000) {
            rewards(plAtt);
            lastTimeDropItem = System.currentTimeMillis();
        }

        this.nPoint.subHP(damage);
        if (this.isDie()) {
            rewards(plAtt);
            die();
        }
        return damage;
    }

    @Override
    public void rewards(Player plKill) {
        if (plKill == null || Util.isTrue(55, 100)) {
            return;
        }
        for (int i = 0; i < 2; i++) {
            ItemMap item = new ItemMap(this.zone, 590, 1, (plKill.isPet ? ((Pet) plKill).master.location.x : plKill.location.x) + Util.nextInt(-20, 20), this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), (int) plKill.id);
            item.options.add(new ItemOption(30, 0));
            Service.getInstance().dropItemMap(this.zone, item);
        }
    }

    @Override
    public void attack() {
        try {
            if (!charge()) {
                Player pl = getPlayerAttack();
                if (pl != null && !pl.isDie() && !pl.isMiniPet) {
                    this.playerSkill.skillSelect = this.getSkillAttack();
                    if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                        if (Util.isTrue(15, ConstRatio.PER100)) {
                            if (SkillUtil.isUseSkillChuong(this)) {
                                goToXY(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 80)),
                                        Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 50), false);
                            } else {
                                goToXY(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(10, 30)),
                                        Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 50), false);
                            }
                        }
                        this.effectCharger();
                        SkillService.gI().useSkill(this, pl, null);
                        checkPlayerDie(pl);
                    } else {
                        goToPlayer();
                    }
                }
            }

        } catch (Exception ex) {
            Log.error(Boss.class, ex);
        }
    }

    @Override
    public void checkPlayerDie(Player pl) {
        if (pl.isDie()) {
            Service.getInstance().chat(this, "Bí thuật này không thể truyền cho kẻ yếu đuối như ngươi đâu " + plAttack.name + " à!");
            this.plAttack = null;
        }
    }

    protected void effectCharger() {
        if (Util.isTrue(15, ConstRatio.PER100)) {
            EffectSkillService.gI().sendEffectCharge(this);
        }
    }

    protected boolean charge() {
        if (this.effectSkill.isCharging && Util.isTrue(50, 100)) {
            this.effectSkill.isCharging = false;
            return false;
        }
        if (Util.isTrue(1, 20)) {
            for (Skill skill : this.playerSkill.skills) {
                if (skill.template.id == Skill.TAI_TAO_NANG_LUONG) {
                    this.playerSkill.skillSelect = skill;
                    if (this.nPoint.getCurrPercentHP() < 100 && SkillService.gI().canUseSkillWithCooldown(this)
                            && SkillService.gI().useSkill(this, null, null)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void goToPlayer() {
        if (Util.isTrue(1, 10)) {
            return;
        }
        if (this.dir == 1) {
            if (this.location.x >= this.xMoveMax) {
                this.dir = -1;
            }
        } else if (this.dir == -1
                && this.location.x <= this.xMoveMin) {
            this.dir = 1;
        }

        int x = this.location.x + Util.nextInt(20, 40) * this.dir;
        int yGetPhysic = this.location.y - 50;
        if (yGetPhysic < 0) {
            yGetPhysic = 0;
        }
        int y = this.zone.map.yPhysicInTop(x, yGetPhysic);
        goToXY(x, y, false);
    }

    @Override
    public Player getPlayerAttack() {
        if (countChangePlayerAttack < targetCountChangePlayerAttack
                && plAttack != null && plAttack.zone != null
                && plAttack.zone.equals(this.zone)) {
            if (!plAttack.isDie() && !plAttack.effectSkin.isVoHinh && !plAttack.isMiniPet) {
                this.countChangePlayerAttack++;
                return plAttack;
            } else {
                plAttack = null;
            }
        } else {
            this.targetCountChangePlayerAttack = Util.nextInt(10, 20);
            this.countChangePlayerAttack = 0;
            plAttack = this.zone.getRandomPlayerInMap();
            if (plAttack != null && plAttack.effectSkin.isVoHinh) {
                plAttack = null;
            }
        }
        return plAttack;
    }

    @Override
    public void leaveMap() {
        MapService.gI().exitMap(this);
    }

    @Override
    public void joinMap() {
        if (zone != null) {
            ChangeMapService.gI().changeMap(this, this.zone, this.xMoveMin, this.zone.map.yPhysicInTop(this.xMoveMin, 5));
        }
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
        this.textTalkBefore = new String[]{"Ngươi muốn học phép dịch chuyển tới thời ư?"};
        this.textTalkMidle = new String[]{"Tập nữa, tập mãi, tập mãi mãi!", "Ta sẽ dậy cho con công phu dịch chuyển"};
        this.textTalkAfter = new String[]{"Bí thuật này không thể truyền cho kẻ không có lòng kiên trì như người"};
    }

}
