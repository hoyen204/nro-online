package com.nro.nro_online.models.boss.dhvt;

import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.player.Player;

/**
 * @build by arriety
 */
public class TauPayPay extends BossDHVT {

    public TauPayPay(Player player) {
        super(BossFactory.TAU_PAY_PAY, BossData.TAU_PAY_PAY);
        this.playerAtt = player;
    }
}