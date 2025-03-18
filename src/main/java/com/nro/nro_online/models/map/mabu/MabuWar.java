package com.nro.nro_online.models.map.mabu;

import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.models.boss.Boss;
import com.nro.nro_online.models.boss.BossFactory;
import com.nro.nro_online.models.map.Map;
import com.nro.nro_online.models.map.Zone;
import com.nro.nro_online.models.npc.Npc;
import com.nro.nro_online.models.npc.NpcManager;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.EffSkinService;
import com.nro.nro_online.services.MapService;
import com.nro.nro_online.services.NpcService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.func.ChangeMapService;
import com.nro.nro_online.utils.TimeUtil;
import com.nro.nro_online.utils.Util;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MabuWar {
    private static volatile MabuWar instance;
    private final List<Boss> bosses = Collections.synchronizedList(new ArrayList<>());
    private static LocalDateTime TIME_OPEN;
    private static LocalDateTime TIME_CLOSE;
    private static final byte HOUR_OPEN = 12;
    private static final byte MIN_OPEN = 0;
    private static final byte HOUR_CLOSE = 13;
    private static final byte MIN_CLOSE = 0;
    private int day = -1;
    private boolean initBoss;
    private boolean clearBoss;

    private MabuWar() {}

    public static MabuWar gI() {
        if (instance == null) {
            synchronized (MabuWar.class) {
                if (instance == null) {
                    instance = new MabuWar();
                }
            }
        }
        instance.setTime();
        return instance;
    }

    private void setTime() {
        int currentDay = TimeUtil.getCurrDay();
        if (day != currentDay) {
            day = currentDay;
            String today = TimeUtil.getTimeNow("dd/MM/yyyy");
            TIME_OPEN = TimeUtil.getTime(today + " " + HOUR_OPEN + ":" + MIN_OPEN + ":0", "dd/MM/yyyy HH:mm:ss");
            TIME_CLOSE = TimeUtil.getTime(today + " " + HOUR_CLOSE + ":" + MIN_CLOSE + ":0", "dd/MM/yyyy HH:mm:ss");
        }
    }

    public boolean isTimeMabuWar() {
        LocalDateTime now = LocalDateTime.now();
        return TIME_OPEN.isBefore(now) && TIME_CLOSE.isAfter(now);
    }

    public void update(Player player) {
        if (player == null || player.zone == null || !MapService.gI().isMapMabuWar(player.zone.map.mapId)) return;

        try {
            Zone zone = player.zone;
            if (isTimeMabuWar()) {
                if (!initBoss) {
                    BossFactory.initBossMabuWar();
                    initBoss = true;
                }

                if (Util.canDoWithTime(player.lastTimeBabiday, 30000)) {
                    updatePlayerFlag(player);
                    player.lastTimeBabiday = System.currentTimeMillis();
                }

                sendMenuGotoNextFloorMabuWar(player);

                if (zone.map.mapId == 117) {
                    EffSkinService.gI().setSlow(player, System.currentTimeMillis(), 1000);
                }

                if (zone.map.mapId == 120 && !zone.initBossMabu) {
                    Service.getInstance().sendPercentMabuEgg(player, zone.percentMabuEgg);
                    zone.initBossMabu = (zone.percentMabuEgg == 100);
                }

                if (zone.finishMabuWar) {
                    sendMenuFinishMabuWar(player);
                }
            } else if (!MabuWar14h.gI().isTimeMabuWar()) {
                kickOutOfMap(player);
                removeAllBoss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updatePlayerFlag(Player player) {
        Service service = Service.getInstance();
        if (player.cFlag == 9) {
            if (Util.isTrue(50, 100)) {
                service.changeFlag(player, 10);
                service.sendThongBao(player, "Bạn bị Babiđây thôi miên");
            }
        } else if (Util.isTrue(50, 100)) {
            service.changeFlag(player, 9);
            service.sendThongBao(player, "Bạn được Ôsin giải trừ phép thuật");
        }
    }

    private void kickOutOfMap(Player player) {
        Service.getInstance().sendThongBao(player, "Trận đại chiến đã kết thúc, tàu vận chuyển sẽ đưa bạn về nhà");
        ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, -1, 250);
    }

    public void joinMapMabuWar(Player player) {
        if (!player.isBoss) {
            Service.getInstance().changeFlag(player, 9);
        }
    }

    public void removeAllBoss() {
        if (!clearBoss) {
            bosses.forEach(Boss::leaveMap);
            bosses.clear();
            clearBoss = true;
        }
    }

    private void sendMenuGotoNextFloorMabuWar(Player player) {
        if (player.zone.map.mapId != 120 && !player.sendMenuGotoNextFloorMabuWar &&
                (player.getPowerPoint() >= 20 || player.getPercentPowerPont() >= 20)) {
            NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_MABU_WAR, player.cFlag == 9 ? 4390 : 4388,
                    "Mau theo ta xuống tầng tiếp theo", "Ok");
            player.sendMenuGotoNextFloorMabuWar = true;
        }
    }

    private void sendMenuFinishMabuWar(Player player) {
        if (!player.sendMenuGotoNextFloorMabuWar) {
            NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_MABU_WAR, 4390,
                    "Trận chiến đã kết thúc, mau rời khỏi đây", "Ok");
            player.sendMenuGotoNextFloorMabuWar = true;
        }
    }

    public void BabidayTalk(Player player, String text) {
        Npc npc = NpcManager.getByIdAndMap(ConstNpc.BABIDAY, player.zone.map.mapId);
        if (npc != null) npc.npcChat(text);
    }

    public Zone getMapLastFloor(int mapId) {
        Map map = MapService.gI().getMapById(mapId);
        return (map != null) ? map.zones.stream()
                .filter(zone -> !zone.finishMabuWar)
                .findFirst()
                .orElse(null) : null;
    }

    public void initMabu(Zone zone) {
        new Thread(() -> {
            try {
                Thread.sleep(4000);
                Boss boss = BossFactory.createBoss(BossFactory.MABU_MAP);
                boss.zone = zone;
                bosses.add(boss);
            } catch (Exception ignored) {}
        }).start();
    }
}