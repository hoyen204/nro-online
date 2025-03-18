/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nro.nro_online.models.npc.npcForge;

import nro.consts.ConstNpc;
import nro.models.item.Item;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.services.InventoryService;
import nro.services.Service;
import nro.services.func.ShopService;

/**
 *
 * @author Arriety
 */
public class Santa extends Npc {

    public Santa(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            Item pGG = InventoryService.gI().findItem(player.inventory.itemsBag, 459);
            int soLuong = 0;
            if (pGG != null) {
                soLuong = pGG.quantity;
            }
            if (soLuong >= 1) {
                this.createOtherMenu(player, ConstNpc.SANTA_PGG, "Xin chào, ta có một số vật phẩm đặt biệt cậu có muốn xem không?",
                        "Cửa hàng", "Giảm giá\n80%", "Tiệm\nhớt tóc", "Tiệm\nHồng ngọc");
            } else {
                this.createOtherMenu(player, ConstNpc.BASE_MENU, "Xin chào, ta có một số vật phẩm đặt biệt cậu có muốn xem không?",
                        "Cửa hàng", "Tiệm\nhớt tóc", "Tiệm\nHồng ngọc");
            }
        }

    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (this.mapId == 5 || this.mapId == 13 || this.mapId == 20) {
                if (player.iDMark.isBaseMenu()) {
                    switch (select) {
                        case 0: // shop
                            this.openShopWithGender(player, ConstNpc.SHOP_SANTA_0, 0);
                            break;
                        case 1: // tiệm hớt tóc
                            this.npcChat(player, "Không có tiền mà đòi cắt tóc à?");
                            break;
                        case 2:
                            ShopService.gI().openShopSpecial(player, this, ConstNpc.SHOP_SANTA_SPEC, 3, -1);
                            break;
                    }
                } else if (player.iDMark.getIndexMenu() == ConstNpc.SANTA_PGG) {
                    switch (select) {
                        case 0: // shop
                            this.openShopWithGender(player, ConstNpc.SHOP_SANTA_0, 0);
                            break;
                        case 1: // shop
                            Item pGG = InventoryService.gI().findItem(player.inventory.itemsBag, 459);
                            if (pGG != null) {
                                ShopService.gI().openShopSpecial(player, this, ConstNpc.SHOP_GIAM_GIA, 2, -1);
                            } else {
                                Service.getInstance().sendThongBao(player, "Bạn không có phiếu giảm giá!");
                            }
                            break;
                        case 2: // tiệm hớt tóc
                            this.npcChat(player, "Không có tiền mà đòi cắt tóc à?");
                            break;
                        case 3:
                            ShopService.gI().openShopSpecial(player, this, ConstNpc.SHOP_SANTA_SPEC_50, 4, -1);
                            break;
                    }
                }
            }
        }
    }

}
