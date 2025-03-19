package com.nro.nro_online.models.boss.Yardart;

import com.nro.nro_online.models.boss.BossData;
import com.nro.nro_online.models.map.Zone;

public class ChienBinh extends BossYardart {

    public ChienBinh(Zone zoneDefault, byte bossId, int xMoveMin, int xMoveMax) throws Exception {
        super(zoneDefault, bossId, BossData.CHIEN_BINH, xMoveMin, xMoveMax);
    }
}
