package com.nro.nro_online.models.dragon_namec_war;

import com.nro.nro_online.consts.ConstTranhNgocNamek;
import com.nro.nro_online.models.item.Item;
import com.nro.nro_online.models.map.ItemMap;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.server.ServerManager;
import com.nro.nro_online.server.io.Message;
import com.nro.nro_online.services.InventoryService;
import com.nro.nro_online.services.ItemMapService;
import com.nro.nro_online.services.ItemService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.utils.Util;

import java.util.List;

public class TranhNgocService {

    private static final TranhNgocService INSTANCE = new TranhNgocService();

    public static TranhNgocService getInstance() {
        return INSTANCE;
    }

    private TranhNgocService() {}

    public void sendCreatePhoBan(Player pl) {
        sendMessage(pl, 20, msg -> {
            msg.writer().writeByte(0);
            msg.writer().writeByte(0);
            msg.writer().writeShort(ConstTranhNgocNamek.MAP_ID);
            msg.writer().writeUTF(ConstTranhNgocNamek.CADIC);
            msg.writer().writeUTF(ConstTranhNgocNamek.FIDE);
            msg.writer().writeInt(ConstTranhNgocNamek.MAX_LIFE);
            msg.writer().writeShort(ConstTranhNgocNamek.TIME_SECOND);
            msg.writer().writeByte(ConstTranhNgocNamek.MAX_POINT);
        });
    }

    public void sendUpdateLift(Player pl) {
        TranhNgoc tn = getTranhNgoc(pl);
        if (tn != null) {
            sendMessageToZone(pl.zone, 20, msg -> {
                msg.writer().writeByte(0);
                msg.writer().writeByte(1);
                msg.writer().writeInt((int) tn.getPlayersCadic().stream().filter(p -> p != null && !p.isDie()).count());
                msg.writer().writeInt((int) tn.getPlayersFide().stream().filter(p -> p != null && !p.isDie()).count());
            });
        }
    }

    public void sendEndPhoBan(TranhNgoc zone, byte type, boolean isFide) {
        if (zone == null) return;
        List<Player> players = isFide ? zone.getPlayersFide() : zone.getPlayersCadic();
        sendMessageToPlayers(players, 20, msg -> {
            msg.writer().writeByte(0);
            msg.writer().writeByte(2);
            msg.writer().writeByte(type);
        });
    }

    public void sendUpdateTime(Player pl, short second) {
        sendMessageToZone(pl.zone, 20, msg -> {
            msg.writer().writeByte(0);
            msg.writer().writeByte(5);
            msg.writer().writeShort(second);
        });
    }

    public void sendUpdatePoint(Player pl) {
        TranhNgoc tn = getTranhNgoc(pl);
        if (tn != null) {
            sendMessageToZone(pl.zone, 20, msg -> {
                msg.writer().writeByte(0);
                msg.writer().writeByte(4);
                msg.writer().writeByte(tn.pointCadic);
                msg.writer().writeByte(tn.pointFide);
            });
        }
    }

    public void givePrice(List<Player> players, byte type, int point) {
        switch (type) {
        case ConstTranhNgocNamek.LOSE -> handleLose(players, point);
        case ConstTranhNgocNamek.WIN -> handleWin(players, point);
        default -> {}
        }
    }

    private void handleLose(List<Player> players, int point) {
        int pointDiff = ConstTranhNgocNamek.MAX_POINT - point;
        for (Player pl : players) {
            if (pl == null) continue;
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

    private void handleWin(List<Player> players, int point) {
        for (Player pl : players) {
            if (pl == null) continue;
            Item mcl = ItemService.gI().createNewItem((short) 2000, point);
            InventoryService.gI().addItemBag(pl, mcl, 0);
            InventoryService.gI().sendItemBags(pl);
            Service.getInstance().sendThongBao(pl, "Chúc mừng bạn đã nhận được " + mcl.template.name);
        }
    }

    public void pickBall(Player player, ItemMap item) {
        TranhNgoc tn = getTranhNgoc(player);
        if (player.isHoldNamecBallTranhDoat || (!tn.isCadic(player) && !tn.isFide(player))) return;

        if (item.typeHaveBallTranhDoat != -1 && (tn.isCadic(player) || tn.isFide(player))) {
            if (tn.isCadic(player)) tn.pointFide--;
            else tn.pointCadic--;
            sendUpdatePoint(player);
        }

        player.tempIdNamecBallHoldTranhDoat = item.itemTemplate.id;
        player.isHoldNamecBallTranhDoat = true;
        ItemMapService.gI().removeItemMapAndSendClient(item);
        Service.getInstance().sendFlagBag(player);
        Service.getInstance().sendThongBao(player, "Bạn đang giữ viên ngọc rồng Namek");
    }

    public void dropBall(Player player, byte a) {
        if (player.tempIdNamecBallHoldTranhDoat == -1) return;

        player.isHoldNamecBallTranhDoat = false;
        int x = Util.nextInt(20, player.zone.map.mapWidth);
        int y = player.zone.map.yPhysicInTop(x, player.zone.map.mapHeight / 2);
        ItemMap itemMap = new ItemMap(player.zone, player.tempIdNamecBallHoldTranhDoat, 1, x, y, -1);
        itemMap.isNamecBallTranhDoat = true;
        itemMap.typeHaveBallTranhDoat = a;

        Service.getInstance().dropItemMap(player.zone, itemMap);
        Service.getInstance().sendFlagBag(player);
        player.tempIdNamecBallHoldTranhDoat = -1;
    }

    private TranhNgoc getTranhNgoc(Player pl) {
        return ServerManager.gI().getTranhNgocManager().findByPLayerId(pl.id);
    }

    private void sendMessage(Player pl, int cmd, MessageConsumer consumer) {
        try (Message msg = new Message(cmd)) {
            consumer.accept(msg);
            pl.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMessageToZone(com.nro.nro_online.models.map.Zone zone, int cmd, MessageConsumer consumer) {
        try (Message msg = new Message(cmd)) {
            consumer.accept(msg);
            Service.getInstance().sendMessAllPlayerInMap(zone, msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMessageToPlayers(List<Player> players, int cmd, MessageConsumer consumer) {
        try (Message msg = new Message(cmd)) {
            consumer.accept(msg);
            synchronized (players) {
                for (Player pl : players) {
                    if (pl != null) pl.sendMessage(msg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FunctionalInterface
    private interface MessageConsumer {
        void accept(Message msg) throws Exception;
    }
}