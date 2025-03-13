package com.nro.nro_online.models.boss.dhvt;

import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.player.Player;

/**
 * @build by arriety
 */
public class Yamcha extends BossDHVT {

    public Yamcha(Player player) {
        super(BossFactory.YAMCHA, BossData.YAMCHA);
        this.playerAtt = player;
    }
}