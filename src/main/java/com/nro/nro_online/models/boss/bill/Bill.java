package com.nro.nro_online.models.boss.bill;

import nro.models.boss.Boss;
import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.boss.FutureBoss;
import nro.models.player.Player;

/**
 * @author Arriety
 */
public class Bill extends FutureBoss {

    public Bill() {
        super(BossFactory.BILL, BossData.BILL);
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }

    @Override
    public void idle() {
    }

    @Override
    public void checkPlayerDie(Player pl) {
    }

    @Override
    public void initTalk() {
    }

    @Override
    public void rewards(Player pl) {
        if (pl != null) {
            this.dropItemReward(2040, (int) pl.id);
            generalRewards(pl);
        }
    }

    @Override
    public void leaveMap() {
        Boss whis = BossFactory.createBoss(BossFactory.WHIS);
        this.setJustRestToFuture();
        super.leaveMap();
    }
}
