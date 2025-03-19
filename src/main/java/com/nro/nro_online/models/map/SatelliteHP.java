package com.nro.nro_online.models.map;

import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.PlayerService;
import com.nro.nro_online.utils.Util;

public class SatelliteHP extends Satellite {

    public SatelliteHP(Zone zone, int itemID, int x, int y, Player player) {
        super(zone, itemID, x, y, player);
        this.delayBuff = 30000;
    }

    @Override
    public void buff(Player pl) {
        int r = Util.getDistance(pl.location.x, pl.location.y, x, y);
        if (r <= range) {
            PlayerService.gI().hoiPhuc(pl, pl.nPoint.hpMax * 5 / 100, 0);
        }
    }
}
