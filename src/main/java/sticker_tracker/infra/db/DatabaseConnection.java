package sticker_tracker.infra.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection {

    private static final String HOST = findEnvironmentValue("DB_HOST", "localhost");
    private static final String PORT = findEnvironmentValue("DB_PORT", "3306");
    private static final String DATABASE = findEnvironmentValue("DB_DATABASE", "sticker_tracker");
    private static final String USER = findEnvironmentValue("DB_USERNAME", "root");
    private static final String PASSWORD = findEnvironmentValue("DB_PASSWORD", "root");
    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
        + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
        + "&useUnicode=true&characterEncoding=utf8&connectionCollation=utf8mb4_unicode_ci";

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
        } catch (SQLException exception) {
            connect();
        }

        return connection;
    }

    private void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception exception) {
            throw new RuntimeException("Falha ao conectar ao banco: " + exception.getMessage());
        }
    }

    private static String findEnvironmentValue(String name, String fallback) {
        final var environmentValue = System.getenv(name);

        if (environmentValue == null || environmentValue.isBlank()) {
            return fallback;
        }

        return environmentValue;
    }
}
