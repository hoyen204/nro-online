package com.nro.nro_online.models.boss.dhvt;

import com.nro.nro_online.models.boss.BossData;
import com.nro.nro_online.models.boss.BossFactory;
import com.nro.nro_online.models.player.Player;

public class LiuLiu extends BossDHVT {

    public LiuLiu(Player player) {
        super(BossFactory.LIU_LIU, BossData.LIU_LIU);
        this.playerAtt = player;
    }
}