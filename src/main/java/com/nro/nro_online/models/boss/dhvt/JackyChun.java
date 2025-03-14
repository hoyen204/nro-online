package com.nro.nro_online.models.boss.dhvt;

import com.nro.nro_online.models.boss.BossData;
import com.nro.nro_online.models.boss.BossFactory;
import com.nro.nro_online.models.player.Player;

public class JackyChun extends BossDHVT {

    public JackyChun(Player player) {
        super(BossFactory.JACKY_CHUN, BossData.JACKY_CHUN);
        this.playerAtt = player;
    }
}