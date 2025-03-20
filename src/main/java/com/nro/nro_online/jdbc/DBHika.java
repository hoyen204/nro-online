package com.nro.nro_online.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import com.nro.nro_online.utils.Log;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DBHika {
    private DBHika() {}

    private static final HikariConfig config = new HikariConfig();
    private static final HikariDataSource ds;

    static {
        config.setDriverClassName(DBService.DRIVER);
        config.setJdbcUrl(DBService.URL
                .replace("#0", "mysql")
                .replace("#1", DBService.DB_HOST)
                .replace("#2", String.valueOf(DBService.DB_PORT))
                .replace("#3", DBService.DB_NAME));
        config.setUsername(DBService.DB_USER);
        config.setPassword(DBService.DB_PASSWORD);

        // Cấu hình tối ưu
        config.setMinimumIdle(20);
        config.setMaximumPoolSize(100);
        config.setMaxLifetime(180000); // 3 phút
        config.setIdleTimeout(60000);  // 1 phút
        config.setConnectionTimeout(30000); // 30s

        // Tối ưu performance
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");

        // UTF-8 support
        config.addDataSourceProperty("characterEncoding", "utf8");
        config.addDataSourceProperty("useUnicode", "true");

        ds = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        Connection conn = ds.getConnection();
        return conn;
    }

    public static void close() {
        if (ds != null && !ds.isClosed()) {
            ds.close();
            Log.success("Đóng HikariCP pool sạch sẽ, bye bye! 👋");
        }
    }
}