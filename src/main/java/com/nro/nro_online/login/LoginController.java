package com.nro.nro_online.login;

import com.nro.nro_online.data.DataGame;
import com.nro.nro_online.jdbc.DBService;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.resources.Resources;
import com.nro.nro_online.server.Client;
import com.nro.nro_online.server.Manager;
import com.nro.nro_online.server.io.Message;
import com.nro.nro_online.server.io.Session;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.utils.Log;
import com.nro.nro_online.utils.Util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

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
            default -> Log.warning("Command lạ lắm nha: " + m.command + " 🤔");
            }
        } catch (Exception e) {
            Log.error(LoginController.class, e, "Xử lý message lỗi, khổ ghê! 😭");
        }
    }

    private void updateTimeLogout(Message ms) throws IOException {
        int userID = ms.reader().readInt();
        Player player = Client.gI().getPlayerByUser(userID);
        if (player != null) {
            Client.gI().kickSession(player.getSession());
            Log.log("Đá player " + userID + " ra khỏi game! 👋");
        } else {
            updateTimeLogout(userID);
        }
    }

    private void updateTimeLogout(int id) {
        try (Connection con = DBService.gI().getConnectionForGame();
                PreparedStatement ps = con.prepareStatement("UPDATE account SET last_time_logout = ? WHERE id = ?")) {
            ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            ps.setInt(2, id);
            ps.executeUpdate();
            Log.log("Cập nhật logout cho user " + id + " xong xuôi! ⏰");
        } catch (SQLException e) {
            Log.error(LoginController.class, e, "Lỗi SQL khi update logout, chết tui! 😵");
        }
    }

    private void serverMessage(Message ms) throws IOException {
        int clientID = ms.reader().readInt();
        String text = ms.reader().readUTF();
        Session targetSession = Client.gI().getSession(clientID);
        if (targetSession != null) {
            Service.getInstance().sendThongBaoOK(targetSession, text);
            Log.log("Gửi tin nhắn cho client " + clientID + ": " + text + " 📩");
        } else {
            Log.warning("Không tìm thấy session cho client " + clientID + " đâu cả! 🤷");
        }
    }

    private void disconnect(Message ms) throws IOException {
        int userID = ms.reader().readInt();
        Player player = Client.gI().getPlayerByUser(userID);
        if (player != null) {
            Client.gI().kickSession(player.getSession());
            Log.log("Đá user " + userID + " ra khỏi server, bye bye! 👋");
        }
    }

    private void login(Message ms) throws IOException {
        int clientID = ms.reader().readInt();
        Session clientSession = Client.gI().getSession(clientID);
        if (clientSession == null) {
            Log.warning("Session " + clientID + " không tồn tại, ma quỷ à? 👻");
            return;
        }

        try {
            byte status = ms.reader().readByte();
            if (status == 0) {
                handleSuccessfulLogin(ms, clientSession);
            } else {
                String errorMsg = ms.reader().readUTF();
                Service.getInstance().sendThongBaoOK(clientSession, errorMsg);
                Log.log("Login fail cho session " + clientID + ": " + errorMsg + " 😢");
            }
        } finally {
            clientSession.setLogging(false);
        }
    }

    private void handleSuccessfulLogin(Message ms, Session session) throws IOException {
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

        Session existingSession = Client.gI().getSession(session);
        if (existingSession != null) {
            Client.gI().kickSession(existingSession);
            Client.gI().kickSession(session);
            Service.getInstance().sendThongBaoOK(session, "Có ai đó đá mày ra rồi, server lag hay sao ấy! 😵");
            Log.warning("Đá session trùng cho user " + userID + " ra khỏi game! ⚡");
            return;
        }

        session.userId = userID;
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

        Log.log("User " + userID + " login thành công, welcome bro! 😎");
    }

    public void onConnectionFail() {
        Log.warning("Kết nối login server fail, mạng lag hả trời! 🌩️");
        Util.setTimeout(session::reconnect, 10000);
    }

    public void onConnectOK() {
        Log.warning("Kết nối login server ngon lành, sướng ghê! 🌟");
        session.getService().setServer(Manager.SERVER, Client.gI());
    }

    public void onDisconnected() {
        Log.warning("Mất kết nối login server, buồn ghê! 😞");
        Util.setTimeout(session::reconnect, 10000);
    }
}