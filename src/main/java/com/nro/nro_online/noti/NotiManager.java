package com.nro.nro_online.noti;

import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.nro.nro_online.consts.Cmd;
import com.nro.nro_online.jdbc.DBService;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.server.io.Message;
import com.nro.nro_online.services.Service;

public class NotiManager {
    private static final NotiManager INSTANCE = new NotiManager();
    private final List<Notification> notifications = new CopyOnWriteArrayList<>();
    private volatile Alert alert;

    private NotiManager() {
    }

    public static NotiManager getInstance() {
        return INSTANCE;
    }

    public void load() {
        loadNoti();
        loadAlert();
    }

    private void loadNoti() {
        try (PreparedStatement ps = DBService.gI().getConnectionForGame()
                .prepareStatement("SELECT id, content, title FROM `notifications`");
                ResultSet rs = ps.executeQuery()) {
            notifications.clear();
            while (rs.next()) {
                Notification notification = new Notification();
                notification.setId(rs.getInt("id"));
                notification.setContent(rs.getString("content"));
                notification.setTitle(rs.getString("title"));
                notifications.add(notification);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void loadAlert() {
        try (PreparedStatement ps = DBService.gI().getConnectionForGame()
                .prepareStatement("SELECT content FROM `alert`");
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                Alert a = new Alert();
                a.content = rs.getString("content");
                this.alert = a;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void sendAlert(Player player) {
        if (alert != null && player != null) {
            Service.getInstance().sendThongBaoFromAdmin(player, alert.content);
        }
    }

    public void sendNoti(Player player) {
        if (player == null)
            return;

        try (Message m = new Message(Cmd.GAME_INFO)) {
            DataOutputStream ds = m.writer();
            ds.writeByte(notifications.size());
            for (Notification notification : notifications) {
                ds.writeShort(notification.getId());
                ds.writeUTF(notification.getTitle());
                ds.writeUTF(notification.getContent());
            }
            ds.flush();
            player.sendMessage(m);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}