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
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao buscar figurinhas: " + e.getMessage(), e);
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
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao buscar figurinhas por seção: " + e.getMessage(), e);
        }
    }

    public List<Sticker> findByCollection(String collectionId) {
        final var sql = """
            SELECT s.id, s.collection_id, s.section_id, s.code, s.number, s.name, s.image_url, s.created_at, s.updated_at
            FROM stickers s
            JOIN sections sec ON sec.id = s.section_id
            WHERE s.collection_id = ?
            ORDER BY sec.display_order, s.number, s.code
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
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao buscar figurinhas por coleção: " + e.getMessage(), e);
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
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao buscar figurinha por id: " + e.getMessage(), e);
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
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao buscar figurinha por código: " + e.getMessage(), e);
        }
    }

    public Progress findProgress(String collectionId) {
        final var sql = """
            SELECT
                COUNT(DISTINCT s.id)          AS total,
                COUNT(DISTINCT us.sticker_id) AS collected
            FROM stickers s
            LEFT JOIN user_stickers us ON us.sticker_id = s.id
            WHERE s.collection_id = ?
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
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao buscar progresso: " + e.getMessage(), e);
        }
    }

    public Map<String, Progress> findProgressBySection(String collectionId) {
        final var sql = """
            SELECT
                sec.id,
                COUNT(DISTINCT s.id)          AS total,
                COUNT(DISTINCT us.sticker_id) AS collected
            FROM sections sec
            JOIN stickers s ON s.section_id = sec.id
            LEFT JOIN user_stickers us ON us.sticker_id = s.id
            WHERE sec.collection_id = ?
            GROUP BY sec.id
            ORDER BY sec.display_order
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
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao buscar progresso por seção: " + e.getMessage(), e);
        }
    }

    public void createSticker(Sticker sticker) {
        final var sql = """
            INSERT INTO stickers (id, collection_id, section_id, code, number, name, image_url)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
        final var id = sticker.getId() == null || sticker.getId().isBlank()
            ? UUID.randomUUID().toString()
            : sticker.getId();

        try (final var statement = db.getConnection().prepareStatement(sql)) {
            statement.setString(1, id);
            statement.setString(2, sticker.getCollectionId());
            statement.setString(3, sticker.getSectionId());
            statement.setString(4, sticker.getCode());
            setNullableInteger(statement, 5, sticker.getNumber());
            statement.setString(6, sticker.getName());
            statement.setString(7, sticker.getImageUrl());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao criar figurinha: " + e.getMessage(), e);
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

    private void setNullableInteger(java.sql.PreparedStatement statement, int index, Integer value)
        throws SQLException {
        if (value == null) {
            statement.setNull(index, java.sql.Types.INTEGER);
        } else {
            statement.setInt(index, value);
        }
    }
}
