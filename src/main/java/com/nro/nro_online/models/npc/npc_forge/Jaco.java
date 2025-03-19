package com.nro.nro_online.models.npc.npc_forge;

import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.models.item.Item;
import com.nro.nro_online.models.npc.Npc;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.InventoryService;
import com.nro.nro_online.services.ItemService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.func.ChangeMapService;

public class Jaco extends Npc {

    public Jaco(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (!canOpenNpc(player))
            return;

        switch (this.mapId) {
            case 24, 25, 26 -> createOtherMenu(player, ConstNpc.BASE_MENU,
                    "Gô Tên, Calich và Monaka đang gặp chuyện ở hành tinh Potaufeu \n Hãy đến đó ngay",
                    "Đến \nPotaufeu");
            case 139 -> createOtherMenu(player, ConstNpc.BASE_MENU, "Người muốn trở về?", "Quay về", "Từ chối");
            case 208 -> createOtherMenu(player, ConstNpc.BASE_MENU,
                    "Con có muốn đổi x999 bông hoa để đổi lấy x1 capsule Vàng và x10 gậy thượng đế", "Đổi", "Từ chối");
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (!canOpenNpc(player) || !player.iDMark.isBaseMenu())
            return;

        switch (this.mapId) {
            case 24, 25, 26 -> {
                if (select == 0)
                    ChangeMapService.gI().goToPotaufeu(player);
            }
            case 139 -> {
                if (select == 0)
                    ChangeMapService.gI().changeMapBySpaceShip(player, 24 + player.gender, -1, -1);
            }
            case 208 -> {
                if (select == 0)
                    handleTrade(player);
            }
        }
    }

    private void handleTrade(Player player) {
        Item gayThuongDe = InventoryService.gI().findItemBag(player, 1231);
        Item hoa = InventoryService.gI().findItemBag(player, 1098);

        if (hoa == null || hoa.quantity < 999) {
            npcChat(player, "Bạn chưa đủ hoa");
            return;
        }
        if (gayThuongDe == null || gayThuongDe.quantity < 10) {
            npcChat(player, "Bạn chưa đủ gậy thượng đế");
            return;
        }

        Item capsule = ItemService.gI().createNewItem((short) 574);
        InventoryService.gI().addItemBag(player, capsule, 999);
        InventoryService.gI().subQuantityItemsBag(player, hoa, 999);
        InventoryService.gI().subQuantityItemsBag(player, gayThuongDe, 10);
        Service.getInstance().sendThongBao(player, "Chúc mừng bạn đã nhận được x1 capsule Vàng");
        InventoryService.gI().sendItemBags(player);
    }
}