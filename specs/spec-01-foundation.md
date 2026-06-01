# spec-01 — Fundação do Projeto

> Estrutura de pastas, dependências externas e ambiente de banco via Docker.
> Nenhuma feature deve ser implementada antes desta spec estar completa e o banco rodando.

---

## 1. Estrutura de Pastas

```
sticker-tracker/
├── src/
│   └── main/
│       └── java/
│           └── sticker_tracker/
│               ├── domain/
│               │   ├── Collection.java
│               │   ├── Section.java
│               │   ├── Sticker.java
│               │   └── UserSticker.java
│               ├── data/
│               │   └── repository/
│               │       ├── CollectionRepository.java
│               │       ├── SectionRepository.java
│               │       ├── StickerRepository.java
│               │       └── UserStickerRepository.java
│               ├── infra/
│               │   └── db/
│               │       └── DatabaseConnection.java
│               ├── ui/
│               │   ├── components/
│               │   │   ├── RoundedPanel.java
│               │   │   ├── RoundedButton.java
│               │   │   ├── StickerCard.java
│               │   │   ├── ProgressBarCustom.java
│               │   │   ├── FilterBar.java
│               │   │   └── WrapLayout.java
│               │   ├── screens/
│               │   │   ├── HomeScreen.java
│               │   │   └── AlbumScreen.java
│               │   ├── AppFrame.java
│               │   ├── Theme.java
│               │   └── FontLoader.java
│               └── Main.java
├── src/
│   └── main/
│       └── resources/
│           └── assets/
│               ├── fonts/
│               │   ├── Geist-Regular.ttf
│               │   ├── Geist-Medium.ttf
│               │   ├── Geist-SemiBold.ttf
│               │   ├── Geist-Bold.ttf
│               │   └── GeistMono-Regular.ttf
│               ├── flags/
│               │   └── (PNG 32x32 por prefix: BRA.png, MEX.png ...)
│               └── data/
│                   └── (não usado — seed é SQL puro)
├── db/
│   ├── migrations/          ← estrutura do schema, ordem numérica
│   │   ├── 01-create-collections.sql
│   │   ├── 02-create-sections.sql
│   │   ├── 03-create-stickers.sql
│   │   └── 04-create-user-stickers.sql
│   └── seed/                ← dados iniciais, rodados separadamente
│       ├── 01-seed-collection.sql
│       ├── 02-seed-sections.sql
│       └── 03-seed-stickers.sql
├── lib/
│   ├── flatlaf-3.x.jar
│   └── mysql-connector-j-8.x.jar
├── specs/
├── docs/
├── docker-compose.yml
└── README.md
```

---

## 2. Separação migrations vs seed

| Pasta | Conteúdo | Quando rodar |
|---|---|---|
| `db/migrations/` | DDL — CREATE TABLE, ALTER TABLE | Uma vez ao criar o banco |
| `db/seed/` | DML — INSERT com dados iniciais | Uma vez após as migrations |

**Por que separado:** migrations definem estrutura e nunca mudam. Seeds inserem dados e podem ser rerodados se o banco for resetado. Misturar os dois tornaria difícil recriar apenas a estrutura sem dados.

**Como rodar** (manual, via MySQL client ou DBeaver):
```bash
# Migrations — em ordem numérica
mysql -u sticker_tracker -p sticker_tracker < db/migrations/01-create-collections.sql
mysql -u sticker_tracker -p sticker_tracker < db/migrations/02-create-sections.sql
mysql -u sticker_tracker -p sticker_tracker < db/migrations/03-create-stickers.sql
mysql -u sticker_tracker -p sticker_tracker < db/migrations/04-create-user-stickers.sql

# Seed — após todas as migrations
mysql -u sticker_tracker -p sticker_tracker < db/seed/01-seed-collection.sql
mysql -u sticker_tracker -p sticker_tracker < db/seed/02-seed-sections.sql
mysql -u sticker_tracker -p sticker_tracker < db/seed/03-seed-stickers.sql
```

---

## 3. Dependências Externas (lib/)

| JAR | Versão | Finalidade |
|---|---|---|
| `flatlaf` | 3.x | Tema moderno para Swing |
| `mysql-connector-j` | 8.x | Driver JDBC para MySQL |

Ambos ficam na pasta `lib/` do repositório. Zero Maven/Gradle — compila via classpath.

---

## 4. Docker — Banco de Dados

O MySQL roda em Docker. O app Java roda nativamente (Swing requer display do SO).

### `docker-compose.yml`
```yaml
services:
  db:
    image: mysql:8.0
    container_name: sticker-tracker-db
    restart: unless-stopped
    environment:
      MYSQL_ROOT_PASSWORD: sticker_tracker
      MYSQL_DATABASE: sticker_tracker
      MYSQL_USER: sticker_tracker
      MYSQL_PASSWORD: sticker_tracker
    ports:
      - "3306:3306"
    volumes:
      - sticker_tracker_data:/var/lib/mysql
    command: --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci

volumes:
  sticker_tracker_data:
```

### Comandos
```bash
docker compose up -d    # sobe o banco
docker compose down     # para o banco
docker compose down -v  # para e apaga todos os dados
```

---

## 5. DatabaseConnection — Singleton JDBC

```java
// sticker_tracker/infra/db/DatabaseConnection.java
public final class DatabaseConnection {

    private static final String URL      = "jdbc:mysql://localhost:3306/sticker_tracker?useSSL=false&serverTimezone=UTC";
    private static final String USER     = "sticker_tracker";
    private static final String PASSWORD = "sticker_tracker";

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
}
```

---

## 6. Main.java — Entry Point

```java
public class Main {
    public static void main(String[] args) {
        FontLoader.load();
        FlatDarkLaf.setup();
        configureFlatLaf();

        SwingUtilities.invokeLater(() -> {
            final var frame = new AppFrame();
            frame.setVisible(true);
        });
    }

    private static void configureFlatLaf() {
        UIManager.put("defaultFont",        Theme.FONT_REGULAR.deriveFont(Theme.SIZE_BASE));
        UIManager.put("Panel.background",   Theme.BG_PRIMARY);
        UIManager.put("Button.arc",         Theme.RADIUS_MD);
        UIManager.put("Component.arc",      Theme.RADIUS_MD);
        UIManager.put("ScrollBar.thumbArc", Theme.RADIUS_SM);
        UIManager.put("ScrollBar.width",    6);
    }
}
```

---

## 7. Checklist de Conclusão

- [ ] Estrutura de pastas criada conforme o diagrama
- [ ] `lib/` contém FlatLaf e MySQL Connector
- [ ] `assets/fonts/` contém Geist e Geist Mono
- [ ] `docker-compose.yml` na raiz do projeto
- [ ] `docker compose up -d` sobe o banco sem erro
- [ ] Migrations rodadas em ordem numérica sem erro
- [ ] Seeds rodados após migrations sem erro
- [ ] App conecta ao MySQL via JDBC sem erro
- [ ] Janela vazia abre com tema escuro aplicado
