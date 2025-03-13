package com.nro.nro_online.models.boss.dhvt;

import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.player.Player;

/**
 * @build by arriety
 */
public class ODo extends BossDHVT {

    public ODo(Player player) {
        super(BossFactory.O_DO, BossData.O_DO);
        this.playerAtt = player;
    }
}
