package com.nro.nro_online.jdbc.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import org.springframework.util.StringUtils;

import com.nro.nro_online.jdbc.DBService;
import com.nro.nro_online.server.io.Session;
import com.nro.nro_online.utils.Log;

public class AccountDAO {

    private AccountDAO() {}

    public static void changePassword(Session session) {
        if (!StringUtils.hasText(session.uu))
            return;

        try (Connection con = DBService.gI().getConnectionForGetPlayer();
                PreparedStatement ps = con.prepareStatement("update account set password = ? where id = ?")) {
            ps.setString(1, session.pp);
            ps.setInt(2, session.userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            Log.error(AccountDAO.class, e);
        }
    }

    public static void updateLastTimeLoginAllAccount(Session session) {
        if (!StringUtils.hasText(session.uu))
            return;

        try (Connection con = DBService.gI().getConnectionForGame();
                PreparedStatement ps = con.prepareStatement("update account set last_time_login = ? where id = ?")) {
            ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            ps.setInt(2, session.userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            Log.error(AccountDAO.class, e);
        }
    }

    public static void updateAccountLogout(Session session) {
        if (!StringUtils.hasText(session.uu))
            return;

        try (Connection con = DBService.gI().getConnectionForGame(); PreparedStatement ps = con.prepareStatement("update account set last_time_logout = ? where id = ?")){
            ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            ps.setInt(2, session.userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            Log.error(AccountDAO.class, e);
        }
    }

    public static void banAccount(Session session) {
        if (!StringUtils.hasText(session.uu))
            return;

        try (Connection con = DBService.gI().getConnectionForGetPlayer(); PreparedStatement ps = con.prepareStatement("update account set is_ban = 1 where id = ?")) {
            ps.setInt(1, session.userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            Log.error(AccountDAO.class, e);
        }
    }

    public static int createAccount(String user, String password) {
        try (Connection con = DBService.gI().getConnectionForGetPlayer()) {
            try (PreparedStatement psCheck = con.prepareStatement("SELECT 1 FROM account WHERE username = ?")) {
                psCheck.setString(1, user);
                if (psCheck.executeQuery().next()) {
                    Log.warning("Tạo thất bại do tài khoản đã tồn tại");
                    return -1;
                }
            }
            try (PreparedStatement psInsert = con.prepareStatement(
                    "INSERT INTO account(username, password) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                psInsert.setString(1, user);
                psInsert.setString(2, password);
                psInsert.executeUpdate();
                try (ResultSet rs = psInsert.getGeneratedKeys()) {
                    rs.next();
                    Log.success("User %s đã tạo tài khoản thành công!".formatted(user));
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            Log.error(AccountDAO.class, e);
            return -1;
        }
    }
}
