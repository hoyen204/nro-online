package com.nro.nro_online.models.boss.dhvt;

import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.player.Player;

/**
 * @build by arriety
 */
public class PonPut extends BossDHVT {

    public PonPut(Player player) {
        super(BossFactory.PON_PUT, BossData.PON_PUT);
        this.playerAtt = player;
    }
}