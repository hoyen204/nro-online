package com.nro.nro_online.server;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.nro.nro_online.attr.AttributeManager;
import com.nro.nro_online.jdbc.DBService;
import com.nro.nro_online.jdbc.daos.HistoryTransactionDAO;
import com.nro.nro_online.jdbc.daos.PlayerDAO;
import com.nro.nro_online.login.LoginSession;
import com.nro.nro_online.manager.ConsignManager;
import com.nro.nro_online.manager.SieuHangControl;
import com.nro.nro_online.manager.SieuHangManager;
import com.nro.nro_online.manager.TopCoin;
import com.nro.nro_online.manager.TopManager;
import com.nro.nro_online.manager.TopWhis;
import com.nro.nro_online.manager.TranhNgocManager;
import com.nro.nro_online.models.boss.BossFactory;
import com.nro.nro_online.models.boss.BossManager;
import com.nro.nro_online.models.map.challenge.MartialCongressManager;
import com.nro.nro_online.models.map.dungeon.DungeonManager;
import com.nro.nro_online.models.map.phoban.BanDoKhoBau;
import com.nro.nro_online.models.map.phoban.DoanhTrai;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.server.io.Session;
import com.nro.nro_online.services.ClanService;
import com.nro.nro_online.utils.Log;
import com.nro.nro_online.utils.TimeUtil;
import com.nro.nro_online.utils.Util;
import lombok.Getter;
import lombok.Setter;

public class ServerManager {

    public final String timeStart;

    public static final Map<String, Integer> clients = new HashMap<>();

    public static String name = "";
    public static int port = 14445;

    private Controller controller;

    private static ServerManager instance;

    public static ServerSocket listenSocket;
    public static boolean isRunning;

    @Getter
    private TranhNgocManager tranhNgocManager;
    private SieuHangControl sieuHangControl;

    @Getter
    private LoginSession login;
    public static boolean updateTimeLogin;
    @Getter
    @Setter
    private AttributeManager attributeManager;
    private long lastUpdateAttribute;
    @Getter
    private DungeonManager dungeonManager;

    public SieuHangControl getSieuHangController() {
        return this.sieuHangControl;
    }

    private ServerManager() {
        timeStart = TimeUtil.getTimeNow("dd/MM/yyyy HH:mm:ss");
    }

    public void init() {
        Manager.gI();
        HistoryTransactionDAO.deleteHistory();
        BossFactory.initBoss();
        this.controller = new Controller();
    }

    public static ServerManager gI() {
        if (instance == null) {
            instance = new ServerManager();
            instance.init();
        }
        return instance;
    }

    public static void main(String[] args) {

        ServerManager.gI().run();
    }

    public void run() {
        isRunning = true;
        activeCommandLine();
        activeGame();
        new Thread(TopCoin.getInstance(), "update Top Coin").start();
        activeLogin();
        autoTask();
        activeServerSocket();
    }

    public void activeLogin() {
        login = new LoginSession();
        login.connect(Manager.loginHost, Manager.loginPort);
    }

    private void activeServerSocket() {
        try {
            Log.log("Start server......... Current thread: " + Thread.activeCount());
            listenSocket = new ServerSocket(port);
            while (isRunning) {
                try {
                    Socket sc = listenSocket.accept();
                    String ip = (((InetSocketAddress) sc.getRemoteSocketAddress()).getAddress()).toString().replace("/", "");
                    if (canConnectWithIp(ip)) {
                        Session session = new Session(sc, controller, ip);
                        session.ipAddress = ip;
                    } else {
                        sc.close();
                    }
                } catch (Exception e) {
                }
            }
            listenSocket.close();
        } catch (Exception e) {
            Log.error(ServerManager.class, e, "Lỗi mở port");
            System.exit(0);
        }
    }

    private boolean canConnectWithIp(String ipAddress) {
        Object o = clients.get(ipAddress);
        if (o == null) {
            clients.put(ipAddress, 1);
            return true;
        } else {
            int n = Integer.parseInt(String.valueOf(o));
            if (n < Manager.MAX_PER_IP) {
                n++;
                clients.put(ipAddress, n);
                return true;
            } else {
                return false;
            }
        }
    }

    public void disconnect(Session session) {
        Object o = clients.get(session.ipAddress);
        if (o != null) {
            int n = Integer.parseInt(String.valueOf(o));
            n--;
            if (n < 0) {
                n = 0;
            }
            clients.put(session.ipAddress, n);
        }
    }

    private void activeCommandLine() {
        new Thread(() -> {
            Scanner sc = new Scanner(System.in);
            while (true) {
                String line = sc.nextLine();
                if (line.equals("baotri")) {
                    new Thread(() -> {
                        Maintenance.gI().start(5);
                    }).start();
                } else if (line.equals("athread")) {
                    ServerNotify.gI().notify("Debug server: " + Thread.activeCount());
                } else if (line.equals("nplayer")) {
                    Log.error("Player in game: " + Client.gI().getPlayers().size());
                } else if (line.equals("admin")) {
                    new Thread(() -> {
                        Client.gI().close();
                    }).start();
                }
            }
        }, "Active line").start();
    }

    private void activeGame() {
        long delay = 500;
        long delaySecond = 5000;
        new Thread(() -> {
            while (isRunning) {
                long l1 = System.currentTimeMillis();
                BossManager.gI().updateAllBoss();
                long l2 = System.currentTimeMillis() - l1;
                if (l2 < delay) {
                    try {
                        Thread.sleep(delay - l2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, "update boss").start();
        new Thread(() -> {
            while (isRunning) {
                long start = System.currentTimeMillis();
                for (DoanhTrai dt : DoanhTrai.DOANH_TRAIS) {
                    dt.update();
                }
                for (BanDoKhoBau bdkb : BanDoKhoBau.BAN_DO_KHO_BAUS) {
                    bdkb.update();
                }
                long timeUpdate = System.currentTimeMillis() - start;
                if (timeUpdate < delay) {
                    try {
                        Thread.sleep(delay - timeUpdate);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }, "update pho ban").start();
        new Thread(() -> {
            while (isRunning) {
                try {
                    long start = System.currentTimeMillis();
                    if (attributeManager != null) {
                        attributeManager.update();
                        if (Util.canDoWithTime(lastUpdateAttribute, 600000)) {
                            Manager.gI().updateAttributeServer();
                        }
                    }
                    long timeUpdate = System.currentTimeMillis() - start;
                    if (timeUpdate < delay) {
                        Thread.sleep(delay - timeUpdate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "update Attribute Server").start();
        dungeonManager = new DungeonManager();
        dungeonManager.start();
        new Thread(dungeonManager, "Phó bản").start();
        new Thread(() -> {
            while (isRunning) {
                try {
                    long start = System.currentTimeMillis();
                    MartialCongressManager.gI().update();
                    long timeUpdate = System.currentTimeMillis() - start;
                    if (timeUpdate < delay) {
                        Thread.sleep(delay - timeUpdate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "update dai hoi vo thuat").start();
        new Thread(() -> {
            while (isRunning) {
                try {
                    long start = System.currentTimeMillis();
                    TopWhis.update();
                    long timeUpdate = System.currentTimeMillis() - start;
                    if (timeUpdate < delay) {
                        Thread.sleep(delay - timeUpdate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "update top whis").start();
        this.tranhNgocManager = new TranhNgocManager();
        new Thread(this.tranhNgocManager, "Tranh ngoc").start();

        this.sieuHangControl = new SieuHangControl();
        new Thread(this.sieuHangControl, "Sieu hang").start();
        new Thread(() -> {
            while (isRunning) {
                try {
                    long start = System.currentTimeMillis();
                    SieuHangManager.Update();
                    long timeUpdate = System.currentTimeMillis() - start;
                    if (timeUpdate < delay) {
                        Thread.sleep(delay - timeUpdate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "update giai sieu hang").start();

        new Thread(() -> {
            while (isRunning) {
                try {
                    long start = System.currentTimeMillis();
                    SieuHangManager.UpdatePedingFight();
                    long timeUpdate = System.currentTimeMillis() - start;
                    if (timeUpdate < delaySecond) {
                        Thread.sleep(delaySecond - timeUpdate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "update giai sieu hang pending").start();
    }

    public void close(long delay) {
        try {
            dungeonManager.shutdown();
        } catch (Exception e) {
            Log.error(ServerManager.class, e);
        }
        try {
            Manager.gI().updateEventCount();
        } catch (Exception e) {
            Log.error(ServerManager.class, e);
        }
        try {
            Manager.gI().updateAttributeServer();
        } catch (Exception e) {
            Log.error(ServerManager.class, e);
        }
        try {
            Client.gI().close();
        } catch (Exception e) {
            Log.error(ServerManager.class, e);
        }
        try {
            ClanService.gI().close();
        } catch (Exception e) {
            Log.error(ServerManager.class, e);
        }
        try {
            ConsignManager.getInstance().close();
        } catch (Exception e) {
            Log.error(ServerManager.class, e);
        }
        Client.gI().close();
        Log.success("BẢO TRÌ THÀNH CÔNG!...................................");
        System.exit(0);
    }

    public void saveAll(boolean updateTimeLogout) {
        try {
            List<Player> list = Client.gI().getPlayers();
            Connection conn = DBService.gI().getConnectionForAutoSave();
            for (Player player : list) {
                try {
                    PlayerDAO.updateTimeLogout = updateTimeLogout;
                    PlayerDAO.updatePlayer(player, conn);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void autoTask() {
        ScheduledExecutorService autoSave = Executors.newScheduledThreadPool(1);
        ScheduledExecutorService autoTopPower = Executors.newScheduledThreadPool(1);
        autoSave.scheduleWithFixedDelay(() -> saveAll(false), 300000, 300000, TimeUnit.MILLISECONDS);
        autoTopPower.scheduleWithFixedDelay(() -> TopManager.getInstance().load(), 0, 600000, TimeUnit.MILLISECONDS);
    }
}
