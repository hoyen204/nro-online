/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nro.nro_online.models.boss.Yardart;

import com.nro.nro_online.models.boss.BossData;
import com.nro.nro_online.models.map.Zone;

/**
 *
 * @author Arriety
 */
public class TanBinh extends BossYardart {

    public TanBinh(Zone zoneDefault, byte bossId, int xMoveMin, int xMoveMax) throws Exception {
        super(zoneDefault, bossId, BossData.PUT_NGU, xMoveMin, xMoveMax);
    }
}
