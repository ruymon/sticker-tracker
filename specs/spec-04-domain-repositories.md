# spec-04 — Domínio e Repositórios

> Classes de domínio puras e camada de acesso ao banco via JDBC.
> Repositórios são a única camada que toca SQL. Telas nunca acessam o banco diretamente.

---

## 1. Classes de Domínio (`sticker_tracker/domain/`)

### `Collection.java`
```java
public final class Collection {
    private final String id;
    private final String name;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public Collection(String id, String name, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id        = id;
        this.name      = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId()              { return id; }
    public String getName()            { return name; }
    public LocalDateTime getCreatedAt(){ return createdAt; }
    public LocalDateTime getUpdatedAt(){ return updatedAt; }
}
```

### `Section.java`
```java
public final class Section {
    private final String id;
    private final String collectionId;
    private final String prefix;
    private final String name;
    private final SectionType type;
    private final String flagAsset;    // nullable
    private final int displayOrder;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    // construtor completo + getters apenas
}
```

### `SectionType.java`
```java
public enum SectionType {
    TEAM, SPECIAL, REGIONAL;

    public static SectionType fromString(String value) {
        return switch (value.toLowerCase()) {
            case "team"     -> TEAM;
            case "special"  -> SPECIAL;
            case "regional" -> REGIONAL;
            default -> throw new IllegalArgumentException("Unknown section type: " + value);
        };
    }
}
```

### `Sticker.java`
```java
public final class Sticker {
    private final String id;
    private final String collectionId;
    private final String sectionId;
    private final String code;
    private final Integer number;      // nullable — figurinhas especiais como "00"
    private final String name;
    private final String imageUrl;     // nullable
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    // construtor completo + getters apenas
}
```

### `UserSticker.java`
```java
public final class UserSticker {
    private final String id;
    private final String stickerId;
    private final int quantity;        // sempre >= 1
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    // construtor completo + getters apenas

    public boolean isRepeated() {
        return quantity > 1;
    }

    public int repeatedCount() {
        return quantity - 1;
    }
}
```

### `Progress.java` — record de valor
```java
public record Progress(int collected, int total) {

    public int missing() {
        return total - collected;
    }

    public double percentage() {
        if (total == 0) return 0.0;
        return (double) collected / total * 100;
    }
}
```

---

## 2. Repositórios (`sticker_tracker/data/repository/`)

### Convenções obrigatórias
- Leituras: `find*` — nunca `get*` ou `fetch*`
- Criações: `create*` — nunca `save*` ou `insert*`
- Deleções: `delete*` — nunca `remove*`
- Atualizações: `update*` — nunca `edit*`
- Nunca retornar `null` — usar `Optional<T>` para resultado único
- IDs gerados com `UUID.randomUUID().toString()`

---

### `StickerRepository.java`
```java
public final class StickerRepository {

    private final DatabaseConnection db;

    public StickerRepository(DatabaseConnection db) {
        this.db = db;
    }

    public List<Sticker> findAll() { ... }

    public List<Sticker> findBySection(String sectionId) { ... }

    public List<Sticker> findByCollection(String collectionId) { ... }

    public Optional<Sticker> findById(String id) { ... }

    public Optional<Sticker> findByCode(String code, String collectionId) { ... }

    // Retorna progresso geral da coleção
    public Progress findProgress(String collectionId) {
        final var sql = """
            SELECT
                COUNT(DISTINCT s.id)          AS total,
                COUNT(DISTINCT us.sticker_id) AS collected
            FROM stickers s
            LEFT JOIN user_stickers us ON us.sticker_id = s.id
            WHERE s.collection_id = ?
            """;
        // executa e mapeia para Progress record
    }

    // Retorna progresso agrupado por seção
    public Map<String, Progress> findProgressBySection(String collectionId) {
        final var sql = """
            SELECT
                sec.id,
                COUNT(DISTINCT s.id)            AS total,
                COUNT(DISTINCT us.sticker_id)   AS collected
            FROM sections sec
            JOIN stickers s ON s.section_id = sec.id
            LEFT JOIN user_stickers us ON us.sticker_id = s.id
            WHERE sec.collection_id = ?
            GROUP BY sec.id
            ORDER BY sec.display_order
            """;
        // retorna Map<sectionId, Progress>
    }

    public void createSticker(Sticker sticker) { ... }
}
```

---

### `UserStickerRepository.java`
```java
public final class UserStickerRepository {

    private final DatabaseConnection db;

    public UserStickerRepository(DatabaseConnection db) {
        this.db = db;
    }

    public List<UserSticker> findAll() { ... }

    // Retorna apenas repetidas (quantity > 1)
    public List<UserSticker> findRepeated() { ... }

    // Retorna as N mais recentes por created_at DESC
    public List<UserSticker> findRecent(int limit) { ... }

    public Optional<UserSticker> findByStickerId(String stickerId) { ... }

    // Cria novo registro com quantity = 1
    public void createUserSticker(String stickerId) { ... }

    // Incrementa quantity em 1
    public void updateIncrementQuantity(String stickerId) { ... }

    // Decrementa quantity em 1 — se chegar a 0, deleta o registro
    public void updateDecrementQuantity(String stickerId) {
        final var current = findByStickerId(stickerId);

        current.ifPresent(us -> {
            if (us.getQuantity() == 1) {
                deleteUserSticker(us.getId());
            } else {
                // UPDATE user_stickers SET quantity = quantity - 1 WHERE sticker_id = ?
            }
        });
    }

    public void deleteUserSticker(String id) { ... }
}
```

---

### `SectionRepository.java`
```java
public final class SectionRepository {

    private final DatabaseConnection db;

    public List<Section> findAll() { ... }

    public List<Section> findByCollection(String collectionId) { ... }

    public Optional<Section> findByPrefix(String prefix, String collectionId) { ... }

    public void createSection(Section section) { ... }
}
```

---

### `CollectionRepository.java`
```java
public final class CollectionRepository {

    private final DatabaseConnection db;

    public List<Collection> findAll() { ... }

    public Optional<Collection> findById(String id) { ... }

    public void createCollection(Collection collection) { ... }
}
```

---

## 3. Checklist de Conclusão

- [ ] Todas as classes de domínio criadas — imutáveis, sem setters
- [ ] `Progress` record com `missing()` e `percentage()`
- [ ] `StickerRepository` com `findProgress()` e `findProgressBySection()`
- [ ] `UserStickerRepository` com lógica de decremento → deleção
- [ ] `SectionRepository` e `CollectionRepository` implementados
- [ ] Zero retorno `null` em qualquer método público
- [ ] Queries funcionando contra o banco seedado
