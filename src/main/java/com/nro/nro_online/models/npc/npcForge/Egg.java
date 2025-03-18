/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nro.nro_online.models.npc.npcForge;

import nro.consts.ConstNpc;
import nro.consts.ConstPlayer;
import nro.models.item.Item;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.services.InventoryService;
import nro.services.Service;

/**
 *
 * @author Arriety
 */
public class Egg extends Npc {

    private final int COST_AP_TRUNG_NHANH = 1000000000;

    public Egg(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (this.mapId == (21 + player.gender)) {
                player.mabuEgg.sendMabuEgg();
                if (player.mabuEgg.getSecondDone() != 0) {
                    this.createOtherMenu(player, ConstNpc.CAN_NOT_OPEN_EGG, "Bư bư bư...",
                            "Hủy bỏ\ntrứng", "" + player.mabuEgg.getSecondDone(), "Đóng");
                } else {
                    this.createOtherMenu(player, ConstNpc.CAN_OPEN_EGG, "Bư bư bư...", "Nở",
                            "Hủy bỏ\ntrứng", "Đóng");
                }
            }
            if (this.mapId == 104) {
                player.egglinhthu.sendEggLinhThu();
                if (player.egglinhthu.getSecondDone() > 0) {
                    this.createOtherMenu(player, ConstNpc.CAN_NOT_OPEN_EGG, "Burk Burk...",
                            "Hủy bỏ\ntrứng", "Ấp nhanh\n", "Đóng");
                } else {
                    this.createOtherMenu(player, ConstNpc.CAN_OPEN_EGG, "Burk Burk...", "Nở", "Hủy bỏ\ntrứng", "Đóng");
                }
            }
            if (this.mapId == 154) {
                player.billEgg.sendBillEgg();
                if (player.billEgg.getSecondDone() != 0) {
                    this.createOtherMenu(player, ConstNpc.CAN_NOT_OPEN_EGG, "Gruuu Gruuu Gruuu...",
                            "Hủy bỏ\ntrứng",
                            "Ấp nhanh\n1 Gậy thượng đế", "Đóng");
                } else {
                    this.createOtherMenu(player, ConstNpc.CAN_OPEN_EGG, "Gruuu Gruuu Gruuu...", "Nở",
                            "Hủy bỏ\ntrứng", "Đóng");
                }
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (this.mapId == 104) {
                switch (player.iDMark.getIndexMenu()) {
                    case ConstNpc.CAN_NOT_OPEN_EGG:
                        if (select == 0) {
                            this.createOtherMenu(player, ConstNpc.CONFIRM_DESTROY_EGG,
                                    "Bạn có chắc chắn muốn hủy bỏ trứng Linh Thú?", "Đồng ý", "Từ chối");
                        } else if (select == 1) {
                            this.createOtherMenu(player, ConstNpc.CONFIRM_SPEED_EGG,
                                    "Lựa chọn:\n 1) 5k Ruby(Giảm 1 Day)\n "
                                    + "2) 25k Ruby(Giảm 5 Day)\n "
                                    + "3) 50k Ruby(Giảm 10 Day)", "Lựa chọn 1", "Lựa chọn 2", "Lựa chọn 3", "Từ chối");
                        }
                        break;
                    case ConstNpc.CAN_OPEN_EGG:
                        switch (select) {
                            case 0:
                                this.createOtherMenu(player, ConstNpc.CONFIRM_OPEN_EGG,
                                        "Bạn có chắc chắn cho trứng nở?\n",
                                        "Đồng ý", "Từ chối");
                                break;
                            case 1:
                                this.createOtherMenu(player, ConstNpc.CONFIRM_DESTROY_EGG,
                                        "Bạn có chắc chắn muốn hủy bỏ trứng?", "Đồng ý", "Từ chối");
                                break;
                        }
                        break;
                    case ConstNpc.CONFIRM_OPEN_EGG:
                        switch (select) {
                            case 0:
                                player.egglinhthu.openEgg();
                                break;
                            default:
                                break;
                        }
                        break;
                    case ConstNpc.CONFIRM_SPEED_EGG:
                        byte time = 0;
                        switch (select) {
                            case 0:
                                time = 1;
                                break;
                            case 1:
                                time = 5;
                                break;
                            case 2:
                                time = 10;
                                break;
                            default:
                                break;
                        }
                        if (player.inventory.ruby < 5_000 * time) {
                            Service.getInstance().sendThongBao(player, "Thiếu Ruby!");
                        } else {
                            player.egglinhthu.subTimeDone(time, 0, 0, 0);
                            player.inventory.ruby -= 5_000 * time;
                            Service.getInstance().sendMoney(player);
                        }
                        break;
                    case ConstNpc.CONFIRM_DESTROY_EGG:
                        if (select == 0) {
                            player.egglinhthu.destroyEgg();
                        }
                        break;
                }
            }
            if (this.mapId == (21 + player.gender)) {
                switch (player.iDMark.getIndexMenu()) {
                    case ConstNpc.CAN_NOT_OPEN_EGG:
                        if (select == 0) {
                            this.createOtherMenu(player, ConstNpc.CONFIRM_DESTROY_EGG,
                                    "Bạn có chắc chắn muốn hủy bỏ trứng?", "Đồng ý", "Từ chối");
                        }
//                        else if (select == 1) {
//                            if (player.inventory.gold >= COST_AP_TRUNG_NHANH) {
//                                player.inventory.gold -= COST_AP_TRUNG_NHANH;
////                                player.mabuEgg.timeDone = 0;
//                                Service.getInstance().sendMoney(player);
//                                player.mabuEgg.sendMabuEgg();
//                            } else {
//                                Service.getInstance().sendThongBao(player,
//                                        "Bạn không đủ vàng để thực hiện, còn thiếu "
//                                        + Util.numberToMoney(
//                                                (COST_AP_TRUNG_NHANH - player.inventory.gold))
//                                        + " vàng");
//                            }
//                        }
                        break;
                    case ConstNpc.CAN_OPEN_EGG:
                        switch (select) {
                            case 0:
                                this.createOtherMenu(player, ConstNpc.CONFIRM_OPEN_EGG,
                                        "Bạn có chắc chắn cho trứng nở?\n"
                                        + "Đệ tử của bạn sẽ được thay thế bằng đệ Mabư",
                                        "Đệ mabư\nTrái Đất", "Đệ mabư\nNamếc", "Đệ mabư\nXayda",
                                        "Từ chối");
                                break;
                            case 1:
                                this.createOtherMenu(player, ConstNpc.CONFIRM_DESTROY_EGG,
                                        "Bạn có chắc chắn muốn hủy bỏ trứng Mabư?", "Đồng ý",
                                        "Từ chối");
                                break;
                        }
                        break;
                    case ConstNpc.CONFIRM_OPEN_EGG:
                        switch (select) {
                            case 0:
                                player.mabuEgg.openEgg(ConstPlayer.TRAI_DAT);
                                break;
                            case 1:
                                player.mabuEgg.openEgg(ConstPlayer.NAMEC);
                                break;
                            case 2:
                                player.mabuEgg.openEgg(ConstPlayer.XAYDA);
                                break;
                            default:
                                break;
                        }
                        break;
                    case ConstNpc.CONFIRM_DESTROY_EGG:
                        if (select == 0) {
                            player.mabuEgg.destroyEgg();
                        }
                        break;
                }
            }
            if (this.mapId == 154) {
                switch (player.iDMark.getIndexMenu()) {
                    case ConstNpc.CAN_NOT_OPEN_EGG:
                        switch (select) {
                            case 0:
                                this.createOtherMenu(player, ConstNpc.CONFIRM_DESTROY_EGG,
                                        "Bạn có chắc chắn muốn hủy bỏ trứng Bill?", "Đồng ý", "Từ chối");
                                break;
                            case 1:
                                Item gayWhist = InventoryService.gI().findItem(player.inventory.itemsBag, 1231);
                                if (gayWhist != null) {
                                    InventoryService.gI().subQuantityItemsBag(player, gayWhist, 1);
                                    player.billEgg.timeDone = 0;
                                    InventoryService.gI().sendItemBags(player);
                                    player.billEgg.sendBillEgg();
                                } else {
                                    Service.getInstance().sendThongBao(player, "Gay whist cua may dau?");
                                }
                                break;
                            default:
                                break;
                        }
                        break;
                    case ConstNpc.CAN_OPEN_EGG:
                        switch (select) {
                            case 0:
                                this.createOtherMenu(player, ConstNpc.CONFIRM_OPEN_EGG,
                                        "Bạn có chắc chắn cho trứng nở?\n"
                                        + "Đệ tử của bạn sẽ được thay thế bằng đệ Bill",
                                        "Đệ mabư\nTrái Đất", "Đệ mabư\nNamếc", "Đệ mabư\nXayda",
                                        "Từ chối");
                                break;
                            case 1:
                                this.createOtherMenu(player, ConstNpc.CONFIRM_DESTROY_EGG,
                                        "Bạn có chắc chắn muốn hủy bỏ trứng Bill?", "Đồng ý",
                                        "Từ chối");
                                break;
                        }
                        break;
                    case ConstNpc.CONFIRM_OPEN_EGG:
                        switch (select) {
                            case 0:
                                player.billEgg.openEggBill(ConstPlayer.TRAI_DAT);
                                break;
                            case 1:
                                player.billEgg.openEggBill(ConstPlayer.NAMEC);
                                break;
                            case 2:
                                player.billEgg.openEggBill(ConstPlayer.XAYDA);
                                break;
                            default:
                                break;
                        }
                        break;
                    case ConstNpc.CONFIRM_DESTROY_EGG:
                        if (select == 0) {
                            player.billEgg.destroyEggBill();
                        }
                        break;
                }
            }
        }
    }
}
