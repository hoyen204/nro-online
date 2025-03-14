package com.nro.nro_online.models.boss.dhvt;

import com.nro.nro_online.models.boss.BossData;
import com.nro.nro_online.models.boss.BossFactory;
import com.nro.nro_online.models.player.Player;

public class ODo extends BossDHVT {

    public ODo(Player player) {
        super(BossFactory.O_DO, BossData.O_DO);
        this.playerAtt = player;
    }
}
