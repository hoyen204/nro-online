package com.nro.nro_online.models.boss.hell;

import com.nro.nro_online.models.boss.Boss;
import com.nro.nro_online.models.boss.BossData;
import com.nro.nro_online.models.boss.BossFactory;
import com.nro.nro_online.models.item.Item;
import com.nro.nro_online.models.map.mabu.MabuWar;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.server.Client;
import com.nro.nro_online.services.*;
import com.nro.nro_online.utils.SkillUtil;
import com.nro.nro_online.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class SatanKing extends Boss {

    private static final int MAX_DAMAGE = 10_000_000;
    private static final int REWARD_ITEM_ID = 2062;
    private static final int REWARD_QUANTITY = 10;

    private final List<Long> playerAttack;

    public SatanKing() {
        super(BossFactory.SATAN_KING, BossData.SATAN_KING);
        this.playerAttack = new ArrayList<>();
    }

    protected SatanKing(byte id, BossData bossData) {
        super(id, bossData);
        this.playerAttack = new ArrayList<>();
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }

    @Override
    public void rewards(Player plKill) {
        playerAttack.forEach(id -> {
            Player player = Client.gI().getPlayer(id);
            if (player != null) {
                Item reward = ItemService.gI().createNewItem((short) REWARD_ITEM_ID);
                reward.quantity = REWARD_QUANTITY;
                InventoryService.gI().addItemBag(player, reward, 999);
                InventoryService.gI().sendItemBags(player);
                Service.getInstance().sendThongBao(player, "Bạn nhận được " + reward.template.name);
            }
        });
        playerAttack.clear();
    }

    @Override
    public void initTalk() {
        this.textTalkAfter = new String[] { "Các ngươi chờ đấy, ta sẽ quay lại sau" };
    }

    @Override
    public void idle() {
        if (this.countIdle >= this.maxIdle) {
            this.maxIdle = Util.nextInt(0, 3);
            this.countIdle = 0;
            this.changeAttack();
        } else {
            this.countIdle++;
        }
    }

    @Override
    public void checkPlayerDie(Player pl) {
        if (pl != null && pl.isDie()) {
            Service.getInstance().chat(this, "Chừa nha " + pl.name + " động vào ta chỉ có chết.");
            this.plAttack = null;
        }
    }

    @Override
    public int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        if (plAtt == null || this.isMiniPet)
            return 0;
        if (!playerAttack.contains(plAtt.id))
            playerAttack.add(plAtt.id);

        if (this.isDie())
            return 0;

        damage = adjustDamage(plAtt, damage, piercing, isMobAttack);
        if (damage <= 0)
            return 0;

        this.nPoint.subHP(damage);
        handlePostDamage(plAtt, damage);
        return damage;
    }

    private int adjustDamage(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        int mstChuong = this.nPoint.mstChuong;
        int giamst = this.nPoint.tlGiamst;

        if (SkillUtil.isUseSkillChuong(plAtt)) {
            if (plAtt.nPoint.xDameChuong && !this.isBoss) {
                damage = plAtt.nPoint.tlDameChuong * damage;
                plAtt.nPoint.xDameChuong = false;
            }
            if (mstChuong > 0) {
                PlayerService.gI().hoiPhuc(this, 0, damage * mstChuong / 100);
                return 0;
            }
        }

        if (!piercing && !SkillUtil.isUseSkillBoom(plAtt) && Util.isTrue(this.nPoint.tlNeDon, 100)) {
            return 0;
        }

        if (isMobAttack && (this.charms.tdBatTu > System.currentTimeMillis() || this.itemTime.isMaTroi)
                && damage >= this.nPoint.hp) {
            damage = this.nPoint.hp - 1;
        }

        damage = this.nPoint.subDameInjureWithDeff(damage);
        if (!piercing && effectSkill.isShielding) {
            if (damage > nPoint.hpMax)
                EffectSkillService.gI().breakShield(this);
            damage = 1;
        }

        if (giamst > 0)
            damage -= nPoint.calPercent(damage, giamst);
        if (this.effectSkill.isHoldMabu)
            damage = 1;
        if (damage > MAX_DAMAGE)
            damage = MAX_DAMAGE;
        if (plAtt.getSession() != null && plAtt.isAdmin())
            damage = this.nPoint.hpMax / 3;

        return damage;
    }

    private void handlePostDamage(Player plAtt, int damage) {
        if (this.effectSkill.isHoldMabu && Util.isTrue(30, 150)) {
            Service.getInstance().removeMabuEat(this);
        }
        if (isDie() && plAtt != null && plAtt.zone != null) {
            if (MapService.gI().isMapMabuWar(plAtt.zone.map.mapId) && MabuWar.gI().isTimeMabuWar()) {
                plAtt.addPowerPoint(5);
                Service.getInstance().sendPowerInfo(plAtt, "TL", plAtt.getPowerPoint());
            }
            setDie(plAtt);
            rewards(plAtt);
            notifyPlayeKill(plAtt);
            die();
        }
    }
}