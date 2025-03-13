/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nro.nro_online.models.map;

import java.util.ArrayList;
import java.util.List;

import nro.models.boss.Boss;
import nro.models.boss.BossFactory;
import nro.models.boss.Yardart.ChienBinh;
import nro.models.boss.Yardart.DoiTruong;
import nro.models.boss.Yardart.TanBinh;
import nro.models.boss.Yardart.TapSu;

/**
 *
 * @author Administrator
 */
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
        Boss boss = null;
        for (Zone zone : map.zones) {
            try {
                switch (map.mapId) {
                    case 131:
                        boss = new TapSu(zone, BossFactory.TAP_SU_1, 165, 245);
                        boss = new TapSu(zone, BossFactory.TAP_SU_2, 375, 445);
                        boss = new TapSu(zone, BossFactory.TAP_SU_3, 585, 650);
                        boss = new TapSu(zone, BossFactory.TAP_SU_4, 790, 850);
                        boss = new TapSu(zone, BossFactory.TAP_SU_5, 995, 1090);
                        boss = new TanBinh(zone, BossFactory.TAN_BINH_1, 1200, 1260);
                        break;
                    case 132:
                        boss = new TanBinh(zone, BossFactory.TAN_BINH_2, 170, 240);
                        boss = new TanBinh(zone, BossFactory.TAN_BINH_3, 375, 445);
                        boss = new TanBinh(zone, BossFactory.TAN_BINH_4, 587, 752);
                        boss = new TanBinh(zone, BossFactory.TAN_BINH_5, 770, 853);
                        boss = new TanBinh(zone, BossFactory.TAN_BINH_6, 995, 1080);
                        boss = new ChienBinh(zone, BossFactory.CHIEN_BINH_1, 1189, 1285);
                        break;
                    case 133:
                        boss = new ChienBinh(zone, BossFactory.CHIEN_BINH_2, 179, 239);
                        boss = new ChienBinh(zone, BossFactory.CHIEN_BINH_3, 374, 450);
                        boss = new ChienBinh(zone, BossFactory.CHIEN_BINH_4, 584, 654);
                        boss = new ChienBinh(zone, BossFactory.CHIEN_BINH_5, 784, 859);
                        boss = new ChienBinh(zone, BossFactory.CHIEN_BINH_6, 994, 1060);
                        boss = new DoiTruong(zone, BossFactory.DOI_TRUONG_1, 1205, 1275);
                        break;
                }

            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}
