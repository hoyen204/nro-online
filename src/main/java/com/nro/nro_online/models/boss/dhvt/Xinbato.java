package com.nro.nro_online.models.boss.dhvt;

import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.player.Player;

/**
 * @build by arriety
 */
public class Xinbato extends BossDHVT {

    public Xinbato(Player player) {
        super(BossFactory.XINBATO, BossData.XINBATO);
        this.playerAtt = player;
    }
}