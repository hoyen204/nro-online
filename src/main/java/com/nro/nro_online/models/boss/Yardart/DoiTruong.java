/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nro.nro_online.models.boss.Yardart;

import nro.models.boss.BossData;
import nro.models.map.Zone;

/**
 *
 * @author Administrator
 */
public class DoiTruong extends BossYardart {

    public DoiTruong(Zone zoneDefault, byte bossId, int xMoveMin, int xMoveMax) throws Exception {
        super(zoneDefault, bossId, BossData.DOI_TRUONG, xMoveMin, xMoveMax);
    }
}
