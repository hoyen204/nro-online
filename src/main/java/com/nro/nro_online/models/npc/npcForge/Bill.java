/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nro.nro_online.models.npc.npcForge;

import nro.consts.ConstNpc;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.services.func.ChangeMapService;
import nro.services.func.ShopService;

/**
 *
 * @author Arriety
 */
public class Bill extends Npc {

    public Bill(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            switch (this.mapId) {
                case 48:
                    if (!player.setClothes.godClothes) {
                        this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Ngươi hãy mang 5 món thần linh\nvà x99 thức ăn đến đây...\nrồi ta nói tiếp", "Từ chối");
                    } else {
                        createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Ngươi muốn gì nào?",
                                "Mua đồ\nhủy diệt", "Đóng");
                    }
                    break;
                case 154:
                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                            "...", "Về\nthánh địa\n Kaio", "Từ chối");
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            switch (this.mapId) {
                case 48:
                    switch (player.iDMark.getIndexMenu()) {
                        case ConstNpc.BASE_MENU:
                            if (select == 0) {
                                ShopService.gI().openShopSpecial(player, this, ConstNpc.SHOP_BILL_HUY_DIET_0, 0, -1);
                            }
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
}
