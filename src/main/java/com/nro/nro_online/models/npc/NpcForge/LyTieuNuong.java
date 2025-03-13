/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nro.nro_online.models.npc.NpcForge;

import nro.consts.ConstNpc;
import nro.jdbc.daos.PlayerDAO;
import nro.models.item.Item;
import nro.models.item.ItemOption;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.services.InventoryService;
import nro.services.ItemService;
import nro.services.Service;

/**
 *
 * @author Arriety
 */
public class LyTieuNuong extends Npc {

    public LyTieuNuong(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            createOtherMenu(player, ConstNpc.BASE_MENU,
                    "Xin chào, tôi có 1 sự kiện đặc biệt bạn có muốn tham gia không?\n"
                    + "Số tiền nạp tích lũy của bạn hiện tại là: ["
                    + player.getSession().poinCharging + "]",
                    "1 hộp quà\n[10.000 điểm]\nSkien New",
                    "12 hộp quà\n[100.000 điểm]\nSkien New",
                    "1 hộp quà\n[10.000 điểm]",
                    "12 hộp quà\n[100.000 điểm]"
            );
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (this.mapId == 5) {
                if (player.iDMark.isBaseMenu()) {
                    switch (select) {
                        case 0:
                            if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                                if (player.getSession().poinCharging >= 10000) {
                                    if (PlayerDAO.subPoin(player, 10000)) {
                                        Item pet = ItemService.gI().createNewItem((short) 397);
                                        pet.itemOptions.add(new ItemOption(74, 0));
                                        pet.itemOptions.add(new ItemOption(30, 0));
                                        InventoryService.gI().addItemBag(player, pet, 0);
                                        InventoryService.gI().sendItemBags(player);
                                        Service.getInstance().sendThongBao(player, "Success");
                                    } else {
                                        this.npcChat(player, "Lỗi vui lòng báo admin...");
                                    }
                                } else {
                                    Service.getInstance().sendThongBao(player, "Số dư poin không đủ vui lòng nạp thêm tại:\nNROKIMKAN.ONLINE");
                                }
                            } else {
                                Service.getInstance().sendThongBao(player, "Hàng trang đã đầy");
                            }
                            break;
                        case 1:
                            if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                                if (player.getSession().poinCharging >= 100_000) {
                                    if (PlayerDAO.subPoin(player, 100_000)) {
                                        Item pet = ItemService.gI().createNewItem((short) 397);
                                        pet.itemOptions.add(new ItemOption(74, 0));
                                        pet.itemOptions.add(new ItemOption(30, 0));
                                        pet.quantity = 12;
                                        for (int i = 2045; i <= 2051; i++) {
                                            Item item = ItemService.gI().createNewItem((short) i);
                                            item.quantity = 3;
                                            InventoryService.gI().addItemBag(player, item, 0);
                                        }
                                        Item dabaove = ItemService.gI().createNewItem((short) 1143);
                                        dabaove.quantity = 10;
                                        InventoryService.gI().addItemBag(player, dabaove, 0);
                                        InventoryService.gI().addItemBag(player, pet, 0);
                                        InventoryService.gI().sendItemBags(player);
                                        Service.getInstance().sendThongBao(player, "Success");
                                    } else {
                                        this.npcChat(player, "Lỗi vui lòng báo admin...");
                                    }
                                } else {
                                    Service.getInstance().sendThongBao(player, "Số dư poin không đủ vui lòng nạp thêm tại:\nNROKIMKAN.ONLINE");
                                }
                            } else {
                                Service.getInstance().sendThongBao(player, "Hàng trang đã đầy");
                            }
                            break;
                        case 2:
                            if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                                if (player.getSession().poinCharging >= 10000) {
                                    if (PlayerDAO.subPoin(player, 10000)) {
                                        Item pet = ItemService.gI().createNewItem((short) 398);
                                        pet.itemOptions.add(new ItemOption(74, 0));
                                        pet.itemOptions.add(new ItemOption(30, 0));
                                        InventoryService.gI().addItemBag(player, pet, 0);
                                        InventoryService.gI().sendItemBags(player);
                                        Service.getInstance().sendThongBao(player, "Success");
                                    } else {
                                        this.npcChat(player, "Lỗi vui lòng báo admin...");
                                    }
                                } else {
                                    Service.getInstance().sendThongBao(player, "Số dư poin không đủ vui lòng nạp thêm tại:\nNROKIMKAN.ONLINE");
                                }
                            } else {
                                Service.getInstance().sendThongBao(player, "Hàng trang đã đầy");
                            }
                            break;
                        case 3:
                            if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                                if (player.getSession().poinCharging >= 100_000) {
                                    if (PlayerDAO.subPoin(player, 100_000)) {
                                        Item pet = ItemService.gI().createNewItem((short) 398);
                                        pet.itemOptions.add(new ItemOption(74, 0));
                                        pet.itemOptions.add(new ItemOption(30, 0));
                                        pet.quantity = 12;
                                        for (int i = 2045; i <= 2051; i++) {
                                            Item item = ItemService.gI().createNewItem((short) i);
                                            item.quantity = 3;
                                            InventoryService.gI().addItemBag(player, item, 0);
                                        }
                                        Item dabaove = ItemService.gI().createNewItem((short) 1143);
                                        dabaove.quantity = 10;
                                        InventoryService.gI().addItemBag(player, dabaove, 0);
                                        InventoryService.gI().addItemBag(player, pet, 0);
                                        InventoryService.gI().sendItemBags(player);
                                        Service.getInstance().sendThongBao(player, "Success");
                                    } else {
                                        this.npcChat(player, "Lỗi vui lòng báo admin...");
                                    }
                                } else {
                                    Service.getInstance().sendThongBao(player, "Số dư poin không đủ vui lòng nạp thêm tại:\nNROKIMKAN.ONLINE");
                                }
                            } else {
                                Service.getInstance().sendThongBao(player, "Hàng trang đã đầy");
                            }
                            break;
                    }
                }
            }
        }
    }
}
