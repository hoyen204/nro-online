package com.nro.nro_online.models.boss;

import java.util.ArrayList;
import java.util.List;

import nro.models.Part;
import nro.models.PartManager;
import nro.models.map.Zone;
import nro.models.player.Player;
import nro.server.io.Message;
import nro.services.MapService;
import nro.services.Service;
import nro.services.func.ChangeMapService;
import nro.utils.Log;

/**
 *
 * @stole Arriety
 *
 */
public class BossManager {

    private static final List<Boss> BOSSES_IN_GAME;

    private static BossManager intance;

    static {
        BOSSES_IN_GAME = new ArrayList<>();
    }

    public void updateAllBoss() {
        for (int i = BOSSES_IN_GAME.size() - 1; i >= 0; i--) {
            try {
                Boss boss = BOSSES_IN_GAME.get(i);
                if (boss != null) {
                    boss.update();
                }
            } catch (Exception e) {
                Log.error(BossManager.class, e);
            }
        }
    }

    public void FindBoss(Player player, int id) {
        Boss boss = BossManager.gI().getBossById(id);
        if (boss != null && boss.zone != null && boss.zone.map != null && !boss.isDie()) {
            Zone z = MapService.gI().getMapCanJoin(player, boss.zone.map.mapId, boss.zone.zoneId);
            if (z.getNumOfPlayers() < z.maxPlayer) {
                ChangeMapService.gI().changeMap(player, boss.zone, boss.location.x, boss.location.y);
            } else {
                Service.getInstance().sendThongBao(player, "Khu vực đang full.");
            }
        } else {
            Service.getInstance().sendThongBao(player, "");
        }
    }

    public void showListBoss(Player player) {
        Message msg;
        try {
            long aliveBossCount = BOSSES_IN_GAME.stream().filter(boss -> boss != null).filter(boss -> !BossFactory.isYar((byte) boss.id)).count();
            int count = (int) (aliveBossCount > Byte.MAX_VALUE ? Byte.MAX_VALUE : aliveBossCount);
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Boss (SL: " + count + ")");

            msg.writer().writeByte(count);
            for (int i = 0; i < BOSSES_IN_GAME.size(); i++) {
                Boss boss = BossManager.BOSSES_IN_GAME.get(i);
                if (BossFactory.isYar((byte) boss.id)) {
                    continue;
                }
                msg.writer().writeInt((int) boss.id);
                msg.writer().writeInt((int) boss.id);
                msg.writer().writeShort(boss.getHead());
                if (player.isVersionAbove(220)) {
                    Part part = PartManager.getInstance().find(boss.getHead());
                    msg.writer().writeShort(part.getIcon(0));
                }
                msg.writer().writeShort(boss.getBody());
                msg.writer().writeShort(boss.getLeg());
                msg.writer().writeUTF(boss.name);
                if (boss != null && boss.zone != null) {
                    msg.writer().writeUTF("Sống");
                    msg.writer().writeUTF(boss.zone.map.mapName + "(" + boss.zone.map.mapId + ") khu " + boss.zone.zoneId);
                } else {
                    msg.writer().writeUTF("Chết");
                    msg.writer().writeUTF("Chết rồi");
                }
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static BossManager gI() {
        if (intance == null) {
            intance = new BossManager();
        }
        return intance;
    }

    public Boss getBossById(int bossId) {
        for (int i = BOSSES_IN_GAME.size() - 1; i >= 0; i--) {
            if (BOSSES_IN_GAME.get(i).id == bossId) {
                return BOSSES_IN_GAME.get(i);
            }
        }
        return null;
    }

    public void addBoss(Boss boss) {
        boolean have = false;
        for (Boss b : BOSSES_IN_GAME) {
            if (boss.equals(b)) {
                have = true;
                break;
            }
        }
        if (!have) {
            BOSSES_IN_GAME.add(boss);
        }
    }

    public void removeBoss(Boss boss) {
        BOSSES_IN_GAME.remove(boss);
        boss.dispose();
    }
}
