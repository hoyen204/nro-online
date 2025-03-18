package com.nro.nro_online.models.boss;

import com.nro.nro_online.models.Part;
import com.nro.nro_online.models.PartManager;
import com.nro.nro_online.models.map.Zone;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.server.io.Message;
import com.nro.nro_online.services.MapService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.func.ChangeMapService;
import com.nro.nro_online.utils.Log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BossManager {
    private static final List<Boss> BOSSES_IN_GAME = new CopyOnWriteArrayList<>();
    private static volatile BossManager instance;

    private BossManager() {}

    public static BossManager gI() {
        if (instance == null) {
            synchronized (BossManager.class) {
                if (instance == null) {
                    instance = new BossManager();
                }
            }
        }
        return instance;
    }

    public void updateAllBoss() {
        BOSSES_IN_GAME.forEach(boss -> {
            try {
                if (boss != null) boss.update();
            } catch (Exception e) {
                Log.error(BossManager.class, e);
            }
        });
    }

    public void findBoss(Player player, int id) {
        Boss boss = getBossById(id);
        if (boss != null && boss.zone != null && boss.zone.map != null && !boss.isDie()) {
            Zone z = MapService.gI().getMapCanJoin(player, boss.zone.map.mapId, boss.zone.zoneId);
            if (z.getNumOfPlayers() < z.maxPlayer) {
                ChangeMapService.gI().changeMap(player, z, boss.location.x, boss.location.y);
            } else {
                Service.getInstance().sendThongBao(player, "Khu vực đang full.");
            }
        } else {
            Service.getInstance().sendThongBao(player, "");
        }
    }

    public void showListBoss(Player player) {
        try (Message msg = new Message(-96)) {
            int count = (int) Math.min(BOSSES_IN_GAME.stream()
                    .filter(boss -> boss != null && !BossFactory.isYar((byte) boss.id))
                    .count(), Byte.MAX_VALUE);

            msg.writer().writeByte(0);
            msg.writer().writeUTF("Boss (SL: " + count + ")");
            msg.writer().writeByte(count);

            BOSSES_IN_GAME.stream()
                    .filter(boss -> !BossFactory.isYar((byte) boss.id))
                    .limit(count)
                    .forEach(boss -> {
                        try {
                            msg.writer().writeInt((int) boss.id);
                            msg.writer().writeInt((int) boss.id);
                            msg.writer().writeShort(boss.getHead());
                            if (player.isVersionAbove(220)) {
                                msg.writer().writeShort(PartManager.getInstance().find(boss.getHead()).getIcon(0));
                            }
                            msg.writer().writeShort(boss.getBody());
                            msg.writer().writeShort(boss.getLeg());
                            msg.writer().writeUTF(boss.name);
                            msg.writer().writeUTF(boss.zone != null ? "Sống" : "Chết");
                            msg.writer().writeUTF(boss.zone != null
                                    ? boss.zone.map.mapName + "(" + boss.zone.map.mapId + ") khu " + boss.zone.zoneId
                                    : "Chết rồi");
                        } catch (Exception e) {
                            Log.error(BossManager.class, e);
                        }
                    });

            player.sendMessage(msg);
        } catch (Exception e) {
            Log.error(BossManager.class, e);
        }
    }

    public Boss getBossById(int bossId) {
        return BOSSES_IN_GAME.stream()
                .filter(boss -> boss.id == bossId)
                .findFirst()
                .orElse(null);
    }

    public void addBoss(Boss boss) {
        if (!BOSSES_IN_GAME.contains(boss)) {
            BOSSES_IN_GAME.add(boss);
        }
    }

    public void removeBoss(Boss boss) {
        if (BOSSES_IN_GAME.remove(boss)) {
            boss.dispose();
        }
    }
}