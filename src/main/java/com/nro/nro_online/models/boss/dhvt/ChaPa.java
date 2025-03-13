package com.nro.nro_online.models.boss.dhvt;

import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.player.Player;

/**
 * @build by arriety
 */
public class ChaPa extends BossDHVT {

    public ChaPa(Player player) {
        super(BossFactory.CHA_PA, BossData.CHA_PA);
        this.playerAtt = player;
    }
}