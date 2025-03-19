package com.nro.nro_online.models.boss.bill;

import com.nro.nro_online.models.boss.BossData;
import com.nro.nro_online.models.boss.BossFactory;
import com.nro.nro_online.models.boss.FutureBoss;
import com.nro.nro_online.models.player.Player;

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
        BossFactory.createBoss(BossFactory.WHIS);
        this.setJustRestToFuture();
        super.leaveMap();
    }
}
