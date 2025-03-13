package com.nro.nro_online.models.boss.cold;

import nro.models.boss.*;
import nro.models.map.ItemMap;
import nro.models.player.Player;
import nro.services.Service;
import nro.utils.Util;

/**
 * Arriety
 */
public class Cooler2 extends Boss {

    public Cooler2() {
        super(BossFactory.COOLER2, BossData.COOLER2);
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }

    @Override
    public int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        if (this.isDie()) {
            return 0;
        } else {
            if (!piercing && Util.isTrue(25, 100)) {
                this.chat("Xí hụt lêu lêu");
                return 0;
            }
            if (this.isDie()) {
                rewards(plAtt);
            }
            return super.injured(plAtt, damage, piercing, isMobAttack);
        }
    }

    @Override
    public void rewards(Player plKill) {
        if (Util.isTrue(15, 100)) {
            if (Util.isTrue(1, 5)) {
                Service.getInstance().dropItemMap(this.zone, Util.ratiItem(zone, 561, 1, this.location.x, this.location.y, plKill.id));
            } else {
                Service.getInstance().dropItemMap(this.zone, new ItemMap(zone, 457, 1, this.location.x, zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id));
            }
        } else {
            int hlt = Util.nextInt(3, 5);
            for (int i = 0; i < hlt; i++) {
                ItemMap hon = new ItemMap(zone, 2029, 1, 10 * i + this.location.x, zone.map.yPhysicInTop(this.location.x, 0), -1);
                Service.getInstance().dropItemMap(this.zone, hon);
            }
        }
    }

    @Override
    public void idle() {
    }

    @Override
    public void checkPlayerDie(Player pl) {

    }

    @Override
    public void initTalk() {

        textTalkAfter = new String[]{"Ta đã giấu hết ngọc rồng rồi, các ngươi tìm vô ích hahaha"};
    }

    @Override
    public void leaveMap() {
        BossManager.gI().getBossById(BossFactory.COOLER).setJustRest();
        super.leaveMap();
        BossManager.gI().removeBoss(this);
    }
}
