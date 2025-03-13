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

    private AccountDAO() {
    }

    public static void changePassword(Session session) {
        updateAccountField(session, "password", session.pp);
    }

    public static void updateLastTimeLoginAllAccount(Session session) {
        updateAccountTimestampField(session, "last_time_login");
    }

    public static void updateAccountLogout(Session session) {
        updateAccountTimestampField(session, "last_time_logout");
    }

    public static void banAccount(Session session) {
        updateAccountField(session, "is_ban", 1);
    }

    public static int createAccount(String user, String password) {
        try (Connection con = DBService.gI().getConnectionForGetPlayer()) {
            if (isAccountExists(con, user)) {
                Log.warning("Tạo thất bại do tài khoản đã tồn tại");
                return -1;
            }
            return insertAccount(con, user, password);
        } catch (SQLException e) {
            Log.error(AccountDAO.class, e);
            return -1;
        }
    }

    private static boolean isAccountExists(Connection con, String user) throws SQLException {
        try (PreparedStatement psCheck = con.prepareStatement("SELECT 1 FROM account WHERE username = ?")) {
            psCheck.setString(1, user);
            return psCheck.executeQuery().next();
        }
    }

    private static int insertAccount(Connection con, String user, String password) throws SQLException {
        try (PreparedStatement psInsert = con.prepareStatement(
                "INSERT INTO account(username, password) VALUES (?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            psInsert.setString(1, user);
            psInsert.setString(2, password);
            psInsert.executeUpdate();
            try (ResultSet rs = psInsert.getGeneratedKeys()) {
                if (rs.next()) {
                    Log.success("User %s đã tạo tài khoản thành công!".formatted(user));
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    private static void updateAccountField(Session session, String field, Object value) {
        if (!StringUtils.hasText(session.uu))
            return;

        String sql = "UPDATE account SET " + field + " = ? WHERE id = ?";
        executeUpdate(session, sql, value, session.userId);
    }

    private static void updateAccountTimestampField(Session session, String field) {
        if (!StringUtils.hasText(session.uu))
            return;

        String sql = "UPDATE account SET " + field + " = ? WHERE id = ?";
        executeUpdate(session, sql, new Timestamp(System.currentTimeMillis()), session.userId);
    }

    private static void executeUpdate(Session session, String sql, Object value, int userId) {
        try (Connection con = DBService.gI().getConnectionForGame();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setObject(1, value);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            Log.error(AccountDAO.class, e);
        }
    }
}
