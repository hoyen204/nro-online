package com.nro.nro_online.models.npc.npc_forge;

import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.consts.ConstPlayer;
import com.nro.nro_online.models.npc.Npc;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.TaskService;

public class Appule extends Npc {

    public Appule(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player) && !TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
            createOtherMenu(player, ConstNpc.BASE_MENU, "Ngươi cần trang bị gì cứ đến chỗ ta nhé", "Cửa\nhàng");
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (!canOpenNpc(player) || !player.iDMark.isBaseMenu()) return;

        if (select == 0) { // Shop
            handleShop(player);
        }
    }

    private void handleShop(Player player) {
        if (player.gender == ConstPlayer.XAYDA) {
            openShopWithGender(player, ConstNpc.SHOP_APPULE_0, 0);
        } else {
            createOtherMenu(player, ConstNpc.IGNORE_MENU,
                    "Về hành tinh hạ đẳng của ngươi mà mua đồ cùi nhé. Tại đây ta chỉ bán đồ cho người Xayda thôi",
                    "Đóng");
        }
    }
}
