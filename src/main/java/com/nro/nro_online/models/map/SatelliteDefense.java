/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nro.nro_online.models.map;

import com.nro.nro_online.models.player.NPoint;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.utils.Util;

/**
 *
 * @Build by Arriety
 */
public class SatelliteDefense extends Satellite {

    public SatelliteDefense(Zone zone, int itemID, int x, int y, Player player) {
        super(zone, itemID, x, y, player);
        this.delayBuff = 1000;
    }

    @Override
    public void buff(Player pl) {
        int r = Util.getDistance(pl.location.x, pl.location.y, x, y);
        NPoint n = pl.nPoint;
        if (n != null) {
            if (r <= range) {
                n.buffDefenseSatellite = true;
            } else {
                n.buffDefenseSatellite = false;
            }
        }
    }

}
