package com.nro.nro_online.services;

import com.nro.nro_online.models.map.ItemMap;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.server.io.Message;
import com.nro.nro_online.utils.Log;
import com.nro.nro_online.utils.Util;

public class ItemMapService {

    private ItemMapService(){}

    private static final ItemMapService i = new ItemMapService();

    public static ItemMapService gI() {
        return i;
    }

    public void pickItem(Player player, int itemMapId) {
        if (player.zone == null) {
            Service.getInstance().sendThongBaoOK(player, "Đã có lỗi xảy ra");
            return;
        }
        if (Util.canDoWithTime(player.lastTimePickItem, 1000)) {
            player.zone.pickItem(player, itemMapId);
            player.lastTimePickItem = System.currentTimeMillis();
        }
    }

    public void removeItemMapAndSendClient(ItemMap itemMap) {
        sendItemMapDisappear(itemMap);
        removeItemMap(itemMap);
    }

    public void sendItemMapDisappear(ItemMap itemMap) {
        try (Message msg = new Message(-21)) {
            msg.writer().writeShort(itemMap.itemMapId);
            Service.getInstance().sendMessAllPlayerInMap(itemMap.zone, msg);
        } catch (Exception e) {
            Log.error(ItemMapService.class, e);
        }
    }

    public void removeItemMap(ItemMap itemMap) {
        itemMap.zone.removeItemMap(itemMap);
        itemMap.dispose();
    }

    public boolean isBlackBall(int tempId) {
        return tempId >= 372 && tempId <= 378;
    }

    public boolean isNamecBall(int tempId) {
        return tempId >= 353 && tempId <= 360;
    }
}