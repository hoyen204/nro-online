package com.nro.nro_online.models.npc.npc_forge;

import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.models.item.Item;
import com.nro.nro_online.models.npc.Npc;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.InventoryService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.func.ShopService;

public class Santa extends Npc {

    private static final int[] VALID_MAPS = { 5, 13, 20 };

    public Santa(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (!canOpenNpc(player))
            return;

        Item pGG = InventoryService.gI().findItem(player.inventory.itemsBag, 459);
        boolean hasDiscount = pGG != null && pGG.quantity >= 1;
        String[] options = hasDiscount
                ? new String[] { "Cửa hàng", "Giảm giá\n80%", "Tiệm\nhớt tóc", "Tiệm\nHồng ngọc" }
                : new String[] { "Cửa hàng", "Tiệm\nhớt tóc", "Tiệm\nHồng ngọc" };
        int menuId = hasDiscount ? ConstNpc.SANTA_PGG : ConstNpc.BASE_MENU;

        createOtherMenu(player, menuId,
                "Xin chào, ta có một số vật phẩm đặt biệt cậu có muốn xem không?", options);
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (!canOpenNpc(player) || !isValidMap(this.mapId))
            return;

        if (player.iDMark.isBaseMenu()) {
            handleBaseMenu(player, select);
        } else if (player.iDMark.getIndexMenu() == ConstNpc.SANTA_PGG) {
            handleDiscountMenu(player, select);
        }
    }

    private boolean isValidMap(int mapId) {
        for (int validMap : VALID_MAPS) {
            if (mapId == validMap)
                return true;
        }
        return false;
    }

    private void handleBaseMenu(Player player, int select) {
        switch (select) {
            case 0 -> openShopWithGender(player, ConstNpc.SHOP_SANTA_0, 0);
            case 1 -> npcChat(player, "Không có tiền mà đòi cắt tóc à?");
            case 2 -> ShopService.gI().openShopSpecial(player, this, ConstNpc.SHOP_SANTA_SPEC, 3, -1);
        }
    }

    private void handleDiscountMenu(Player player, int select) {
        switch (select) {
            case 0 -> openShopWithGender(player, ConstNpc.SHOP_SANTA_0, 0);
            case 1 -> openDiscountShop(player);
            case 2 -> npcChat(player, "Không có tiền mà đòi cắt tóc à?");
            case 3 -> ShopService.gI().openShopSpecial(player, this, ConstNpc.SHOP_SANTA_SPEC_50, 4, -1);
        }
    }

    private void openDiscountShop(Player player) {
        Item pGG = InventoryService.gI().findItem(player.inventory.itemsBag, 459);
        if (pGG != null) {
            ShopService.gI().openShopSpecial(player, this, ConstNpc.SHOP_GIAM_GIA, 2, -1);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn không có phiếu giảm giá!");
        }
    }
}