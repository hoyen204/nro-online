package com.nro.nro_online.models.npc.npc_forge;

import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.models.npc.Npc;
import com.nro.nro_online.models.player.NPoint;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.OpenPowerService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.utils.Util;

public class QuocVuong extends Npc {

    public QuocVuong(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (!canOpenNpc(player))
            return;

        createOtherMenu(player, ConstNpc.BASE_MENU,
                "Con muốn nâng giới hạn sức mạnh cho bản thân?", "Bản thân", "Từ chối");
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (!canOpenNpc(player))
            return;

        if (player.iDMark.isBaseMenu()) {
            if (select == 0)
                handleBaseMenu(player);
        } else if (player.iDMark.getIndexMenu() == ConstNpc.OPEN_POWER_MYSEFT) {
            handlePowerMenu(player, select);
        }
    }

    private void handleBaseMenu(Player player) {
        if (player.nPoint.limitPower < NPoint.MAX_LIMIT) {
            createOtherMenu(player, ConstNpc.OPEN_POWER_MYSEFT,
                    "Ta sẽ truền năng lượng giúp con mở giới hạn sức mạnh của bản thân lên " +
                            Util.numberToMoney(player.nPoint.getPowerNextLimit()),
                    "Nâng\ngiới hạn\nsức mạnh",
                    "Nâng ngay\n" + Util.numberToMoney(OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER) + " vàng",
                    "Đóng");
        } else {
            createOtherMenu(player, ConstNpc.IGNORE_MENU, "Sức mạnh của con đã đạt tới giới hạn", "Đóng");
        }
    }

    private void handlePowerMenu(Player player, int select) {
        switch (select) {
            case 0 -> OpenPowerService.gI().openPowerBasic(player);
            case 1 -> upgradePowerSpeed(player);
        }
    }

    private void upgradePowerSpeed(Player player) {
        long cost = OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER;
        if (player.inventory.gold < cost) {
            Service.getInstance().sendThongBao(player,
                    "Bạn không đủ vàng để mở, còn thiếu " + Util.numberToMoney(cost - player.inventory.gold) + " vàng");
            return;
        }
        if (OpenPowerService.gI().openPowerSpeed(player)) {
            player.inventory.gold -= cost;
            Service.getInstance().sendMoney(player);
        }
    }
}