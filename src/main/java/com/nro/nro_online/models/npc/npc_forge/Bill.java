package com.nro.nro_online.models.npc.npc_forge;

import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.models.npc.Npc;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.func.ChangeMapService;
import com.nro.nro_online.services.func.ShopService;

public class Bill extends Npc {

    public Bill(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (!canOpenNpc(player)) {
            return;
        }
        
        switch (this.mapId) {
            case 48:
                handleMap48Menu(player);
                break;
            case 154:
                createOtherMenu(player, ConstNpc.IGNORE_MENU, "...", "Về\nthánh địa\n Kaio", "Từ chối");
                break;
        }
    }

    private void handleMap48Menu(Player player) {
        if (!player.setClothes.godClothes) {
            createOtherMenu(player, ConstNpc.IGNORE_MENU,
                    "Ngươi hãy mang 5 món thần linh\nvà x99 thức ăn đến đây...\nrồi ta nói tiếp", "Từ chối");
            return;
        }
        createOtherMenu(player, ConstNpc.BASE_MENU, "Ngươi muốn gì nào?", "Mua đồ\nhủy diệt", "Đóng");
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (!canOpenNpc(player)) {
            return;
        }

        switch (this.mapId) {
            case 48:
                if (player.iDMark.getIndexMenu() == ConstNpc.BASE_MENU && select == 0) {
                    ShopService.gI().openShopSpecial(player, this, ConstNpc.SHOP_BILL_HUY_DIET_0, 0, -1);
                }
                break;
            case 154:
                if (select == 0) {
                    ChangeMapService.gI().changeMapBySpaceShip(player, 50, -1, 387);
                }
                break;
        }
    }
}
