package com.nro.nro_online.models.boss.dhvt;

import com.nro.nro_online.models.boss.BossData;
import com.nro.nro_online.models.boss.BossFactory;
import com.nro.nro_online.models.player.Player;

public class TauPayPay extends BossDHVT {

    public TauPayPay(Player player) {
        super(BossFactory.TAU_PAY_PAY, BossData.TAU_PAY_PAY);
        this.playerAtt = player;
    }
}