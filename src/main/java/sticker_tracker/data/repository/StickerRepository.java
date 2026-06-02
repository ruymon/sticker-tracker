package sticker_tracker.data.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import sticker_tracker.domain.Progress;
import sticker_tracker.domain.Sticker;
import sticker_tracker.infra.db.DatabaseConnection;

public final class StickerRepository {

    private final DatabaseConnection db;

    public StickerRepository(DatabaseConnection db) {
        this.db = db;
    }

    public List<Sticker> findAll() {
        final var sql = """
            SELECT id, collection_id, section_id, code, number, name, image_url, created_at, updated_at
            FROM stickers
            ORDER BY code
            """;

        try (final var statement = db.getConnection().prepareStatement(sql);
             final var result = statement.executeQuery()) {
            final var stickers = new ArrayList<Sticker>();

            while (result.next()) {
                stickers.add(mapSticker(result));
            }

            return stickers;
        } catch (SQLException exception) {
            throw new RuntimeException("Falha ao buscar figurinhas: " + exception.getMessage(), exception);
        }
    }

    public List<Sticker> findBySection(String sectionId) {
        final var sql = """
            SELECT id, collection_id, section_id, code, number, name, image_url, created_at, updated_at
            FROM stickers
            WHERE section_id = ?
            ORDER BY number, code
            """;

        try (final var statement = db.getConnection().prepareStatement(sql)) {
            statement.setString(1, sectionId);

            try (final var result = statement.executeQuery()) {
                final var stickers = new ArrayList<Sticker>();

                while (result.next()) {
                    stickers.add(mapSticker(result));
                }

                return stickers;
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Falha ao buscar figurinhas por seção: " + exception.getMessage(), exception);
        }
    }

    public List<Sticker> findByCollection(String collectionId) {
        final var sql = """
            SELECT
                stickers.id,
                stickers.collection_id,
                stickers.section_id,
                stickers.code,
                stickers.number,
                stickers.name,
                stickers.image_url,
                stickers.created_at,
                stickers.updated_at
            FROM stickers
            JOIN sections ON sections.id = stickers.section_id
            WHERE stickers.collection_id = ?
            ORDER BY sections.display_order, stickers.number, stickers.code
            """;

        try (final var statement = db.getConnection().prepareStatement(sql)) {
            statement.setString(1, collectionId);

            try (final var result = statement.executeQuery()) {
                final var stickers = new ArrayList<Sticker>();

                while (result.next()) {
                    stickers.add(mapSticker(result));
                }

                return stickers;
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Falha ao buscar figurinhas por coleção: " + exception.getMessage(), exception);
        }
    }

    public Optional<Sticker> findById(String id) {
        final var sql = """
            SELECT id, collection_id, section_id, code, number, name, image_url, created_at, updated_at
            FROM stickers
            WHERE id = ?
            """;

        try (final var statement = db.getConnection().prepareStatement(sql)) {
            statement.setString(1, id);

            try (final var result = statement.executeQuery()) {
                if (result.next()) {
                    return Optional.of(mapSticker(result));
                }

                return Optional.empty();
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Falha ao buscar figurinha por id: " + exception.getMessage(), exception);
        }
    }

    public Optional<Sticker> findByCode(String code, String collectionId) {
        final var sql = """
            SELECT id, collection_id, section_id, code, number, name, image_url, created_at, updated_at
            FROM stickers
            WHERE code = ?
              AND collection_id = ?
            """;

        try (final var statement = db.getConnection().prepareStatement(sql)) {
            statement.setString(1, code);
            statement.setString(2, collectionId);

            try (final var result = statement.executeQuery()) {
                if (result.next()) {
                    return Optional.of(mapSticker(result));
                }

                return Optional.empty();
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Falha ao buscar figurinha por código: " + exception.getMessage(), exception);
        }
    }

    public Progress findProgress(String collectionId) {
        final var sql = """
            SELECT
                COUNT(DISTINCT stickers.id)               AS total,
                COUNT(DISTINCT user_stickers.sticker_id)  AS collected
            FROM stickers
            LEFT JOIN user_stickers ON user_stickers.sticker_id = stickers.id
            WHERE stickers.collection_id = ?
            """;

        try (final var statement = db.getConnection().prepareStatement(sql)) {
            statement.setString(1, collectionId);

            try (final var result = statement.executeQuery()) {
                if (result.next()) {
                    return new Progress(
                        result.getInt("collected"),
                        result.getInt("total")
                    );
                }

                return new Progress(0, 0);
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Falha ao buscar progresso: " + exception.getMessage(), exception);
        }
    }

    public Map<String, Progress> findProgressBySection(String collectionId) {
        final var sql = """
            SELECT
                sections.id,
                COUNT(DISTINCT stickers.id)              AS total,
                COUNT(DISTINCT user_stickers.sticker_id) AS collected
            FROM sections
            JOIN stickers ON stickers.section_id = sections.id
            LEFT JOIN user_stickers ON user_stickers.sticker_id = stickers.id
            WHERE sections.collection_id = ?
            GROUP BY sections.id
            ORDER BY sections.display_order
            """;

        try (final var statement = db.getConnection().prepareStatement(sql)) {
            statement.setString(1, collectionId);

            try (final var result = statement.executeQuery()) {
                final var progressBySection = new LinkedHashMap<String, Progress>();

                while (result.next()) {
                    progressBySection.put(
                        result.getString("id"),
                        new Progress(result.getInt("collected"), result.getInt("total"))
                    );
                }

                return progressBySection;
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Falha ao buscar progresso por seção: " + exception.getMessage(), exception);
        }
    }

    public void createSticker(Sticker sticker) {
        final var sql = """
            INSERT INTO stickers (id, collection_id, section_id, code, number, name, image_url)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
        final var stickerId = sticker.getId() == null || sticker.getId().isBlank()
            ? UUID.randomUUID().toString()
            : sticker.getId();

        try (final var statement = db.getConnection().prepareStatement(sql)) {
            statement.setString(1, stickerId);
            statement.setString(2, sticker.getCollectionId());
            statement.setString(3, sticker.getSectionId());
            statement.setString(4, sticker.getCode());
            setNullableInteger(statement, 5, sticker.getNumber());
            statement.setString(6, sticker.getName());
            statement.setString(7, sticker.getImageUrl());
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Falha ao criar figurinha: " + exception.getMessage(), exception);
        }
    }

    private Sticker mapSticker(ResultSet result) throws SQLException {
        return new Sticker(
            result.getString("id"),
            result.getString("collection_id"),
            result.getString("section_id"),
            result.getString("code"),
            findNullableInteger(result, "number"),
            result.getString("name"),
            result.getString("image_url"),
            findDateTime(result, "created_at"),
            findDateTime(result, "updated_at")
        );
    }

    private Integer findNullableInteger(ResultSet result, String column) throws SQLException {
        return result.getObject(column, Integer.class);
    }

    private java.time.LocalDateTime findDateTime(ResultSet result, String column) throws SQLException {
        final Timestamp timestamp = result.getTimestamp(column);

        return timestamp.toLocalDateTime();
    }

    private void setNullableInteger(java.sql.PreparedStatement statement, int parameterIndex, Integer integerValue)
        throws SQLException {
        if (integerValue == null) {
            statement.setNull(parameterIndex, java.sql.Types.INTEGER);
        } else {
            statement.setInt(parameterIndex, integerValue);
        }
    }
}
