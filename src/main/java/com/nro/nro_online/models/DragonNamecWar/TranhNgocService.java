/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nro.nro_online.models.DragonNamecWar;

import java.util.List;

import nro.consts.ConstTranhNgocNamek;
import nro.models.item.Item;
import nro.models.map.ItemMap;
import nro.models.player.Player;
import nro.server.ServerManager;
import nro.server.io.Message;
import nro.services.InventoryService;
import nro.services.ItemMapService;
import nro.services.ItemService;
import nro.services.Service;
import nro.utils.Util;

/**
 *
 * @Build Arriety
 */
public class TranhNgocService {

    private static TranhNgocService instance;

    public static TranhNgocService getInstance() {
        if (instance == null) {
            instance = new TranhNgocService();
        }
        return instance;
    }

    public void sendCreatePhoBan(Player pl) {
        Message msg;
        try {
            msg = new Message(20);
            msg.writer().writeByte(0);
            msg.writer().writeByte(0);
            msg.writer().writeShort(ConstTranhNgocNamek.MAP_ID);
            msg.writer().writeUTF(ConstTranhNgocNamek.CADIC); // team 1
            msg.writer().writeUTF(ConstTranhNgocNamek.FIDE); // team 2
            msg.writer().writeInt(ConstTranhNgocNamek.MAX_LIFE);
            msg.writer().writeShort(ConstTranhNgocNamek.TIME_SECOND);
            msg.writer().writeByte(ConstTranhNgocNamek.MAX_POINT);
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendUpdateLift(Player pl) {
        Message msg;
        try {
            final TranhNgoc tn = ServerManager.gI().getTranhNgocManager().findByPLayerId(pl.id);
            if (tn != null) {
                msg = new Message(20);
                msg.writer().writeByte(0);
                msg.writer().writeByte(1);
                msg.writer().writeInt((int) tn.getPlayersCadic().stream().filter(p -> p != null && !p.isDie()).count());
                msg.writer().writeInt((int) tn.getPlayersFide().stream().filter(p -> p != null && !p.isDie()).count());
                Service.getInstance().sendMessAllPlayerInMap(pl.zone, msg);
                msg.cleanup();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendEndPhoBan(TranhNgoc zone, byte type, boolean isFide) {
        Message msg;
        try {
            msg = new Message(20);
            msg.writer().writeByte(0);
            msg.writer().writeByte(2);
            msg.writer().writeByte(type);
            if (zone != null) {
                List<Player> players = isFide ? zone.getPlayersFide() : zone.getPlayersCadic();
                synchronized (players) {
                    for (Player pl : players) {
                        if (pl != null) {
                            pl.sendMessage(msg);
                        }
                    }
                }
                msg.cleanup();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendUpdateTime(Player pl, short second) {
        Message msg;
        try {
            msg = new Message(20);
            msg.writer().writeByte(0);
            msg.writer().writeByte(5);
            msg.writer().writeShort(second);
            Service.getInstance().sendMessAllPlayerInMap(pl.zone, msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendUpdatePoint(Player pl) {
        Message msg;
        try {
            TranhNgoc tn = ServerManager.gI().getTranhNgocManager().findByPLayerId(pl.id);

            msg = new Message(20);
            msg.writer().writeByte(0);
            msg.writer().writeByte(4);
            msg.writer().writeByte(tn.pointCadic);
            msg.writer().writeByte(tn.pointFide);
            Service.getInstance().sendMessAllPlayerInMap(pl.zone, msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void givePrice(List<Player> players, byte type, int point) {
        switch (type) {
            case ConstTranhNgocNamek.LOSE:
                int pointDiff = ConstTranhNgocNamek.MAX_POINT - point;
                for (Player pl : players) {
                    if (pl != null) {
                        Item mcl = InventoryService.gI().findItemBagByTemp(pl, 2000);
                        if (mcl != null) {
                            InventoryService.gI().subQuantityItemsBag(pl, mcl, pointDiff);
                            InventoryService.gI().sendItemBags(pl);
                            Service.getInstance().sendThongBao(pl, "Haizzz thật là nhục nhã\nkiểu này sao báo lại với đội trưởng đây");
                        } else {
                            Service.getInstance().sendThongBao(pl, "Bạn không có mảnh chiến lực\nnên tôi xóa đi 1 item trong túi bạn");
                        }
                    }
                }
                break;
            case ConstTranhNgocNamek.WIN:
                for (Player pl : players) {
                    if (pl != null) {
                        Item mcl = ItemService.gI().createNewItem((short) 2000);
                        mcl.quantity = point;
                        InventoryService.gI().addItemBag(pl, mcl, 0);
                        InventoryService.gI().sendItemBags(pl);
                        Service.getInstance().sendThongBao(pl, "Chúc mừng bạn đã nhận được " + mcl.template.name);
                    }
                }
                break;
//            case ConstTranhNgocNamek.DRAW:
//                for (Player pl : players) {
//                    if (pl != null) {
//                        Item mcl = ItemService.gI().createNewItem((short) 2000);
//                        mcl.quantity = point;
//                        InventoryService.gI().addItemBag(pl, mcl, 99999);
//                        InventoryService.gI().sendItemBags(pl);
//                        TranhNgoc.gI().removePlayersCadic(pl);
//                        TranhNgoc.gI().removePlayersFide(pl);
//                    }
//                }
//                break;
            default:
                break;
        }
    }

    public void pickBall(Player player, ItemMap item) {
        final TranhNgoc tn = ServerManager.gI().getTranhNgocManager().findByPLayerId(player.id);
        if (player.isHoldNamecBallTranhDoat || (!tn.isCadic(player) && !tn.isFide(player))) {
            return;
        }
        if (item.typeHaveBallTranhDoat != -1 && (tn.isCadic(player) || tn.isFide(player))) {
            if (tn.isCadic(player)) {
                tn.pointFide--;
            } else if (tn.isFide(player)) {
                tn.pointCadic--;
            }
            sendUpdatePoint(player);
        }
        player.tempIdNamecBallHoldTranhDoat = item.itemTemplate.id;
        player.isHoldNamecBallTranhDoat = true;
        ItemMapService.gI().removeItemMapAndSendClient(item);
        Service.getInstance().sendFlagBag(player);
        Service.getInstance().sendThongBao(player, "Bạn đang giữ viên ngọc rồng Namek");
    }

    public void dropBall(Player player, byte a) {
        if (player.tempIdNamecBallHoldTranhDoat != -1) {
            player.isHoldNamecBallTranhDoat = false;
        }
        int x = Util.nextInt(20, player.zone.map.mapWidth);
        int y = player.zone.map.yPhysicInTop(x, player.zone.map.mapHeight / 2);
        ItemMap itemMap = new ItemMap(player.zone, player.tempIdNamecBallHoldTranhDoat, 1, x, y, -1);
        itemMap.isNamecBallTranhDoat = true;
        itemMap.typeHaveBallTranhDoat = a;
        itemMap.x = player.location.x;
        itemMap.y = player.location.y;
        Service.getInstance().dropItemMap(player.zone, itemMap);
        Service.getInstance().sendFlagBag(player);
        player.tempIdNamecBallHoldTranhDoat = -1;
    }
}
