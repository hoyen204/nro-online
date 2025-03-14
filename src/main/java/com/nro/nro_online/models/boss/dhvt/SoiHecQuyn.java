package com.nro.nro_online.models.boss.dhvt;

import com.nro.nro_online.models.boss.BossData;
import com.nro.nro_online.models.boss.BossFactory;
import com.nro.nro_online.models.player.Player;

public class SoiHecQuyn extends BossDHVT {
    public SoiHecQuyn(Player player) {
        super(BossFactory.SOI_HEC_QUYN, BossData.SOI_HEC_QUYN);
        this.playerAtt = player;
    }
}
