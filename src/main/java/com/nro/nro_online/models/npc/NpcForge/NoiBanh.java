/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nro.nro_online.models.npc.NpcForge;

import static nro.models.npc.NpcFactory.getMenuLamBanh;

import nro.consts.ConstNpc;
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
public class NoiBanh extends Npc {

    private final int COST_DOI_BANH = 1_000_000_000;

    public NoiBanh(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                    "Xin chào " + player.name + "\nTôi là nồi nấu bánh\nTôi có thể giúp gì cho bạn",
                    "Làm\nBánh 1 Trứng", "Làm\nBánh 2 Trứng", getMenuLamBanh(player, 0),
                    getMenuLamBanh(player, 1), "Đổi capsule\nTrung thu");
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            switch (player.iDMark.getIndexMenu()) {
                case ConstNpc.BASE_MENU:
                    switch (select) {
                        case 0:// banh 1 trung
                            Item dauxanh = InventoryService.gI().findItem(player, 889, 10);// dau xanh
                            Item ga = InventoryService.gI().findItem(player, 887, 10);// ga
                            Item trung = InventoryService.gI().findItem(player, 886, 10);// trung
                            Item botmi = InventoryService.gI().findItem(player, 888, 10);// botmy
                            if (dauxanh != null && ga != null && trung != null && botmi != null) {
                                InventoryService.gI().subQuantityItemsBag(player, dauxanh, 10);
                                InventoryService.gI().subQuantityItemsBag(player, ga, 10);
                                InventoryService.gI().subQuantityItemsBag(player, trung, 10);
                                InventoryService.gI().subQuantityItemsBag(player, botmi, 10);
                                Item banhtrungthu1Trung = ItemService.gI().createNewItem((short) 465);
                                banhtrungthu1Trung.itemOptions.add(new ItemOption(74, 0));
                                InventoryService.gI().addItemBag(player, banhtrungthu1Trung, 0);
                                InventoryService.gI().sendItemBags(player);
                                Service.getInstance().sendThongBao(player, "Bạn nhận được Bánh trung thu 1 trứng");
                            } else {
                                Service.getInstance().sendThongBao(player, "Không đủ nguyên liệu");
                            }
                            break;
                        case 1:// banh 2 trung
                            Item dauxanh1 = InventoryService.gI().findItem(player, 889, 20);// dau xanh
                            Item ga1 = InventoryService.gI().findItem(player, 887, 20);// ga
                            Item trung1 = InventoryService.gI().findItem(player, 886, 20);// trung
                            Item botmi1 = InventoryService.gI().findItem(player, 888, 20);// botmy
                            if (dauxanh1 != null && ga1 != null && trung1 != null && botmi1 != null) {
                                InventoryService.gI().subQuantityItemsBag(player, dauxanh1, 20);
                                InventoryService.gI().subQuantityItemsBag(player, ga1, 20);
                                InventoryService.gI().subQuantityItemsBag(player, trung1, 20);
                                InventoryService.gI().subQuantityItemsBag(player, botmi1, 20);
                                Item banhChung = ItemService.gI().createNewItem((short) 466);
                                banhChung.itemOptions.add(new ItemOption(74, 0));
                                InventoryService.gI().addItemBag(player, banhChung, 0);
                                InventoryService.gI().sendItemBags(player);
                                Service.getInstance().sendThongBao(player, "Bạn nhận được Bánh trung thu 2 trứng");
                            } else {
                                Service.getInstance().sendThongBao(player, "Không đủ nguyên liệu");
                            }
                            break;
                        case 2:// banh 1 trung
//                                                if (!player.event.isCookingTetCake()) {
//                                                    Item banh1trung = InventoryService.gI().findItem(player, 465, 1);
//                                                    if (banh1trung != null && player.inventory.ruby >= 2000) {
//                                                        InventoryService.gI().subQuantityItemsBag(player, banh1trung, 1);
//                                                        player.inventory.ruby -= 2000;
//                                                        InventoryService.gI().sendItemBags(player);
//                                                        Service.getInstance().sendMoney(player);
//                                                        player.event.setTimeCookTetCake(300);
//                                                        player.event.setCookingTetCake(true);
//                                                        Service.getInstance().sendThongBao(player, "Bắt đầu nấu bánh,thời gian nấu bánh là 5 phút");
//                                                    } else {
//                                                        Service.getInstance().sendThongBao(player, "Không đủ nguyên liệu hoặc 2 nghìn ruby");
//                                                    }
//                                                } else if (player.event.isCookingTetCake() && player.event.getTimeCookTetCake() == 0) {
//                                                    Item cake = ItemService.gI().createNewItem((short) 891, 1);
//                                                    cake.itemOptions.add(new ItemOption(50, 10));
//                                                    cake.itemOptions.add(new ItemOption(77, 10));
//                                                    cake.itemOptions.add(new ItemOption(103, 10));
//                                                    cake.itemOptions.add(new ItemOption(74, 0));
//                                                    InventoryService.gI().addItemBag(player, cake, 0);
//                                                    InventoryService.gI().sendItemBags(player);
//                                                    player.event.setCookingTetCake(false);
//                                                    Service.getInstance().sendThongBao(player,
//                                                            "Bạn nhận được Bánh trung thu thập cẩm (đã chín)");
//                                                }
                            break;
                        case 3:// banh 2 chung
//                                                if (!player.event.isCookingChungCake()) {
//                                                    Item banhtrungthu2trung = InventoryService.gI().findItem(player, 466, 1);
//                                                    if (banhtrungthu2trung != null && player.inventory.ruby >= 5000) {
//                                                        InventoryService.gI().subQuantityItemsBag(player, banhtrungthu2trung, 1);
//                                                        player.inventory.ruby -= 5000;
//                                                        InventoryService.gI().sendItemBags(player);
//                                                        Service.getInstance().sendMoney(player);
//                                                        player.event.setTimeCookChungCake(300);
//                                                        player.event.setCookingChungCake(true);
//                                                        Service.getInstance().sendThongBao(player,
//                                                                "Bắt đầu nấu bánh,thời gian nấu bánh là 5 phút");
//                                                    } else {
//                                                        Service.getInstance().sendThongBao(player, "Không đủ nguyên liệu hoặc 5 nghìn ruby");
//                                                    }
//                                                } else if (player.event.isCookingChungCake() && player.event.getTimeCookChungCake() == 0) {
//                                                    Item cake = ItemService.gI().createNewItem((short) 472, 1);
//                                                    cake.itemOptions.add(new ItemOption(50, 15));
//                                                    cake.itemOptions.add(new ItemOption(77, 15));
//                                                    cake.itemOptions.add(new ItemOption(103, 15));
//                                                    cake.itemOptions.add(new ItemOption(74, 0));
//                                                    InventoryService.gI().addItemBag(player, cake, 0);
//                                                    InventoryService.gI().sendItemBags(player);
//                                                    player.event.setCookingChungCake(false);
//                                                    Service.getInstance().sendThongBao(player,
//                                                            "Bạn nhận được Bánh Trung Thu Đặc Biệt (đã chín)");
//                                                }
                            break;
                        case 4:
                            Item carot = InventoryService.gI().findItem(player, 462, 99);
                            if (carot != null && player.inventory.gold >= COST_DOI_BANH) {
                                Item hopQua = ItemService.gI().createNewItem((short) 737, 1);
                                hopQua.itemOptions.add(new ItemOption(30, 0));
                                hopQua.itemOptions.add(new ItemOption(74, 0));
                                InventoryService.gI().subQuantityItemsBag(player, carot, 99);
                                player.inventory.gold -= COST_DOI_BANH;
                                InventoryService.gI().addItemBag(player, hopQua, 0);
                                InventoryService.gI().sendItemBags(player);
                                Service.getInstance().sendMoney(player);
                                Service.getInstance().sendThongBao(player, "Bạn nhận được Capsule Trung Thu");
                            } else {
                                Service.getInstance().sendThongBao(player, "Không đủ nguyên liệu hoặc 1 tỷ vàng để đổi");
                            }
                            break;
                    }
                    break;

            }
        }
    }

}
