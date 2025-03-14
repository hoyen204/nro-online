package com.nro.nro_online.models.boss.dhvt;

import com.nro.nro_online.models.boss.BossData;
import com.nro.nro_online.models.boss.BossFactory;
import com.nro.nro_online.models.player.Player;

public class ChaPa extends BossDHVT {

    public ChaPa(Player player) {
        super(BossFactory.CHA_PA, BossData.CHA_PA);
        this.playerAtt = player;
    }
}