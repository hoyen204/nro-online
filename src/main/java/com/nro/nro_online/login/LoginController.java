package com.nro.nro_online.login;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.nro.nro_online.data.DataGame;
import com.nro.nro_online.jdbc.DBService;
import com.nro.nro_online.resources.Resources;
import com.nro.nro_online.server.io.Message;
import com.nro.nro_online.server.io.Session;
import com.nro.nro_online.utils.Log;
import com.nro.nro_online.utils.Util;

public class LoginController {

    private final LoginSession session;

    public LoginController(LoginSession session) {
        this.session = session;
    }

    public void process(Message m) {
        try {
            switch (m.command) {
            case Cmd.LOGIN -> login(m);
            case Cmd.DISCONNECT -> disconnect(m);
            case Cmd.SERVER_MESSAGE -> serverMessage(m);
            case Cmd.UPDATE_TIME_LOGOUT -> updateTimeLogout(m);
            default -> Log.warning("cmd: " + m.command);
            }
        } catch (Exception e) {
            Log.error(LoginController.class, e);
        }
    }

    public void updateTimeLogout(Message ms) {
        try {
            int userID = ms.reader().readInt();
            Player player = Client.gI().getPlayerByUser(userID);
            if (player != null) {
                Client.gI().kickSession(player.getSession());
            } else {
                updateTimeLogout(userID);
            }
        } catch (IOException e) {
            Log.error(LoginController.class, e);
        }
    }

    public void updateTimeLogout(int id) {
        try (Connection con = DBService.gI().getConnectionForGame();
                PreparedStatement ps = con.prepareStatement("UPDATE account SET last_time_logout = ? WHERE id = ?")) {
            ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            Log.error(LoginController.class, e);
        }
    }

    public void serverMessage(Message ms) {
        try {
            int clientID = ms.reader().readInt();
            String text = ms.reader().readUTF();
            Session session = Client.gI().getSession(clientID);
            if (session != null) {
                Service.getInstance().sendThongBaoOK(session, text);
            }
        } catch (IOException e) {
            Log.error(LoginController.class, e);
        }
    }

    public void disconnect(Message ms) {
        try {
            int userID = ms.reader().readInt();
            Player player = Client.gI().getPlayerByUser(userID);
            if (player != null) {
                Client.gI().kickSession(player.getSession());
            }
        } catch (IOException e) {
            Log.error(LoginController.class, e);
        }
    }

    public void login(Message ms) {
        try {
            int clientID = ms.reader().readInt();
            Session session = Client.gI().getSession(clientID);
            if (session != null) {
                try {
                    byte status = ms.reader().readByte();
                    if (status == 0) {
                        int userID = ms.reader().readInt();
                        boolean isAdmin = ms.reader().readBoolean();
                        boolean actived = ms.reader().readBoolean();
                        int goldBar = ms.reader().readInt();
                        int vnd = ms.reader().readInt();
                        int poinNap = ms.reader().readInt();
                        long lastTimeLogin = ms.reader().readLong();
                        String rewards = ms.reader().readUTF();
                        int ruby = ms.reader().readInt();
                        int diemTichNap = ms.reader().readInt();
                        int server = ms.reader().readInt();
                        session.userId = userID;
                        Session existingSession = Client.gI().getSession(session);
                        if (existingSession != null) {
                            Client.gI().kickSession(existingSession);
                            Client.gI().kickSession(session);
                            Service.getInstance().sendThongBaoOK(session, "Máy chủ tắt hoặc mất sóng");
                            return;
                        }
                        session.isAdmin = isAdmin;
                        session.actived = actived;
                        session.goldBar = goldBar;
                        session.vnd = vnd;
                        session.poinCharging = poinNap;
                        session.lastTimeLogout = lastTimeLogin;
                        session.dataReward = rewards;
                        session.ruby = ruby;
                        session.diemTichNap = diemTichNap;
                        session.server = server;
                        Resources.getInstance().sendSmallVersion(session);
                        Resources.getInstance().sendBGVersion(session);
                        session.timeWait = 0;
                        session.loginSuccess = true;
                        DataGame.sendVersionGame(session);
                    } else {
                        String text = ms.reader().readUTF();
                        Service.getInstance().sendThongBaoOK(session, text);
                    }
                } finally {
                    session.setLogging(false);
                }
            }
        } catch (IOException e) {
            Log.error(LoginController.class, e);
        }
    }

    public void onConnectionFail() {
        Log.warning("Kết nối đến máy chủ login thất bại!");
        Util.setTimeout(session::reconnect, 10000);
    }

    public void onConnectOK() {
        Log.warning("Đã kết nối máy chủ login thành công!");
        session.getService().setServer(Manager.SERVER, Client.gI());
    }

    public void onDisconnected() {
        Log.warning("Mất kết nối máy chủ login");
        Util.setTimeout(session::reconnect, 10000);
    }
}