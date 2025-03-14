package com.nro.nro_online.models.boss.dhvt;

import com.nro.nro_online.models.boss.BossData;
import com.nro.nro_online.models.boss.BossFactory;
import com.nro.nro_online.models.player.Player;

public class ChanXu extends BossDHVT {

    public ChanXu(Player player) {
        super(BossFactory.CHAN_XU, BossData.CHAN_XU);
        this.playerAtt = player;
    }
}