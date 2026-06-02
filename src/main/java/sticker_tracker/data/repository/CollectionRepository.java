package sticker_tracker.data.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import sticker_tracker.domain.Collection;
import sticker_tracker.infra.db.DatabaseConnection;

public final class CollectionRepository {

    private final DatabaseConnection db;

    public CollectionRepository(DatabaseConnection db) {
        this.db = db;
    }

    public List<Collection> findAll() {
        final var sql = """
            SELECT id, name, created_at, updated_at
            FROM collections
            ORDER BY created_at
            """;

        try (final var statement = db.getConnection().prepareStatement(sql);
             final var result = statement.executeQuery()) {
            final var collections = new ArrayList<Collection>();

            while (result.next()) {
                collections.add(mapCollection(result));
            }

            return collections;
        } catch (SQLException exception) {
            throw new RuntimeException("Falha ao buscar álbuns: " + exception.getMessage(), exception);
        }
    }

    public Optional<Collection> findById(String id) {
        final var sql = """
            SELECT id, name, created_at, updated_at
            FROM collections
            WHERE id = ?
            """;

        try (final var statement = db.getConnection().prepareStatement(sql)) {
            statement.setString(1, id);

            try (final var result = statement.executeQuery()) {
                if (result.next()) {
                    return Optional.of(mapCollection(result));
                }

                return Optional.empty();
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Falha ao buscar coleção por id: " + exception.getMessage(), exception);
        }
    }

    public void createCollection(Collection collection) {
        final var sql = """
            INSERT INTO collections (id, name)
            VALUES (?, ?)
            """;
        final var collectionId = collection.getId() == null || collection.getId().isBlank()
            ? UUID.randomUUID().toString()
            : collection.getId();

        try (final var statement = db.getConnection().prepareStatement(sql)) {
            statement.setString(1, collectionId);
            statement.setString(2, collection.getName());
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Falha ao criar coleção: " + exception.getMessage(), exception);
        }
    }

    private Collection mapCollection(ResultSet result) throws SQLException {
        return new Collection(
            result.getString("id"),
            result.getString("name"),
            findDateTime(result, "created_at"),
            findDateTime(result, "updated_at")
        );
    }

    private java.time.LocalDateTime findDateTime(ResultSet result, String column) throws SQLException {
        final Timestamp timestamp = result.getTimestamp(column);

        return timestamp.toLocalDateTime();
    }
}
