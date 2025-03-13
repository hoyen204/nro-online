package com.nro.nro_online.models.map.tranhngoc;

import com.nro.nro_online.consts.ConstItem;
import com.nro.nro_online.consts.ConstTranhNgocNamek;
import com.nro.nro_online.models.DragonNamecWar.TranhNgocService;
import com.nro.nro_online.models.map.ItemMap;
import com.nro.nro_online.models.map.Map;
import com.nro.nro_online.models.map.Zone;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.func.ChangeMapService;
import com.nro.nro_online.utils.Util;

import java.util.List;

public class TranhNgocZone extends Zone {

    private long lastTimeDropBall;
    private boolean isOpen;
    private boolean isClosed;

    public TranhNgocZone(Map map, int zoneId, int maxPlayer) {
        super(map, zoneId, maxPlayer);
    }

    @Override
    public void update() {
        super.update();
        if (isOpen) updateZoneTranhNgoc();
    }

    @Override
    public void pickItem(Player player, int itemMapId) {
        ItemMap itemMap = getItemMapByItemMapId(itemMapId);
        if (itemMap == null || itemMap.isPickedUp || (itemMap.playerId != player.id && itemMap.playerId != -1)) return;

        synchronized (itemMap) {
            if (!itemMap.isPickedUp && itemMap.isNamecBallTranhDoat) {
                TranhNgocService.getInstance().pickBall(player, itemMap);
            }
        }
    }

    private void updateZoneTranhNgoc() {
        if (isClosed) {
            items.clear();
            return;
        }

        if (!Util.canDoWithTime(lastTimeDropBall, ConstTranhNgocNamek.LAST_TIME_DROP_BALL)) return;

        int id = Util.nextInt(ConstItem.NGOC_RONG_NAMEK_1_SAO, ConstItem.NGOC_RONG_NAMEK_7_SAO);
        if (getItemMapByTempId(id) == null && !findPlayerHaveBallTranhDoat(id)) {
            lastTimeDropBall = System.currentTimeMillis();
            int x = Util.nextInt(20, map.mapWidth);
            int y = map.yPhysicInTop(x, Util.nextInt(20, map.mapHeight - 200));
            ItemMap itemMap = new ItemMap(this, id, 1, x, y, -1);
            itemMap.isNamecBallTranhDoat = true;
            Service.getInstance().dropItemMap(this, itemMap);
        }
    }

    public void close() {
        kickAllPlayer();
        map.zones.remove(this);
    }

    public void kickAllPlayer() {
        List<Player> players = getPlayers();
        for (Player player : players) {
            synchronized (player) {
                ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, -1, 250);
                Service.getInstance().changeFlag(player, 0);
                player.isHoldNamecBallTranhDoat = false;
                player.tempIdNamecBallHoldTranhDoat = -1;
            }
        }
        isClosed = true;
    }

    // Getter/Setter nếu cần
    public void setOpen(boolean open) {
        this.isOpen = open;
    }
}