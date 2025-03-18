package com.nro.nro_online.services;

import com.nro.nro_online.models.map.Zone;
import com.nro.nro_online.models.map.phoban.DoanhTrai;
import com.nro.nro_online.models.mob.Mob;
import com.nro.nro_online.models.player.Player;

public class DoanhTraiService {
    private static volatile DoanhTraiService instance;

    private DoanhTraiService() {
    }

    public static DoanhTraiService gI() {
        if (instance == null) {
            synchronized (DoanhTraiService.class) {
                if (instance == null) {
                    instance = new DoanhTraiService();
                }
            }
        }
        return instance;
    }

    public Zone getMapDoanhTrai(Player player, int mapId) {
        if (player == null || player.zone == null)
            return null;

        if (MapService.gI().isMapDoanhTrai(player.zone.map.mapId) && !player.isAdmin() && !canJoin(player.zone)) {
            return null;
        }

        return (player.clan != null && player.clan.doanhTrai != null)
                ? player.clan.doanhTrai.zones.stream()
                .filter(z -> z.map.mapId == mapId)
                .findFirst()
                .orElse(null)
                : null;
    }

    private boolean canJoin(Zone zone) {
        for (Mob mob : zone.mobs) {
            if (!mob.isDie())
                return false;
        }
        return zone.getBosses().stream().allMatch(Player::isDie);
    }

    public void openDoanhTrai(Player player) {
        if (player == null || player.clan == null || player.clan.doanhTrai != null || player.clan.haveGoneDoanhTrai) {
            Service.getInstance().sendThongBao(player, "Không thể thực hiện");
            return;
        }

        DoanhTrai doanhTrai = DoanhTrai.DOANH_TRAIS.stream()
                .filter(dt -> !dt.isOpened)
                .findFirst()
                .orElse(null);

        if (doanhTrai != null) {
            doanhTrai.openDoanhTrai(player, player.clan);
        } else {
            Service.getInstance().sendThongBao(player, "Doanh trại đã đầy, vui lòng quay lại sau");
        }
    }
}