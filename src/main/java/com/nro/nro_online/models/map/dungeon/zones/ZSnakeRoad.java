/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nro.nro_online.models.map.dungeon.zones;

import java.util.List;
import java.util.stream.Collectors;

import static com.nro.nro_online.services.func.ChangeMapService.NON_SPACE_SHIP;

import com.nro.nro_online.models.map.Map;
import com.nro.nro_online.models.map.dungeon.Dungeon;
import com.nro.nro_online.consts.ConstMap;
import com.nro.nro_online.models.map.WayPoint;
import com.nro.nro_online.models.map.Zone;
import com.nro.nro_online.models.map.dungeon.SnakeRoad;
import com.nro.nro_online.models.mob.Mob;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.MapService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.func.ChangeMapService;

/**
 *
 * @Build by Arriety
 */
public class ZSnakeRoad extends ZDungeon {

    public ZSnakeRoad(Map map, Dungeon dungeon) {
        super(map, dungeon);
    }

    public boolean isKilledAll() {
        synchronized (mobs) {
            for (Mob mob : mobs) {
                if (!mob.isDie()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void changeMapWaypoint(Player player) {
        if (!isKilledAll()) {
            int x = player.location.x;
            if (player.location.x >= map.mapWidth - 60) {
                x = map.mapWidth - 60;
            } else if (player.location.x <= 60) {
                x = 60;
            }
            Service.getInstance().resetPoint(player, x, player.location.y);
            Service.getInstance().sendThongBaoOK(player, "Không thể đến khu vực này");
            return;
        }
        if (map.mapId == 45 || map.mapId == 46) {
            int x = player.location.x;
            int y = player.location.y;
            if (x >= 35 && x <= 685 && y >= 550 && y <= 560) {
                int xGo = map.mapId == 45 ? 420 : 636;
                int yGo = 150;
                ZSnakeRoad z = (ZSnakeRoad) dungeon.find(map.mapId + 1);
                z.enter(player, xGo, yGo);
            }
            return;
        }
        WayPoint wp = MapService.gI().getWaypointPlayerIn(player);
        if (wp != null) {
            int next = wp.goMap;
            short xGo = wp.goX;
            short yGo = wp.goY;
            if (next == ConstMap.DOI_HOA_CUC) {
                Zone z = MapService.gI().getMapCanJoin(player, next);
                ChangeMapService.gI().changeMap(player, z, -1, -1, xGo, yGo, NON_SPACE_SHIP);
            } else {
                ZSnakeRoad r = (ZSnakeRoad) dungeon.find(next);
                r.enter(player, xGo, yGo);
            }
        }
    }

    @Override
    public void enter(Player player, int x, int y) {
        if (map.mapId == ConstMap.HOANG_MAC || (player.zone.map.mapId == ConstMap.CON_DUONG_RAN_DOC && map.mapId == ConstMap.THAN_DIEN)) {
            ChangeMapService.gI().changeMapYardrat(player, this, x, y);
        } else {
            ChangeMapService.gI().changeMap(player, this, x, y);
        }
        setTextTime();
    }

    @Override
    public void initMob(Mob mob) {
        int level = ((SnakeRoad) dungeon).getLevel();
        int maxHP = mob.point.getHpFull() * 30 * level;
        mob.point.setHP(maxHP);
        mob.point.setHpFull(maxHP);
        mob.setTiemNang();
    }

    @Override
    public void playerMove(Player player, int x, int y) {
        if (map.mapId == ConstMap.RUNG_KARIN && player.isInteractWithKarin()) {
            if (player.location.x < 200) {
                ZSnakeRoad r = (ZSnakeRoad) dungeon.find(ConstMap.HOANG_MAC);
                int xGo = 375;
                int yGo = 312;
                r.enter(player, xGo, yGo);
            }
        }
        super.playerMove(player, x, y);
    }

    @Override
    public void close() {
        List<Player> players = getPlayers();
        synchronized (players) {
            players = players.stream().collect(Collectors.toList());
            for (Player player : players) {
                ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, -1, -1);
            }
        }
        super.close();
    }

}
