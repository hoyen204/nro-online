package com.nro.nro_online.models.boss.dhvt;

import com.nro.nro_online.models.boss.BossData;
import com.nro.nro_online.models.boss.BossFactory;
import com.nro.nro_online.models.player.Player;

public class Xinbato extends BossDHVT {

    public Xinbato(Player player) {
        super(BossFactory.XINBATO, BossData.XINBATO);
        this.playerAtt = player;
    }
}