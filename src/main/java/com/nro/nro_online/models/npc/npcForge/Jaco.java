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
import nro.services.ItemService;
import nro.services.Service;
import nro.services.func.ChangeMapService;

/**
 *
 * @author Arriety
 */
public class Jaco extends Npc {

    public Jaco(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        switch (this.mapId) {
            case 24:
            case 25:
            case 26:
                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                        "Gô Tên, Calich và Monaka đang gặp chuyện ở hành tinh Potaufeu \n Hãy đến đó ngay", "Đến \nPotaufeu");
                break;
            case 139:
                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                        "Người muốn trở về?", "Quay về", "Từ chối");
                break;
            case 208:
                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                        "Con có muốn đổi x999 bông hoa để đổi lấy x1 capsule Vàng và x10 gậy thượng đế", "Đổi", "Từ chối");
                break;
            default:
                break;
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            switch (this.mapId) {
                case 24:
                case 25:
                case 26:
                    if (player.iDMark.isBaseMenu()) {
                        if (select == 0) {
                            ChangeMapService.gI().goToPotaufeu(player);
                        }
                    }
                    break;
                case 139:
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0:
                                ChangeMapService.gI().changeMapBySpaceShip(player, 24 + player.gender, -1, -1);
                                break;
                        }
                    }
                    break;
                case 208:
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0:
                                Item gayThuongDe = InventoryService.gI().findItemBag(player, 1231);
                                Item hoa = InventoryService.gI().findItemBag(player, 1098);
                                if (hoa == null || hoa.quantity < 999) {
                                    this.npcChat(player, "Bạn chưa đủ hoa");
                                    return;
                                }
                                if (gayThuongDe == null || gayThuongDe.quantity < 10) {
                                    this.npcChat(player, "Bạn chưa đủ gậy thượng đế");
                                    return;
                                }
                                Item capsule = ItemService.gI().createNewItem((short) 574);
                                InventoryService.gI().addItemBag(player, capsule, 999);
                                InventoryService.gI().subQuantityItemsBag(player, hoa, 999);
                                InventoryService.gI().subQuantityItemsBag(player, gayThuongDe, 10);
                                Service.getInstance().sendThongBao(player, "Chúc mừng bạn đã nhận được x1 capsule Vàng");
                                InventoryService.gI().sendItemBags(player);
                                break;
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
