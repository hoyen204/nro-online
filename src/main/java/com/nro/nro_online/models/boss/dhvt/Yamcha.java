package com.nro.nro_online.models.boss.dhvt;

import com.nro.nro_online.models.boss.BossData;
import com.nro.nro_online.models.boss.BossFactory;
import com.nro.nro_online.models.player.Player;

public class Yamcha extends BossDHVT {

    public Yamcha(Player player) {
        super(BossFactory.YAMCHA, BossData.YAMCHA);
        this.playerAtt = player;
    }
}