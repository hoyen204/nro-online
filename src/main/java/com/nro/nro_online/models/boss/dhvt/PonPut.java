package com.nro.nro_online.models.boss.dhvt;

import com.nro.nro_online.models.boss.BossData;
import com.nro.nro_online.models.boss.BossFactory;
import com.nro.nro_online.models.player.Player;

public class PonPut extends BossDHVT {

    public PonPut(Player player) {
        super(BossFactory.PON_PUT, BossData.PON_PUT);
        this.playerAtt = player;
    }
}