package com.nro.nro_online.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Vector;

import com.nro.nro_online.utils.Log;

public class ConnPool {

    private static volatile ConnPool instance; // Thread-safe singleton
    private final String url;
    private final String user;
    private final String password;
    private final int maxConn;
    private static final int TIMEOUT = 30000; // 30s timeout
    private final Vector<Connection> conns = new Vector<>();
    private final Vector<Long> getTime = new Vector<>();

    public static ConnPool gI() {
        if (instance == null) {
            synchronized (ConnPool.class) {
                if (instance == null) {
                    instance = new ConnPool("jdbc:mysql://" + DBService.DB_HOST + ":" + DBService.DB_PORT + "/" + DBService.DB_NAME
                            + "?autoReconnect=true", DBService.DB_USER, DBService.DB_PASSWORD, DBService.MAX_CONN);
                    Log.success("Khởi tạo ConnPool ngon lành! 🚀");
                }
            }
        }
        return instance;
    }

    private ConnPool(String url, String user, String password, int max) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.maxConn = max;
        try {
            Class.forName(DBService.DRIVER);
        } catch (ClassNotFoundException e) {
            Log.error(ConnPool.class, e, "Không tìm thấy driver, xui ghê! 😭");
        }
    }

    private Connection newConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public synchronized Connection getConnection() throws SQLException {
        // Cleanup expired connections
        for (int i = 0; i < conns.size(); i++) {
            long time = getTime.get(i);
            if (time != 0 && System.currentTimeMillis() - time > TIMEOUT) {
                close(conns.get(i));
                conns.set(i, newConnection());
                getTime.set(i, 0L);
            }
            if (getTime.get(i) == 0) {
                Connection conn = conns.get(i);
                if (conn.isClosed()) {
                    conns.set(i, newConnection());
                }
                getTime.set(i, System.currentTimeMillis());
                return conns.get(i);
            }
        }

        // Tạo mới nếu chưa đầy
        if (conns.size() >= maxConn) {
            Log.warning("Hết slot connection rồi, đợi tí nha! ⏳");
            throw new SQLException("Đã đạt giới hạn " + maxConn + " connections!");
        }

        Connection conn = newConnection();
        conns.add(conn);
        getTime.add(System.currentTimeMillis());
        Log.log("Tạo connection mới, số lượng: " + conns.size() + " 🆙");
        return conn;
    }

    public void close(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                Log.log("Đóng connection xong, sạch sẽ! 🧹");
            }
        } catch (SQLException e) {
            Log.error(ConnPool.class, e, "Đóng connection lỗi, huhu 😢");
        }
    }

    public void free(Connection conn) {
        int index = conns.indexOf(conn);
        if (index > -1) {
            getTime.set(index, 0L);
            Log.log("Trả connection về pool, chill thôi! 😌");
        }
    }
}