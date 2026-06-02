package sticker_tracker.data.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import sticker_tracker.domain.UserSticker;
import sticker_tracker.infra.db.DatabaseConnection;

public final class UserStickerRepository {

    private static final int MISSING_QUANTITY = 0;
    private static final int COLLECTED_QUANTITY = 1;
    private static final int QUANTITY_STEP = 1;

    private final DatabaseConnection db;

    public UserStickerRepository(DatabaseConnection db) {
        this.db = db;
    }

    public List<UserSticker> findAll() {
        final var sql = """
            SELECT id, sticker_id, quantity, created_at, updated_at
            FROM user_stickers
            ORDER BY created_at DESC
            """;

        try (final var statement = db.getConnection().prepareStatement(sql);
             final var result = statement.executeQuery()) {
            final var userStickers = new ArrayList<UserSticker>();

            while (result.next()) {
                userStickers.add(mapUserSticker(result));
            }

            return userStickers;
        } catch (SQLException exception) {
            throw new RuntimeException("Falha ao buscar figurinhas do usuário: " + exception.getMessage(), exception);
        }
    }

    public List<UserSticker> findRepeated() {
        final var sql = """
            SELECT id, sticker_id, quantity, created_at, updated_at
            FROM user_stickers
            WHERE quantity > ?
            ORDER BY quantity DESC, updated_at DESC
            """;

        try (final var statement = db.getConnection().prepareStatement(sql)) {
            statement.setInt(1, COLLECTED_QUANTITY);

            try (final var result = statement.executeQuery()) {
                final var userStickers = new ArrayList<UserSticker>();

                while (result.next()) {
                    userStickers.add(mapUserSticker(result));
                }

                return userStickers;
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Falha ao buscar figurinhas repetidas: " + exception.getMessage(), exception);
        }
    }

    public List<UserSticker> findRecent(int limit) {
        final var sql = """
            SELECT id, sticker_id, quantity, created_at, updated_at
            FROM user_stickers
            ORDER BY created_at DESC
            LIMIT ?
            """;

        try (final var statement = db.getConnection().prepareStatement(sql)) {
            statement.setInt(1, limit);

            try (final var result = statement.executeQuery()) {
                final var userStickers = new ArrayList<UserSticker>();

                while (result.next()) {
                    userStickers.add(mapUserSticker(result));
                }

                return userStickers;
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Falha ao buscar figurinhas recentes: " + exception.getMessage(), exception);
        }
    }

    public Optional<UserSticker> findByStickerId(String stickerId) {
        final var sql = """
            SELECT id, sticker_id, quantity, created_at, updated_at
            FROM user_stickers
            WHERE sticker_id = ?
            """;

        try (final var statement = db.getConnection().prepareStatement(sql)) {
            statement.setString(1, stickerId);

            try (final var result = statement.executeQuery()) {
                if (result.next()) {
                    return Optional.of(mapUserSticker(result));
                }

                return Optional.empty();
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Falha ao buscar figurinha do usuário: " + exception.getMessage(), exception);
        }
    }

    public void createUserSticker(String stickerId) {
        final var sql = """
            INSERT INTO user_stickers (id, sticker_id, quantity)
            VALUES (?, ?, ?)
            """;

        try (final var statement = db.getConnection().prepareStatement(sql)) {
            statement.setString(1, UUID.randomUUID().toString());
            statement.setString(2, stickerId);
            statement.setInt(3, COLLECTED_QUANTITY);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Falha ao criar figurinha do usuário: " + exception.getMessage(), exception);
        }
    }

    public void updateIncrementQuantity(String stickerId) {
        final var currentUserSticker = findByStickerId(stickerId);

        if (currentUserSticker.isEmpty()) {
            createUserSticker(stickerId);
            return;
        }

        final var sql = """
            UPDATE user_stickers
            SET quantity = quantity + ?
            WHERE sticker_id = ?
            """;

        try (final var statement = db.getConnection().prepareStatement(sql)) {
            statement.setInt(1, QUANTITY_STEP);
            statement.setString(2, stickerId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Falha ao incrementar quantidade: " + exception.getMessage(), exception);
        }
    }

    public void updateDecrementQuantity(String stickerId) {
        final var currentUserSticker = findByStickerId(stickerId);

        currentUserSticker.ifPresent(userSticker -> {
            if (userSticker.getQuantity() == COLLECTED_QUANTITY) {
                deleteUserSticker(userSticker.getId());
            } else {
                final var sql = """
                    UPDATE user_stickers
                    SET quantity = quantity - ?
                    WHERE sticker_id = ?
                    """;

                try (final var statement = db.getConnection().prepareStatement(sql)) {
                    statement.setInt(1, QUANTITY_STEP);
                    statement.setString(2, stickerId);
                    statement.executeUpdate();
                } catch (SQLException exception) {
                    throw new RuntimeException("Falha ao decrementar quantidade: " + exception.getMessage(), exception);
                }
            }
        });
    }

    public void updateQuantity(String stickerId, int quantity) {
        if (quantity <= MISSING_QUANTITY) {
            findByStickerId(stickerId).ifPresent(userSticker -> deleteUserSticker(userSticker.getId()));
            return;
        }

        final var currentUserSticker = findByStickerId(stickerId);

        if (currentUserSticker.isEmpty()) {
            createUserSticker(stickerId);
        }

        final var sql = """
            UPDATE user_stickers
            SET quantity = ?
            WHERE sticker_id = ?
            """;

        try (final var statement = db.getConnection().prepareStatement(sql)) {
            statement.setInt(1, quantity);
            statement.setString(2, stickerId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Falha ao atualizar quantidade: " + exception.getMessage(), exception);
        }
    }

    public void deleteUserSticker(String id) {
        final var sql = """
            DELETE FROM user_stickers
            WHERE id = ?
            """;

        try (final var statement = db.getConnection().prepareStatement(sql)) {
            statement.setString(1, id);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Falha ao deletar figurinha do usuário: " + exception.getMessage(), exception);
        }
    }

    private UserSticker mapUserSticker(ResultSet result) throws SQLException {
        return new UserSticker(
            result.getString("id"),
            result.getString("sticker_id"),
            result.getInt("quantity"),
            findDateTime(result, "created_at"),
            findDateTime(result, "updated_at")
        );
    }

    private java.time.LocalDateTime findDateTime(ResultSet result, String column) throws SQLException {
        final Timestamp timestamp = result.getTimestamp(column);

        return timestamp.toLocalDateTime();
    }
}
