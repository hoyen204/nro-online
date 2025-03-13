package com.nro.nro_online.server.io;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.nro.nro_online.data.DataGame;
import com.nro.nro_online.manager.SieuHangManager;
import com.nro.nro_online.manager.TopCoin;
import com.nro.nro_online.models.item.Item;
import com.nro.nro_online.models.item.ItemOption;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.server.Client;
import com.nro.nro_online.server.Controller;
import com.nro.nro_online.services.ItemService;
import com.nro.nro_online.services.ItemTimeService;
import com.nro.nro_online.services.RewardService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.TaskService;
import com.nro.nro_online.utils.Log;
import com.nro.nro_online.utils.Util;
import lombok.Setter;

public class Session {

    private static final Map<String, AntiLogin> ANTI_LOGIN_MAP = new HashMap<>();

    private static final int TIME_WAIT_READ_MESSAGE = 180_000;

    public int poinCharging;
    public boolean logCheck;

    private static int baseId = 0;
    public int id;
    public Player player;

    public byte timeWait = 50;

    public boolean connected;

    static final byte[] KEYS = {0};
    byte curR, curW;

    private Socket socket;
    Thread sendThread;
    Thread receiveThread;
    private MessageCollector collector;
    private MessageSender sender;

    Controller controller;

    public String ipAddress;
    public boolean isAdmin;
    public int userId;
    public String uu;
    public String pp;

    public int typeClient;
    public byte zoomLevel;
    public boolean isSetClientType;

    public long lastTimeLogout;
    public boolean loginSuccess, joinedGame, dataLoadFailed;

    public long lastTimeReadMessage;

    public boolean actived;

    public int goldBar;

    public int vnd;

    public List<Item> itemsReward;
    public String dataReward;
    public int ruby;
    public int diemTichNap;
    public int server;// server account hiện tại
    public int version;

    @Setter
    private boolean logging;

    public void update() {
        if (Util.canDoWithTime(lastTimeReadMessage, TIME_WAIT_READ_MESSAGE)) {
//            Client.gI().kickSession(this);
        }
    }

    public void initItemsReward() {
        try {
            this.itemsReward = new ArrayList<>();
            String[] itemsReward = dataReward.split(";");
            for (String itemInfo : itemsReward) {
                if (itemInfo == null || itemInfo.equals("")) {
                    continue;
                }
                String[] subItemInfo = itemInfo.replaceAll("[{}\\[\\]]", "").split("\\|");
                String[] baseInfo = subItemInfo[0].split(":");
                int itemId = Integer.parseInt(baseInfo[0]);
                int quantity = Integer.parseInt(baseInfo[1]);
                Item item = ItemService.gI().createNewItem((short) itemId, quantity);
                if (subItemInfo.length == 2) {
                    String[] options = subItemInfo[1].split(",");
                    for (String opt : options) {
                        if (opt == null || opt.isEmpty()) {
                            continue;
                        }
                        String[] optInfo = opt.split(":");
                        int tempIdOption = Integer.parseInt(optInfo[0]);
                        int param = Integer.parseInt(optInfo[1]);
                        item.itemOptions.add(new ItemOption(tempIdOption, param));
                    }
                }
                this.itemsReward.add(item);
            }
        } catch (Exception e) {
            Log.error(this.getClass(), e);
        }
    }

    public int getNumOfMessages() {
        return this.sender.getNumMessage();
    }

    public Session(Socket socket, Controller controller, String ip) {
        try {
            socket.setTcpNoDelay(true);
            this.id = baseId++;
            this.socket = socket;
            this.controller = controller;
            this.sendThread = new Thread((this.sender = new MessageSender(this, socket)), "Send " + ip);
            this.receiveThread = new Thread((this.collector = new MessageCollector(this, socket)), "Receive " + ip);
//            this.doControllerThread = new Thread((this.doController = new MessageDoController(this)), "Do controller " + ip);
//            this.doControllerThread.start();
            this.receiveThread.start();
            Client.gI().put(this);
        } catch (Exception e) {
            Log.error(Session.class, e);
        }
    }

    public void sendMessage(Message msg) {
        if (this.sender != null) {
            sender.addMessage(msg);
        }
    }

    public void doSendMessage(Message msg) {
        if (this.sender != null) {
            sender.doSendMessage(msg);
        }
    }

    public void disconnect() {
        if (connected) {
            connected = false;
            curR = 0;
            curW = 0;
            this.player = null;
            try {
                if (this.sender != null) {
                    this.sender.close();
                }
                if (this.collector != null) {
                    this.collector.close();
                }
                if (socket != null) {
                    socket.close();
                }
                this.socket = null;
                this.sender = null;
                this.collector = null;
                this.sendThread = null;
                this.receiveThread = null;
                this.uu = null;
                this.pp = null;
                this.itemsReward = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setClientType(Message msg) {
        try {
            if (!isSetClientType) {
                this.typeClient = (msg.reader().readByte());//client_type
                this.zoomLevel = msg.reader().readByte();//zoom_level
                msg.reader().readBoolean();//is_gprs
                msg.reader().readInt();//width
                msg.reader().readInt();//height
                msg.reader().readBoolean();//is_qwerty
                msg.reader().readBoolean();//is_touch
                String platform = msg.reader().readUTF();
                String[] arrPlatform = platform.split("\\|");
                this.version = Integer.parseInt(arrPlatform[1].replaceAll("\\.", ""));
                isSetClientType = true;
                Resources.getInstance().sendResVersion(this);
            }
        } catch (Exception e) {
        } finally {
            msg.cleanup();
        }
        DataGame.sendLinkIP(this);
    }

    public boolean isVersionAbove(int version) {
        return this.version >= version;
    }

    public String getName() {
        if (this.player != null) {
            return this.player.name;
        } else {
            return String.valueOf(this.socket.getPort());
        }
    }

    public void sendSessionKey() {
        this.sender.sendSessionKey();
    }

    public boolean canConnectWithIp() {
        Object o = ServerManager.CLIENTS.get(ipAddress);
        if (o == null) {
            ServerManager.CLIENTS.put(ipAddress, 1);
            return true;
        } else {
            int n = Integer.parseInt(String.valueOf(o));
            if (n < Manager.MAX_PER_IP) {
                n++;
                ServerManager.CLIENTS.put(ipAddress, n);
                return true;
            } else {
                return false;
            }
        }
    }

    public void login(String username, String password) {
//        if (true) {
//            Service.getInstance().sendThongBaoOK(this, "Máy chủ đang tiến hành bảo trì, vui lòng thử lại sau!");
//            return;
//        }
        if (Maintenance.isRuning || !ServerManager.gI().getLogin().isConnected()) {
            Service.getInstance().sendThongBaoOK(this, "Máy chủ đang tiến hành bảo trì, vui lòng thử lại sau!");
            return;
        }
        if (!isSetClientType || logging || loginSuccess) {
            return;
        }
        logging = true;
        AntiLogin al = ANTI_LOGIN_MAP.get(this.ipAddress);
        if (al == null) {
            al = new AntiLogin();
            ANTI_LOGIN_MAP.put(this.ipAddress, al);
        }
        if (!al.canLogin()) {
            Service.getInstance().sendThongBaoOK(this, al.getNotifyCannotLogin());
            return;
        }
        if (!this.isAdmin && Client.gI().getPlayers().size() >= Manager.MAX_PLAYER) {
            Service.getInstance().sendThongBaoOK(this, "Máy chủ hiện đang quá tải, "
                    + "cư dân vui lòng di chuyển sang máy chủ khác.");
            return;
        }
        if (this.player != null) {
        } else {
            Player player = null;
            try {
                this.uu = username;
                this.pp = password;
                ServerManager.gI().getLogin().getService().login(Manager.SERVER, this.id, username, password);
            } catch (Exception e) {
                e.printStackTrace();
                if (player != null) {
                    player.dispose();
                }
            }
        }
    }

    public void finishUpdate() {
        if (loginSuccess && !joinedGame) {
            player = GodGK.loadPlayer(this);
            if (!dataLoadFailed) {
                if (player != null) {
                    enter();
                } else {
                    Service.getInstance().switchToCreateChar(this);
                }
            } else {
                Service.getInstance().sendThongBaoOK(this, "Lỗi tải dữ liệu vui lòng báo với admin.");
            }
        }
    }

    public void enter() {
        if (!joinedGame) {
            joinedGame = true;
            player.nPoint.initPowerLimit();
            if (player.pet != null) {
                player.pet.nPoint.initPowerLimit();
            }
            player.nPoint.calPoint();
            player.nPoint.setHp(player.nPoint.hp);
            player.nPoint.setMp(player.nPoint.mp);
            player.zone.addPlayer(player);
            player.loaded = true;
            if (player.pet != null) {
                player.pet.nPoint.calPoint();
                player.pet.nPoint.setHp(player.pet.nPoint.hp);
                player.pet.nPoint.setMp(player.pet.nPoint.mp);
            }
            player.setSession(this);
            Client.gI().put(player);
            controller.sendInfo(this);
            Service.getInstance().player(player);
            Service.getInstance().Send_Caitrang(player);

            // -64 my flag bag
            Service.getInstance().sendFlagBag(player);
//            Service.getInstance().sendFlagBag(player.pet);

            // -113 skill shortcut
            player.playerSkill.sendSkillShortCut();
            // item time
            ItemTimeService.gI().sendAllItemTime(player);

            // send current task
            TaskService.gI().sendInfoCurrentTask(player);

            if (player.getSession().isAdmin) {
                Service.getInstance().sendThongBaoAllPlayer("|7|ADMIN VỪA VÔ GAME MẤY ĐỨA BUG CẨN THẬN!");
            }

            CheckTop(player);
            // nhận quà đăng nhập hàng ngày
            RewardService.gI().rewardFirstTimeLoginPerDay(player);
            SieuHangManager.GetRewardDay(player);
        }
    }

    public void CheckTop(Player player) {
        String topPlayer = TopCoin.getInstance().getNamePlayer();
        if (!Util.isNullOrEmpty(topPlayer) && Objects.equals(player.name, topPlayer)) {
                Service.getInstance().sendThongBaoAllPlayer("[THÔNG BÁO] Top 1 hiến máu tên " + player.name + " vừa đăng nhập vào game");
            }

    }
}
