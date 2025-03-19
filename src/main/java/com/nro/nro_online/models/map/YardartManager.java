package com.nro.nro_online.models.map;

import java.util.ArrayList;
import java.util.List;

import com.nro.nro_online.models.boss.BossFactory;
import com.nro.nro_online.models.boss.Yardart.ChienBinh;
import com.nro.nro_online.models.boss.Yardart.DoiTruong;
import com.nro.nro_online.models.boss.Yardart.TanBinh;
import com.nro.nro_online.models.boss.Yardart.TapSu;

public class YardartManager {

    private static YardartManager I;
    private final List<Map> maps;

    public static YardartManager gI() {
        if (I == null) {
            I = new YardartManager();
        }
        return I;
    }

    private YardartManager() {
        this.maps = new ArrayList<>();
    }

    public void addMap(Map map) {
        this.maps.add(map);
        initBoss(map);
    }

    private void initBoss(Map map) {
        for (Zone zone : map.zones) {
            try {
                switch (map.mapId) {
                    case 131:
                        new TapSu(zone, BossFactory.TAP_SU_1, 165, 245);
                        new TapSu(zone, BossFactory.TAP_SU_2, 375, 445);
                        new TapSu(zone, BossFactory.TAP_SU_3, 585, 650);
                        new TapSu(zone, BossFactory.TAP_SU_4, 790, 850);
                        new TapSu(zone, BossFactory.TAP_SU_5, 995, 1090);
                        new TanBinh(zone, BossFactory.TAN_BINH_1, 1200, 1260);
                        break;
                    case 132:
                        new TanBinh(zone, BossFactory.TAN_BINH_2, 170, 240);
                        new TanBinh(zone, BossFactory.TAN_BINH_3, 375, 445);
                        new TanBinh(zone, BossFactory.TAN_BINH_4, 587, 752);
                        new TanBinh(zone, BossFactory.TAN_BINH_5, 770, 853);
                        new TanBinh(zone, BossFactory.TAN_BINH_6, 995, 1080);
                        new ChienBinh(zone, BossFactory.CHIEN_BINH_1, 1189, 1285);
                        break;
                    case 133:
                        new ChienBinh(zone, BossFactory.CHIEN_BINH_2, 179, 239);
                        new ChienBinh(zone, BossFactory.CHIEN_BINH_3, 374, 450);
                        new ChienBinh(zone, BossFactory.CHIEN_BINH_4, 584, 654);
                        new ChienBinh(zone, BossFactory.CHIEN_BINH_5, 784, 859);
                        new ChienBinh(zone, BossFactory.CHIEN_BINH_6, 994, 1060);
                        new DoiTruong(zone, BossFactory.DOI_TRUONG_1, 1205, 1275);
                        break;
                }

            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}
