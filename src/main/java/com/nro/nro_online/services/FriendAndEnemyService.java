package com.nro.nro_online.services;

import com.nro.nro_online.consts.Cmd;
import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.models.Part;
import com.nro.nro_online.models.PartManager;
import com.nro.nro_online.models.map.war.NamekBallWar;
import com.nro.nro_online.models.player.Enemy;
import com.nro.nro_online.models.player.Friend;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.server.Client;
import com.nro.nro_online.server.io.Message;
import com.nro.nro_online.services.func.ChangeMapService;
import com.nro.nro_online.services.func.PVPServcice;
import com.nro.nro_online.utils.Log;
import com.nro.nro_online.utils.Util;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class FriendAndEnemyService {
    private static final byte OPEN_LIST = 0;
    private static final byte MAKE_FRIEND = 1;
    private static final byte REMOVE_FRIEND = 2;
    private static final byte REVENGE = 1;
    private static final byte REMOVE_ENEMY = 2;
    private static final int MAX_FRIENDS = 10; // Giới hạn bạn, khỏi hardcode lung tung 😜
    private static final List<Integer> NO_TELEPORT_MAPS = Arrays.asList(
            114, 115, 116, 117, 118, 119, 120, 121, 122, 123,
            53, 54, 55, 56, 57, 58, 59, 60, 61, 62,
            160, 161, 162, 163, 164, 124, 125, 126, 127, 128, 155,
            206, 207, 208, 209, 210, 211
    ); // Map không tele được, gom lại cho gọn 🚀

    private static final FriendAndEnemyService instance = new FriendAndEnemyService();

    public static FriendAndEnemyService gI() {
        return instance; // Singleton đơn giản, khỏi khởi tạo nhiều lần 😎
    }

    public void controllerFriend(Player player, Message msg) {
        try {
            byte action = msg.reader().readByte();
            switch (action) {
            case OPEN_LIST -> openListFriend(player);
            case MAKE_FRIEND -> makeFriend(player, msg.reader().readInt());
            case REMOVE_FRIEND -> removeFriend(player, msg.reader().readInt());
            }
        } catch (IOException e) {
            Log.error(FriendAndEnemyService.class, e, "Lỗi xử lý friend, chill đi! 😅");
        }
    }

    public void controllerEnemy(Player player, Message msg) {
        try {
            byte action = msg.reader().readByte();
            switch (action) {
            case OPEN_LIST -> openListEnemy(player);
            case REVENGE -> handleRevenge(player, msg.reader().readInt());
            case REMOVE_ENEMY -> removeEnemy(player, msg.reader().readInt());
            }
        } catch (IOException e) {
            Log.error(FriendAndEnemyService.class, e, "Lỗi xử lý enemy, xui thật! 😢");
        }
    }

    private void reloadFriend(Player player) {
        player.friends.forEach(f -> updatePlayerInfo(f, Client.gI().getPlayer(f.id)));
    }

    private void reloadEnemy(Player player) {
        player.enemies.forEach(e -> updatePlayerInfo(e, Client.gI().getPlayer(e.id)));
    }

    private void updatePlayerInfo(Friend foe, Player pl) {
        foe.online = pl != null;
        if (pl != null) {
            foe.power = pl.nPoint.power;
            foe.head = pl.getHead();
            foe.body = pl.getBody();
            foe.leg = pl.getLeg();
            foe.bag = (byte) pl.getFlagBag();
        }
    }

    private void updatePlayerInfo(Enemy foe, Player pl) {
        foe.online = pl != null;
        if (pl != null) {
            foe.power = pl.nPoint.power;
            foe.head = pl.getHead();
            foe.body = pl.getBody();
            foe.leg = pl.getLeg();
            foe.bag = (byte) pl.getFlagBag();
        }
    }

    private void openListFriend(Player player) {
        reloadFriend(player);
        try (Message msg = new Message(Cmd.FRIEND)) {
            msg.writer().writeByte(OPEN_LIST);
            msg.writer().writeByte(player.friends.size());
            for (Friend f : player.friends) {
                writeFriendData(msg, f, player.isVersionAbove(220));
            }
            player.sendMessage(msg);
        } catch (Exception e) {
            Log.error(FriendAndEnemyService.class, e, "Lỗi mở list friend, buồn ghê! 😭");
        }
    }

    private void openListEnemy(Player player) {
        reloadEnemy(player);
        try (Message msg = new Message(-99)) {
            msg.writer().writeByte(OPEN_LIST);
            msg.writer().writeByte(player.enemies.size());
            for (Enemy e : player.enemies) {
                writeEnemyData(msg, e, player.isVersionAbove(220));
            }
            player.sendMessage(msg);
        } catch (Exception e) {
            Log.error(FriendAndEnemyService.class, e, "Lỗi mở list enemy, xui quá! 😅");
        }
    }

    private void writeFriendData(Message msg, Friend f, boolean isVersionAbove220) throws IOException {
        msg.writer().writeInt(f.id);
        msg.writer().writeShort(f.head);
        if (isVersionAbove220) msg.writer().writeShort(PartManager.getInstance().find(f.head).getIcon(0));
        msg.writer().writeShort(f.body);
        msg.writer().writeShort(f.leg);
        msg.writer().writeByte(f.bag);
        msg.writer().writeUTF(f.name);
        msg.writer().writeBoolean(f.online);
        msg.writer().writeUTF(Util.numberToMoney(f.power));
    }

    private void writeEnemyData(Message msg, Enemy e, boolean isVersionAbove220) throws IOException {
        msg.writer().writeInt(e.id);
        msg.writer().writeShort(e.head);
        if (isVersionAbove220) msg.writer().writeShort(PartManager.getInstance().find(e.head).getIcon(0));
        msg.writer().writeShort(e.body);
        msg.writer().writeShort(e.leg);
        msg.writer().writeShort(e.bag);
        msg.writer().writeUTF(e.name);
        msg.writer().writeUTF(Util.numberToMoney(e.power));
        msg.writer().writeBoolean(e.online);
    }

    private void makeFriend(Player player, int playerId) {
        if (player.friends.size() >= MAX_FRIENDS) {
            Service.getInstance().sendThongBao(player, "Đủ 10 thằng bạn rồi, thêm nữa lag lắm! 😂");
            return;
        }
        if (player.friends.stream().anyMatch(f -> f.id == playerId)) {
            Service.getInstance().sendThongBao(player, "Thằng này là bạn mày rồi mà! 😛");
            return;
        }
        Player pl = Client.gI().getPlayer(playerId);
        if (pl != null) {
            String npcSay = player.friends.size() >= 5 ?
                    "Kết bạn với " + pl.name + " mất 5 ngọc, chịu không?" :
                    "Kết bạn với " + pl.name + " miễn phí nè, chịu không?";
            NpcService.gI().createMenuConMeo(player, ConstNpc.MAKE_FRIEND, -1, npcSay, new String[]{"Đồng ý", "Từ chối"}, playerId);
        }
    }

    private void removeFriend(Player player, int playerId) {
        Friend friend = player.friends.stream().filter(f -> f.id == playerId).findFirst().orElse(null);
        if (friend != null) {
            player.friends.remove(friend);
            Service.getInstance().sendThongBao(player, "Xóa " + friend.name + " khỏi list bạn thành công! 👋");
            try (Message msg = new Message(Cmd.FRIEND)) {
                msg.writer().writeByte(REMOVE_FRIEND);
                msg.writer().writeInt(friend.id);
                player.sendMessage(msg);
            } catch (Exception e) {
                Log.error(FriendAndEnemyService.class, e, "Lỗi xóa friend, xui ghê! 😢");
            }
        }
    }

    private void handleRevenge(Player player, int enemyId) {
        if (player.enemies.stream().noneMatch(e -> e.id == enemyId)) {
            Service.getInstance().sendThongBao(player, "Thằng này không phải kẻ thù, báo thù cái gì? 😅");
            return;
        }
        Player enemy = Client.gI().getPlayer(enemyId);
        if (enemy != null) {
            PVPServcice.gI().openSelectRevenge(player, enemy);
        } else {
            Service.getInstance().sendThongBao(player, "Thằng này offline rồi, chờ nó on nha! 😛");
        }
    }

    public void removeEnemy(Player player, int playerId) {
        player.enemies.removeIf(e -> e.id == playerId);
        openListEnemy(player); // Cập nhật list ngay, nhanh như chớp! ⚡
    }

    public void chatPrivate(Player player, Message msg) {
        if (!Util.canDoWithTime(player.lastTimeChatPrivate, 5000)) return;
        player.lastTimeChatPrivate = System.currentTimeMillis();
        try {
            int playerId = msg.reader().readInt();
            String text = msg.reader().readUTF();
            Player pl = Client.gI().getPlayer(playerId);
            if (pl != null) Service.getInstance().chatPrivate(player, pl, text);
        } catch (Exception e) {
            Log.error(FriendAndEnemyService.class, e, "Chat private lỗi, buồn ghê! 😭");
        }
    }

    public void acceptMakeFriend(Player player, int playerId) {
        Player pl = Client.gI().getPlayer(playerId);
        if (pl == null) {
            Service.getInstance().sendThongBao(player, "Thằng này offline rồi, kết bạn sau nha! 😢");
            return;
        }
        Friend friend = new Friend();
        friend.id = (int) pl.id;
        friend.name = pl.name;
        friend.power = pl.nPoint.power;
        friend.head = pl.getHead();
        friend.body = pl.getBody();
        friend.leg = pl.getLeg();
        friend.bag = (byte) pl.getFlagBag();
        player.friends.add(friend);
        Service.getInstance().sendThongBao(player, "Kết bạn với " + pl.name + " thành công! 🎉");
        Service.getInstance().chatPrivate(player, pl, player.name + " vừa kết bạn với mày nè!");
        TaskService.gI().checkDoneTaskMakeFriend(player, pl);
    }

    public void goToPlayerWithYardrat(Player player, Message msg) {
        try {
            Player target = Client.gI().getPlayer(msg.reader().readInt());
            if (target == null) return;
            if (player.isHoldNamecBall) {
                NamekBallWar.gI().dropBall(player);
                return;
            }
            if (!player.isAdmin() && !player.nPoint.teleport) {
                Service.getInstance().sendThongBao(player, "Mày cần đồ dịch chuyển tức thời mới tele được! 😛");
                return;
            }
            if (NO_TELEPORT_MAPS.contains(target.zone.map.mapId)) {
                Service.getInstance().sendThongBao(player, "Map này không tele được, chịu khó đi bộ nha! 😂");
                return;
            }
            if (!player.isAdmin() && target.itemTime.isUseAnDanh) {
                Service.getInstance().sendThongBao(player, "Thằng này ẩn danh, không tìm được! 😢");
                return;
            }
            if (!player.isAdmin() && target.zone.isFullPlayer()) {
                Service.getInstance().sendThongBao(player, "Map full người rồi, đợi tí nha! 😅");
                return;
            }
            ChangeMapService.gI().changeMapYardrat(player, target.zone, target.location.x + Util.nextInt(-5, 5), target.location.y);
        } catch (IOException e) {
            Log.error(FriendAndEnemyService.class, e, "Tele lỗi rồi, xui ghê! 😭");
        }
    }

    public void addEnemy(Player player, Player enemy) {
        if (player.enemies.stream().noneMatch(e -> e.id == enemy.id)) {
            Enemy e = new Enemy();
            e.id = (int) enemy.id;
            e.name = enemy.name;
            e.power = enemy.nPoint.power;
            e.head = enemy.getHead();
            e.body = enemy.getBody();
            e.leg = enemy.getLeg();
            e.bag = (byte) enemy.getFlagBag();
            player.enemies.add(e);
        }
    }
}