package sticker_tracker.infra.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection {

    private static final String HOST = findEnv("DB_HOST", "localhost");
    private static final String PORT = findEnv("DB_PORT", "3306");
    private static final String DATABASE = findEnv("DB_DATABASE", "sticker_tracker");
    private static final String USER = findEnv("DB_USERNAME", "root");
    private static final String PASSWORD = findEnv("DB_PASSWORD", "root");
    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
        + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    private static DatabaseConnection instance;

    private Connection connection;

    private DatabaseConnection() {
        connect();
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }

        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            connect();
        }

        return connection;
    }

    private void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao conectar ao banco: " + e.getMessage());
        }
    }

    private static String findEnv(String name, String fallback) {
        final var value = System.getenv(name);

        if (value == null || value.isBlank()) {
            return fallback;
        }

        return value;
    }
}
