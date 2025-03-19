package com.nro.nro_online.server.io;

import com.nro.nro_online.data.DataGame;
import com.nro.nro_online.jdbc.daos.GodGK;
import com.nro.nro_online.manager.SieuHangManager;
import com.nro.nro_online.manager.TopCoin;
import com.nro.nro_online.models.item.Item;
import com.nro.nro_online.models.item.ItemOption;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.resources.Resources;
import com.nro.nro_online.server.Client;
import com.nro.nro_online.server.Controller;
import com.nro.nro_online.server.Maintenance;
import com.nro.nro_online.server.Manager;
import com.nro.nro_online.server.ServerManager;
import com.nro.nro_online.services.ItemService;
import com.nro.nro_online.services.ItemTimeService;
import com.nro.nro_online.services.RewardService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.TaskService;
import com.nro.nro_online.utils.Log;
import com.nro.nro_online.utils.Util;
import lombok.Setter;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Session {
    public static final Map<String, AntiLogin> ANTI_LOGIN_MAP = new HashMap<>();
    public static final int TIME_WAIT_READ_MESSAGE = 180_000;
    public static final byte[] KEYS = {0};
    public static int baseId = 0;

    // Instance fields
    public final int id;
    public Player player;
    public byte timeWait = 50;
    public boolean connected;
    public byte curR, curW;
    public String ipAddress;
    public boolean isAdmin;
    public int userId;
    public String uu, pp;
    public int typeClient;
    public byte zoomLevel;
    public boolean isSetClientType;
    public long lastTimeLogout, lastTimeReadMessage;
    public boolean actived, loginSuccess, joinedGame, dataLoadFailed;
    public int goldBar, vnd, ruby, diemTichNap, server, version, poinCharging;
    public boolean logCheck;
    public List<Item> itemsReward;
    public String dataReward;

    @Setter
    private boolean logging;

    public Socket socket;
    public Controller controller;
    public Thread sendThread, receiveThread;
    public MessageCollector collector;
    public MessageSender sender;

    public Session(Socket socket, Controller controller, String ip) {
        this.id = baseId++;
        this.socket = socket;
        this.controller = controller;
        this.ipAddress = ip;

        try {
            socket.setTcpNoDelay(true);
            this.sender = new MessageSender(this, socket);
            this.collector = new MessageCollector(this, socket);
            this.sendThread = new Thread(sender, "Send " + ip);
            this.receiveThread = new Thread(collector, "Receive " + ip);
            this.receiveThread.start();
            Client.gI().put(this);
        } catch (Exception e) {
            Log.error(Session.class, e);
            throw new RuntimeException("Failed to initialize session", e);
        }
    }

    public void update() {
        if (Util.canDoWithTime(lastTimeReadMessage, TIME_WAIT_READ_MESSAGE)) {
            // Client.gI().kickSession(this); // Uncomment if needed
        }
    }

    public void initItemsReward() {
        if (dataReward == null || dataReward.isEmpty()) return;

        itemsReward = new ArrayList<>();
        try {
            for (String itemInfo : dataReward.split(";")) {
                if (itemInfo.isEmpty()) continue;

                String[] subItemInfo = itemInfo.replaceAll("[{}\\[\\]]", "").split("\\|");
                String[] baseInfo = subItemInfo[0].split(":");
                int itemId = Integer.parseInt(baseInfo[0]);
                int quantity = Integer.parseInt(baseInfo[1]);

                Item item = ItemService.gI().createNewItem((short) itemId, quantity);
                if (subItemInfo.length == 2) {
                    for (String opt : subItemInfo[1].split(",")) {
                        if (opt.isEmpty()) continue;
                        String[] optInfo = opt.split(":");
                        item.itemOptions.add(new ItemOption(Integer.parseInt(optInfo[0]), Integer.parseInt(optInfo[1])));
                    }
                }
                itemsReward.add(item);
            }
        } catch (Exception e) {
            Log.error(this.getClass(), e);
            itemsReward.clear(); // Reset nếu lỗi
        }
    }

    public int getNumOfMessages() {
        return sender != null ? sender.getNumMessage() : 0;
    }

    public void sendMessage(Message msg) {
        if (sender != null) sender.addMessage(msg);
    }

    public void doSendMessage(Message msg) {
        if (sender != null) sender.doSendMessage(msg);
    }

    public void disconnect() {
        if (!connected) return;

        connected = false;
        curR = curW = 0;
        player = null;

        try {
            if (sender != null) sender.close();
            if (collector != null) collector.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (Exception e) {
            Log.error(Session.class, e);
        } finally {
            this.socket = null; // Xóa reference
            this.sender = null;
            this.collector = null;
            this.sendThread = null;
            this.receiveThread = null;
            this.uu = this.pp = null;
            this.itemsReward = null;
        }
    }

    public void setClientType(Message msg) {
        if (isSetClientType) return;

        try (msg) { // Auto-close message
            typeClient = msg.reader().readByte();
            zoomLevel = msg.reader().readByte();
            msg.reader().readBoolean(); // is_gprs
            msg.reader().readInt(); // width
            msg.reader().readInt(); // height
            msg.reader().readBoolean(); // is_qwerty
            msg.reader().readBoolean(); // is_touch
            String platform = msg.reader().readUTF();
            version = Integer.parseInt(platform.split("\\|")[1].replaceAll("\\.", ""));
            isSetClientType = true;
            Resources.getInstance().sendResVersion(this);
            DataGame.sendLinkIP(this);
        } catch (Exception e) {
            Log.error(Session.class, e);
        }
    }

    public boolean isVersionAbove(int version) {
        return this.version >= version;
    }

    public String getName() {
        return player != null ? player.name : String.valueOf(socket.getPort());
    }

    public void sendSessionKey() {
        if (sender != null) sender.sendSessionKey();
    }

    public boolean canConnectWithIp() {
        return ServerManager.clients.compute(ipAddress, (key, count) ->
                count == null ? 1 : (count < Manager.MAX_PER_IP ? count + 1 : null)) != null;
    }

    public void login(String username, String password) {
        if (Maintenance.isRuning || !ServerManager.gI().getLogin().isConnected()) {
            Service.getInstance().sendThongBaoOK(this, "Máy chủ đang bảo trì, vui lòng thử lại sau!");
            return;
        }
        if (!isSetClientType || logging || loginSuccess) return;

        logging = true;
        AntiLogin al = ANTI_LOGIN_MAP.computeIfAbsent(ipAddress, k -> new AntiLogin());
        if (!al.canLogin()) {
            Service.getInstance().sendThongBaoOK(this, al.getNotifyCannotLogin());
            return;
        }
        if (!isAdmin && Client.gI().getPlayers().size() >= Manager.MAX_PLAYER) {
            Service.getInstance().sendThongBaoOK(this, "Máy chủ quá tải, vui lòng chọn máy chủ khác.");
            return;
        }

        this.uu = username;
        this.pp = password;
        try {
            ServerManager.gI().getLogin().getService().login(Manager.SERVER, this.id, username, password);
        } catch (Exception e) {
            Log.error(Session.class, e);
        } finally {
            logging = false;
        }
    }

    public void finishUpdate() {
        if (!loginSuccess || joinedGame) return;

        player = GodGK.loadPlayer(this);
        if (dataLoadFailed) {
            Service.getInstance().sendThongBaoOK(this, "Lỗi tải dữ liệu, vui lòng báo admin.");
        } else if (player != null) {
            enter();
        } else {
            Service.getInstance().switchToCreateChar(this);
        }
    }

    public void enter() {
        if (joinedGame) return;

        joinedGame = true;
        initializePlayer(player);
        if (player.pet != null) initializePlayer(player.pet);

        player.setSession(this);
        Client.gI().put(player);
        controller.sendInfo(this);

        Service service = Service.getInstance();
        service.player(player);
        service.Send_Caitrang(player);
        service.sendFlagBag(player);
        player.playerSkill.sendSkillShortCut();
        ItemTimeService.gI().sendAllItemTime(player);
        TaskService.gI().sendInfoCurrentTask(player);

        if (isAdmin) {
            service.sendThongBaoAllPlayer("|7|ADMIN VỪA VÔ GAME MẤY ĐỨA BUG CẨN THẬN!");
        }

        checkTop(player);
        RewardService.gI().rewardFirstTimeLoginPerDay(player);
        SieuHangManager.GetRewardDay(player);
    }

    private void initializePlayer(Player p) {
        p.nPoint.initPowerLimit();
        p.nPoint.calPoint();
        p.nPoint.setHp(p.nPoint.hp);
        p.nPoint.setMp(p.nPoint.mp);
        p.zone.addPlayer(p);
        p.loaded = true;
    }

    private void checkTop(Player player) {
        String topPlayer = TopCoin.getInstance().getNamePlayer();
        if (!Util.isNullOrEmpty(topPlayer) && Objects.equals(player.name, topPlayer)) {
            Service.getInstance().sendThongBaoAllPlayer(
                    "[THÔNG BÁO] Top 1 hiến máu " + player.name + " vừa đăng nhập vào game");
        }
    }
}