package sticker_tracker.data.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import sticker_tracker.domain.Section;
import sticker_tracker.domain.SectionType;
import sticker_tracker.infra.db.DatabaseConnection;

public final class SectionRepository {

    private final DatabaseConnection db;

    public SectionRepository(DatabaseConnection db) {
        this.db = db;
    }

    public List<Section> findAll() {
        final var sql = """
            SELECT id, collection_id, prefix, name, type, flag_asset, display_order, created_at, updated_at
            FROM sections
            ORDER BY display_order
            """;

        try (final var statement = db.getConnection().prepareStatement(sql);
             final var result = statement.executeQuery()) {
            final var sections = new ArrayList<Section>();

            while (result.next()) {
                sections.add(mapSection(result));
            }

            return sections;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao buscar seções: " + e.getMessage(), e);
        }
    }

    public List<Section> findByCollection(String collectionId) {
        final var sql = """
            SELECT id, collection_id, prefix, name, type, flag_asset, display_order, created_at, updated_at
            FROM sections
            WHERE collection_id = ?
            ORDER BY display_order
            """;

        try (final var statement = db.getConnection().prepareStatement(sql)) {
            statement.setString(1, collectionId);

            try (final var result = statement.executeQuery()) {
                final var sections = new ArrayList<Section>();

                while (result.next()) {
                    sections.add(mapSection(result));
                }

                return sections;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao buscar seções por coleção: " + e.getMessage(), e);
        }
    }

    public Optional<Section> findByPrefix(String prefix, String collectionId) {
        final var sql = """
            SELECT id, collection_id, prefix, name, type, flag_asset, display_order, created_at, updated_at
            FROM sections
            WHERE prefix = ?
              AND collection_id = ?
            ORDER BY display_order
            LIMIT 1
            """;

        try (final var statement = db.getConnection().prepareStatement(sql)) {
            statement.setString(1, prefix);
            statement.setString(2, collectionId);

            try (final var result = statement.executeQuery()) {
                if (result.next()) {
                    return Optional.of(mapSection(result));
                }

                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao buscar seção por prefixo: " + e.getMessage(), e);
        }
    }

    public void createSection(Section section) {
        final var sql = """
            INSERT INTO sections (id, collection_id, prefix, name, type, flag_asset, display_order)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
        final var id = section.getId() == null || section.getId().isBlank()
            ? UUID.randomUUID().toString()
            : section.getId();

        try (final var statement = db.getConnection().prepareStatement(sql)) {
            statement.setString(1, id);
            statement.setString(2, section.getCollectionId());
            statement.setString(3, section.getPrefix());
            statement.setString(4, section.getName());
            statement.setString(5, section.getType().name().toLowerCase());
            statement.setString(6, section.getFlagAsset());
            statement.setInt(7, section.getDisplayOrder());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao criar seção: " + e.getMessage(), e);
        }
    }

    private Section mapSection(ResultSet result) throws SQLException {
        return new Section(
            result.getString("id"),
            result.getString("collection_id"),
            result.getString("prefix"),
            result.getString("name"),
            SectionType.fromString(result.getString("type")),
            result.getString("flag_asset"),
            result.getInt("display_order"),
            findDateTime(result, "created_at"),
            findDateTime(result, "updated_at")
        );
    }

    private java.time.LocalDateTime findDateTime(ResultSet result, String column) throws SQLException {
        final Timestamp timestamp = result.getTimestamp(column);

        return timestamp.toLocalDateTime();
    }
}
