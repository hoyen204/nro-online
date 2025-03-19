package com.nro.nro_online.models.npc.npc_forge;

import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.consts.ConstPlayer;
import com.nro.nro_online.models.npc.Npc;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.TaskService;

public class Bunma extends Npc {

    public Bunma(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (!canOpenNpc(player) || !TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
            return;
        }
        createOtherMenu(player, ConstNpc.BASE_MENU, "Cậu cần trang bị gì cứ đến chỗ tôi nhé", "Cửa\nhàng");
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (!canOpenNpc(player) || !player.iDMark.isBaseMenu() || select != 0) {
            return;
        }

        if (player.gender == ConstPlayer.TRAI_DAT) {
            openShopWithGender(player, ConstNpc.SHOP_BUNMA_QK_0, 0);
        } else {
            createOtherMenu(player, ConstNpc.IGNORE_MENU, "Xin lỗi cưng, chị chỉ bán đồ cho người Trái Đất", "Đóng");
        }
    }
}