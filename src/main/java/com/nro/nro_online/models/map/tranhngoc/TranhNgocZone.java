package com.nro.nro_online.models.map.tranhngoc;

import java.util.List;

import nro.consts.ConstItem;
import nro.consts.ConstTranhNgocNamek;
import nro.models.map.ItemMap;
import nro.models.map.Map;
import nro.models.map.Zone;
import nro.models.phuban.DragonNamecWar.TranhNgocService;
import nro.models.player.Player;
import nro.services.Service;
import nro.services.func.ChangeMapService;
import nro.utils.Util;

/**
 *
 * @Build by Arriety
 */
public class TranhNgocZone extends Zone {

    public long lastTimeDropBall;

    public boolean is_open;

    private boolean is_closed;

    public TranhNgocZone(Map map, int zoneId, int maxPlayer) {
        super(map, zoneId, maxPlayer);
    }

    @Override
    public void update() {
        super.update();
        updateZoneTranhNgoc();
    }

    @Override
    public void pickItem(Player player, int itemMapId) {
        ItemMap itemMap = getItemMapByItemMapId(itemMapId);
        if (itemMap != null && !itemMap.isPickedUp) {
            synchronized (itemMap) {
                if (!itemMap.isPickedUp) {
                    if (itemMap.playerId == player.id || itemMap.playerId == -1) {
                        if (itemMap.isNamecBallTranhDoat) {
                            TranhNgocService.getInstance().pickBall(player, itemMap);
                        }
                    }
                }
            }
        }
    }

    private void updateZoneTranhNgoc() {
        if (is_open) {
            if (is_closed) {
                items.clear();
            }
            if (Util.canDoWithTime(lastTimeDropBall, ConstTranhNgocNamek.LAST_TIME_DROP_BALL)) {
                int id = Util.nextInt(ConstItem.NGOC_RONG_NAMEK_1_SAO, ConstItem.NGOC_RONG_NAMEK_7_SAO);
                ItemMap it = this.getItemMapByTempId(id);
                if (it == null && !findPlayerHaveBallTranhDoat(id)) {
                    lastTimeDropBall = System.currentTimeMillis();
                    int x = Util.nextInt(20, map.mapWidth);
                    int y = map.yPhysicInTop(x, Util.nextInt(20, map.mapHeight - 200));
                    ItemMap itemMap = new ItemMap(this, id, 1, x, y, -1);
                    itemMap.isNamecBallTranhDoat = true;
                    Service.getInstance().dropItemMap(this, itemMap);
                }
            }
        }
    }

    public void close() {
        this.kickAllPlayer();
        this.map.zones.remove(this);
    }

    public void kickAllPlayer() {
        List<Player> players = this.getPlayers();
        int size = players.size();
        for (int i = size - 1; i >= 0; i--) {
            Player player = players.get(i);
            synchronized (player) {
                ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, -1, 250);
                Service.getInstance().changeFlag(player, 0);
                player.isHoldNamecBallTranhDoat = false;
                player.tempIdNamecBallHoldTranhDoat = -1;
            }
        }
        is_closed = true;
    }
}
