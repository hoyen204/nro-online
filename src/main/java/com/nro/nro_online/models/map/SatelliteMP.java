/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nro.nro_online.models.map;

import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.PlayerService;
import com.nro.nro_online.utils.Util;

/**
 *
 * @Build by Arriety
 */
public class SatelliteMP extends Satellite {

    public SatelliteMP(Zone zone, int itemID, int x, int y, Player player) {
        super(zone, itemID, x, y, player);
        this.delayBuff = 30000;
    }

    @Override
    public void buff(Player pl) {
        int r = Util.getDistance(pl.location.x, pl.location.y, x, y);
        if (r <= range) {
            PlayerService.gI().hoiPhuc(pl, 0, pl.nPoint.mpMax * 5 / 100);
        }
    }

}
