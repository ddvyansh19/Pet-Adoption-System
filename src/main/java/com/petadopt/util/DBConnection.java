package com.petadopt.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Singleton factory for JDBC connections. Callers close the connection (try-with-resources). */
public final class DBConnection {
    private static final DBConnection INSTANCE = new DBConnection();
    private final AppConfig config = AppConfig.getInstance();

    private DBConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("MySQL JDBC driver not found on the classpath", e);
        }
    }

    public static DBConnection getInstance() {
        return INSTANCE;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(config.dbUrl(), config.dbUser(), config.dbPassword());
    }
}
