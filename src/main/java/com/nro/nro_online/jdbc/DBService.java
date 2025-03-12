package com.nro.nro_online.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import com.nro.nro_online.utils.Log;

public class DBService {
    public static String DRIVER = "com.mysql.cj.jdbc.Driver";
    public static String URL = "jdbc:#0://#1:#2/#3";
    public static String DB_HOST = "localhost";
    public static int DB_PORT = 3306;
    public static String DB_NAME = "tuanpeo";
    public static String DB_USER = "root";
    public static String DB_PASSWORD = "";
    public static int MAX_CONN = 2;

    private static final Connection[] connections = new Connection[10];
    private static DBService instance;

    public static DBService gI() {
        if (instance == null) {
            instance = new DBService();
            Log.success("Khởi tạo DBService xong, sẵn sàng chiến! 🚀");
        }
        return instance;
    }

    private DBService() {
        ConnPool.gI();
    }

    private static Connection getOrCreateConnection(int index) throws SQLException {
        Connection conn = connections[index];
        if (conn != null && !conn.isClosed() && !conn.isValid(5)) { // Giảm timeout xuống 5s cho nhanh
                closeConnection(conn);
                conn = null;
            }

        if (conn == null || conn.isClosed()) {
            conn = DBHika.getConnection();
            connections[index] = conn;
            Log.log("Tạo connection mới cho slot " + index + " 🎉");
        }
        return conn;
    }

    private static void closeConnection(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                Log.log("Đóng connection xong, gọn gàng! 🧹");
            }
        } catch (SQLException e) {
            Log.error(DBService.class, e, "Đóng connection lỗi, xui ghê! 😭");
        }
    }

    public Connection getConnectionForLogin() throws SQLException {
        return getOrCreateConnection(0);
    }

    public Connection getConnectionForLogout() throws SQLException {
        return getOrCreateConnection(1);
    }

    public Connection getConnectionForSaveData() throws SQLException {
        return getOrCreateConnection(2);
    }

    public Connection getConnectionForGame() throws SQLException {
        return getOrCreateConnection(3);
    }

    public Connection getConnectionForClan() throws SQLException {
        return getOrCreateConnection(4);
    }

    public Connection getConnectionForAutoSave() throws SQLException {
        return getOrCreateConnection(5);
    }

    public Connection getConnectionForSaveHistory() throws SQLException {
        return getOrCreateConnection(6);
    }

    public Connection getConnectionForGetPlayer() throws SQLException {
        return getOrCreateConnection(7);
    }

    public Connection getConnectionCreatPlayer() throws SQLException {
        return getOrCreateConnection(8);
    }

    public Connection getConnectionLoadCoint() throws SQLException {
        return getOrCreateConnection(9); // Fix lỗi trả nhầm slot 8
    }

    public Connection getConnection() throws SQLException {
        return DBHika.getConnection();
    }

    public void release(Connection con) {
        for (int i = 0; i < connections.length; i++) {
            if (connections[i] == con) {
                closeConnection(con);
                connections[i] = null;
                Log.log("Trả connection slot " + i + " về tự do! 😌");
                break;
            }
        }
    }

    public int currentActive() {
        int count = 0;
        for (Connection conn : connections) {
            try {
                if (conn != null && !conn.isClosed()) count++;
            } catch (SQLException e) {
                Log.error(DBService.class, e, "Check active lỗi, huhu 😢");
            }
        }
        return count;
    }

    public int currentIdle() {
        return MAX_CONN - currentActive();
    }
}