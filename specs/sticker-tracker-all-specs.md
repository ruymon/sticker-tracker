# spec-00 — Convenções e Padrões de Código (BÍBLIA DO PROJETO)

> Este documento é a referência absoluta de estilo e qualidade do projeto Cola Aí.
> Todo código gerado deve seguir estas regras sem exceção. Consistência > preferência pessoal.

---

## 1. Nomenclatura de Métodos (Verb Families)

Cada operação tem uma família de verbos canônica. **Nunca misture verbos da mesma família.**

| Operação | Verbo canônico | Proibido |
|---|---|---|
| Leitura | `find*` | `get*`, `fetch*`, `read*`, `load*` |
| Criação/persistência | `create*` | `save*`, `build*`, `make*`, `insert*` |
| Deleção | `delete*` | `remove*`, `destroy*`, `purge*`, `drop*` |
| Validação/asserção | `assert*` | `ensure*`, `check*`, `validate*` |
| Atualização | `update*` | `edit*`, `modify*`, `change*` |

### Exemplos corretos
```java
// ✅ correto
stickerRepository.findAll();
stickerRepository.findById(id);
stickerRepository.findBySection(sectionId);
userStickerRepository.createUserSticker(sticker);
userStickerRepository.deleteUserSticker(id);
userStickerRepository.updateQuantity(id, quantity);
```

```java
// ❌ errado
stickerRepository.getAll();
stickerRepository.fetchById(id);
userStickerRepository.saveSticker(sticker);
userStickerRepository.removeSticker(id);
```

---

## 2. Convenções de Nomenclatura

### Java
| Elemento | Convenção | Exemplo |
|---|---|---|
| Classes | PascalCase | `StickerRepository`, `UserSticker` |
| Métodos | camelCase | `findBySection()`, `createUserSticker()` |
| Variáveis | camelCase | `stickerCard`, `userCollection` |
| Constantes | UPPER_SNAKE_CASE | `DEFAULT_COLLECTION_ID`, `MAX_QUANTITY` |
| Pacotes | lowercase | `sticker_tracker.data.repository` |

### Banco de dados
| Elemento | Convenção | Exemplo |
|---|---|---|
| Tabelas | snake_case, plural | `user_stickers`, `sections` |
| Colunas | snake_case | `section_id`, `image_url`, `created_at` |
| PKs | sempre `id` | `id CHAR(36)` |
| FKs | `{tabela_singular}_id` | `sticker_id`, `collection_id` |

### Nomes devem revelar intenção
```java
// ✅ correto — linguagem de domínio
Sticker sticker = stickerRepository.findById(stickerId);
List<Sticker> missingStickers = stickerRepository.findMissing();
int repeatedCount = userSticker.getQuantity() - 1;

// ❌ errado — genérico e sem intenção
Object data = repo.get(id);
List<Object> info = repo.findAll();
int value = sticker.getQty() - 1;
```

---

## 3. Qualidade de Código

- Cada método faz **uma coisa só**
- Se um método precisa de comentário para ser entendido, **refatore**
- Separe blocos de responsabilidade com **linhas em branco**

```java
// ✅ correto — linear, claro
public List<Sticker> findRepeated() {
    List<UserSticker> userStickers = userStickerRepository.findAll();

    return userStickers.stream()
        .filter(us -> us.getQuantity() > 1)
        .map(us -> stickerRepository.findById(us.getStickerId()))
        .flatMap(Optional::stream)
        .collect(Collectors.toList());
}

// ❌ errado — denso, ilegível
public List<Sticker> getRepeated() {
    return userStickerRepository.findAll().stream().filter(us->us.getQuantity()>1).map(us->stickerRepository.findById(us.getStickerId())).flatMap(Optional::stream).collect(Collectors.toList());
}
```

---

## 4. Imutabilidade e Estado

- **`final` é o padrão** para campos e variáveis locais
- Zero mutações escondidas; zero side effects dentro de expressões

```java
// ✅ correto
final String stickerId = userSticker.getStickerId();
final Optional<Sticker> sticker = stickerRepository.findById(stickerId);

// ❌ errado — reutilização de variável
String id = userSticker.getStickerId();
id = stickerRepository.findById(id).map(Sticker::getName).orElse("unknown");
```

---

## 5. Tipos e Null Safety

- **`null` nunca é retornado por método público** — use `Optional<T>`
- `Object` como tipo genérico é **proibido**
- Use `var` para inferência quando o tipo for óbvio (Java 10+)

```java
// ✅ correto
public Optional<Sticker> findById(String id) { ... }

var stickers = stickerRepository.findAll();

// ❌ errado
public Sticker findById(String id) {
    return null;
}
```

---

## 6. Streams vs Loops

Prefira streams declarativos para transformações de coleções.

```java
// ✅ preferido
List<String> repeatedCodes = userStickers.stream()
    .filter(us -> us.getQuantity() > 1)
    .map(UserSticker::getStickerId)
    .collect(Collectors.toList());

// ✅ aceitável — loop quando há side effects explícitos
for (Sticker sticker : stickers) {
    stickerRepository.createUserSticker(sticker);
}
```

---

## 7. Java Específico

- **Records** para objetos de valor imutáveis simples (Java 16+)
```java
record Progress(int collected, int total) {
    int missing() { return total - collected; }
}
```

- Getters/setters só onde necessário; prefira construtores com `final`
- Mantenha código debuggável: uma operação por linha em chains complexos

---

## 8. Estrutura de Pacotes

```
sticker_tracker/
├── domain/         — entidades puras, records, value objects
├── data/
│   └── repository/ — acesso ao banco, usa find*/create*/delete*/update*
├── infra/
│   └── db/         — DatabaseConnection (Singleton)
├── ui/
│   ├── screens/    — telas completas
│   └── components/ — componentes reutilizáveis
└── Main.java
```

---

## 9. O que nunca fazer

| Proibido | Alternativa |
|---|---|
| Retornar `null` em método público | `Optional<T>` |
| Usar `Object` como tipo | Generics ou classe de domínio |
| Misturar verbos (`get` + `find`) | Escolha um e mantenha |
| One-liners densos | Quebre em linhas legíveis |
| Nomes genéricos: `data`, `info`, `value`, `obj` | Nome de domínio |
-e 

---

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
-e 

---

# spec-02 — Design System

> Tokens visuais, carregamento de fontes e configuração global do tema.
> Nenhum componente de UI deve ser implementado antes desta spec estar completa.
> Zero valores hardcoded em qualquer componente — tudo referencia Theme.*

---

## 1. Theme.java

```java
// sticker_tracker/ui/Theme.java
public final class Theme {

    private Theme() {}

    // Backgrounds
    public static final Color BG_PRIMARY    = Color.decode("#0A0A0A");
    public static final Color BG_SECONDARY  = Color.decode("#141414");
    public static final Color BG_CARD       = Color.decode("#1C1C1C");
    public static final Color BG_HOVER      = Color.decode("#242424");

    // Text
    public static final Color TEXT_PRIMARY   = Color.decode("#F5F5F5");
    public static final Color TEXT_SECONDARY = Color.decode("#A3A3A3");
    public static final Color TEXT_MUTED     = Color.decode("#525252");

    // Accent — verde Copa
    public static final Color ACCENT         = Color.decode("#22C55E");
    public static final Color ACCENT_HOVER   = Color.decode("#16A34A");
    public static final Color ACCENT_MUTED   = Color.decode("#14532D");

    // Borders
    public static final Color BORDER         = Color.decode("#262626");
    public static final Color BORDER_FOCUS   = Color.decode("#404040");

    // Status
    public static final Color SUCCESS        = Color.decode("#22C55E");
    public static final Color WARNING        = Color.decode("#EAB308");
    public static final Color DANGER         = Color.decode("#EF4444");

    // Fonts — populadas pelo FontLoader
    public static Font FONT_REGULAR;
    public static Font FONT_MEDIUM;
    public static Font FONT_SEMIBOLD;
    public static Font FONT_BOLD;
    public static Font FONT_MONO;

    // Font sizes
    public static final float SIZE_XS   = 11f;
    public static final float SIZE_SM   = 12f;
    public static final float SIZE_BASE = 14f;
    public static final float SIZE_MD   = 16f;
    public static final float SIZE_LG   = 20f;
    public static final float SIZE_XL   = 24f;
    public static final float SIZE_2XL  = 32f;

    // Spacing
    public static final int SPACE_XS  = 4;
    public static final int SPACE_SM  = 8;
    public static final int SPACE_MD  = 16;
    public static final int SPACE_LG  = 24;
    public static final int SPACE_XL  = 32;
    public static final int SPACE_2XL = 48;

    // Border radius
    public static final int RADIUS_SM  = 6;
    public static final int RADIUS_MD  = 12;
    public static final int RADIUS_LG  = 16;
    public static final int RADIUS_XL  = 24;
}
```

---

## 2. FontLoader.java

```java
// sticker_tracker/ui/FontLoader.java
public final class FontLoader {

    private FontLoader() {}

    public static void load() {
        Theme.FONT_REGULAR  = loadFont("/assets/fonts/Geist-Regular.ttf");
        Theme.FONT_MEDIUM   = loadFont("/assets/fonts/Geist-Medium.ttf");
        Theme.FONT_SEMIBOLD = loadFont("/assets/fonts/Geist-SemiBold.ttf");
        Theme.FONT_BOLD     = loadFont("/assets/fonts/Geist-Bold.ttf");
        Theme.FONT_MONO     = loadFont("/assets/fonts/GeistMono-Regular.ttf");
    }

    private static Font loadFont(String path) {
        try (var stream = FontLoader.class.getResourceAsStream(path)) {
            final var font = Font.createFont(Font.TRUETYPE_FONT, stream);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
            return font;
        } catch (Exception e) {
            System.err.println("Font not found: " + path + " — using fallback");
            return new Font("SansSerif", Font.PLAIN, 14);
        }
    }
}
```

---

## 3. FlatLaf — Overrides globais

Aplicado em `Main.java` antes de qualquer JFrame:

```java
UIManager.put("defaultFont",          Theme.FONT_REGULAR.deriveFont(Theme.SIZE_BASE));
UIManager.put("Panel.background",     Theme.BG_PRIMARY);
UIManager.put("Button.arc",           Theme.RADIUS_MD);
UIManager.put("Component.arc",        Theme.RADIUS_MD);
UIManager.put("TextComponent.arc",    Theme.RADIUS_MD);
UIManager.put("ScrollBar.thumbArc",   Theme.RADIUS_SM);
UIManager.put("ScrollBar.width",      6);
UIManager.put("ScrollBar.thumb",      Theme.BG_HOVER);
UIManager.put("TextField.background", Theme.BG_SECONDARY);
UIManager.put("TextField.foreground", Theme.TEXT_PRIMARY);
UIManager.put("TextField.caretColor", Theme.ACCENT);
UIManager.put("Separator.foreground", Theme.BORDER);
```

---

## 4. Referência de Tokens

| Token | Valor | Uso |
|---|---|---|
| `BG_PRIMARY` | `#0A0A0A` | Fundo da janela principal |
| `BG_SECONDARY` | `#141414` | Sidebar, inputs |
| `BG_CARD` | `#1C1C1C` | Cards, painéis elevados |
| `BG_HOVER` | `#242424` | Hover em botões e itens |
| `TEXT_PRIMARY` | `#F5F5F5` | Texto principal |
| `TEXT_SECONDARY` | `#A3A3A3` | Texto de suporte |
| `TEXT_MUTED` | `#525252` | Placeholder, metadados |
| `ACCENT` | `#22C55E` | Verde Copa — CTAs, progresso |
| `BORDER` | `#262626` | Separadores, bordas |

---

## 5. Checklist de Conclusão

- [ ] `Theme.java` criado com todos os tokens
- [ ] `FontLoader.load()` chamado antes de qualquer Swing
- [ ] Geist Regular, Medium, SemiBold, Bold carregando sem erro
- [ ] Geist Mono carregando sem erro
- [ ] Fallback para SansSerif se fonte não encontrada
- [ ] FlatLaf dark theme aplicado globalmente
- [ ] Janela abre com fundo `#0A0A0A` e fonte Geist
-e 

---

# spec-03 — Schema do Banco de Dados

> Arquivos SQL de migrations e seed. Sem MigrationRunner em Java — tudo rodado manualmente.
> Ordem numérica garante execução correta. Migrations definem estrutura; seed insere dados.

---

## 1. Migrations — `db/migrations/`

Rodar em ordem numérica uma única vez ao criar o banco.

### `01-create-collections.sql`
```sql
CREATE TABLE collections (
    id         CHAR(36)     NOT NULL,
    name       VARCHAR(255) NOT NULL,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### `02-create-sections.sql`
```sql
CREATE TABLE sections (
    id            CHAR(36)                          NOT NULL,
    collection_id CHAR(36)                          NOT NULL,
    prefix        VARCHAR(10)                       NOT NULL,
    name          VARCHAR(255)                      NOT NULL,
    type          ENUM('team','special','regional') NOT NULL,
    flag_asset    VARCHAR(50)                           NULL,
    display_order INT                               NOT NULL DEFAULT 0,
    created_at    DATETIME                          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME                          NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_sections_prefix (prefix),
    INDEX idx_sections_collection (collection_id),
    FOREIGN KEY (collection_id) REFERENCES collections(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### `03-create-stickers.sql`
```sql
CREATE TABLE stickers (
    id            CHAR(36)     NOT NULL,
    collection_id CHAR(36)     NOT NULL,
    section_id    CHAR(36)     NOT NULL,
    code          VARCHAR(10)  NOT NULL,
    number        INT              NULL,
    name          VARCHAR(255) NOT NULL,
    image_url     VARCHAR(500)     NULL,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE INDEX idx_stickers_code_collection (code, collection_id),
    INDEX idx_stickers_section (section_id),
    FOREIGN KEY (collection_id) REFERENCES collections(id),
    FOREIGN KEY (section_id)    REFERENCES sections(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

> **Decisão arquitetural:** `code` não é PK — apenas único por `collection_id`. Um álbum futuro
> (Copa 2030) pode ter um `BRA1` sem conflito com o de 2026. Documentar na ADR.

### `04-create-user-stickers.sql`
```sql
CREATE TABLE user_stickers (
    id         CHAR(36) NOT NULL,
    sticker_id CHAR(36) NOT NULL,
    quantity   INT      NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE INDEX idx_user_stickers_sticker (sticker_id),
    FOREIGN KEY (sticker_id) REFERENCES stickers(id),
    CONSTRAINT chk_quantity_positive CHECK (quantity >= 1)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

> **Decisão arquitetural:** `quantity >= 1` enforced por constraint no banco. Ausência de
> registro = não tem. `quantity = 1` = coletada. `quantity >= 2` = repetida para troca.

---

## 2. Seed — `db/seed/`

Rodar após todas as migrations. Usar `INSERT IGNORE` para idempotência — rodar duas vezes não duplica dados.

### `01-seed-collection.sql`
```sql
INSERT IGNORE INTO collections (id, name)
VALUES ('00000000-0000-0000-0000-000000000001', 'Panini FIFA World Cup 2026');
```

### `02-seed-sections.sql`

Inserir todas as seções derivadas do catálogo Panini WC 2026.
Fonte: https://github.com/danieltartaro/sticker-swap/blob/main/data/raw/panini-wc-2026-catalog.json

```sql
INSERT IGNORE INTO sections (id, collection_id, prefix, name, type, flag_asset, display_order) VALUES
('sec-00000001', '00000000-0000-0000-0000-000000000001', '00',     'We Are Panini',           'special',  NULL,        1),
('sec-00000002', '00000000-0000-0000-0000-000000000001', 'FWC',    'FIFA World Cup 2026',     'special',  NULL,        2),
('sec-00000003', '00000000-0000-0000-0000-000000000001', 'MEX',    'Mexico',                  'team',     'MEX.png',   3),
('sec-00000004', '00000000-0000-0000-0000-000000000001', 'RSA',    'South Africa',            'team',     'RSA.png',   4),
('sec-00000005', '00000000-0000-0000-0000-000000000001', 'KOR',    'South Korea',             'team',     'KOR.png',   5),
('sec-00000006', '00000000-0000-0000-0000-000000000001', 'CZE',    'Czechia',                 'team',     'CZE.png',   6),
('sec-00000007', '00000000-0000-0000-0000-000000000001', 'CAN',    'Canada',                  'team',     'CAN.png',   7),
('sec-00000008', '00000000-0000-0000-0000-000000000001', 'BIH',    'Bosnia and Herzegovina',  'team',     'BIH.png',   8),
('sec-00000009', '00000000-0000-0000-0000-000000000001', 'QAT',    'Qatar',                   'team',     'QAT.png',   9),
('sec-00000010', '00000000-0000-0000-0000-000000000001', 'SUI',    'Switzerland',             'team',     'SUI.png',   10),
('sec-00000011', '00000000-0000-0000-0000-000000000001', 'BRA',    'Brazil',                  'team',     'BRA.png',   11),
('sec-00000012', '00000000-0000-0000-0000-000000000001', 'MAR',    'Morocco',                 'team',     'MAR.png',   12),
-- continuar para todos os 50+ times/seções do catálogo
-- gerar o INSERT completo a partir do JSON em:
-- https://raw.githubusercontent.com/danieltartaro/sticker-swap/refs/heads/main/data/raw/panini-wc-2026-catalog.json
;
```

### `03-seed-stickers.sql`

Inserir todas as 1034 figurinhas. Gerar a partir do mesmo JSON.

```sql
INSERT IGNORE INTO stickers (id, collection_id, section_id, code, number, name, image_url) VALUES
-- figurinhas especiais
('stk-000001', '00000000-0000-0000-0000-000000000001', 'sec-00000001', '00',   NULL, 'Panini Logo',      NULL),
('stk-000002', '00000000-0000-0000-0000-000000000001', 'sec-00000002', 'FWC1', 1,    'Official Emblem1', NULL),
('stk-000003', '00000000-0000-0000-0000-000000000001', 'sec-00000002', 'FWC2', 2,    'Official Emblem2', NULL),
-- ... continuar para todas as 1034 figurinhas
-- number = parte numérica do code (MEX14 → 14, FWC1 → 1, 00 → NULL)
-- section_id = id da seção correspondente ao team do JSON
;
```

> **Nota de geração:** Os arquivos `02-seed-sections.sql` e `03-seed-stickers.sql` devem ser
> gerados a partir do JSON do catálogo. O agente deve ler o JSON e gerar os INSERTs completos
> cobrindo todas as entradas. Usar `INSERT IGNORE` para idempotência.

---

## 3. Como Resetar o Banco

```bash
# Apagar tudo e recriar
docker compose down -v
docker compose up -d

# Rodar migrations
mysql -u sticker_tracker -psticker_tracker sticker_tracker < db/migrations/01-create-collections.sql
mysql -u sticker_tracker -psticker_tracker sticker_tracker < db/migrations/02-create-sections.sql
mysql -u sticker_tracker -psticker_tracker sticker_tracker < db/migrations/03-create-stickers.sql
mysql -u sticker_tracker -psticker_tracker sticker_tracker < db/migrations/04-create-user-stickers.sql

# Rodar seed
mysql -u sticker_tracker -psticker_tracker sticker_tracker < db/seed/01-seed-collection.sql
mysql -u sticker_tracker -psticker_tracker sticker_tracker < db/seed/02-seed-sections.sql
mysql -u sticker_tracker -psticker_tracker sticker_tracker < db/seed/03-seed-stickers.sql
```

---

## 4. Checklist de Conclusão

- [ ] `01-create-collections.sql` roda sem erro
- [ ] `02-create-sections.sql` roda sem erro
- [ ] `03-create-stickers.sql` roda sem erro
- [ ] `04-create-user-stickers.sql` roda sem erro
- [ ] `01-seed-collection.sql` insere 1 registro
- [ ] `02-seed-sections.sql` insere todas as seções (rodar duas vezes não duplica)
- [ ] `03-seed-stickers.sql` insere 1034 figurinhas (rodar duas vezes não duplica)
- [ ] Foreign keys funcionando — inserção fora de ordem gera erro esperado
-e 

---

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
-e 

---

# spec-05 — Componentes de UI Base

> Componentes reutilizáveis e AppFrame principal.
> Nenhuma tela deve ser implementada antes desta spec estar concluída.
> Zero valores hardcoded — tudo via Theme.*

---

## 1. AppFrame — Janela Principal

```java
// sticker_tracker/ui/AppFrame.java
public final class AppFrame extends JFrame {

    private final CardLayout cardLayout;
    private final JPanel contentPanel;

    public AppFrame() {
        setTitle("Cola Aí");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 720));
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BG_PRIMARY);

        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        buildLayout();
    }

    private void buildLayout() {
        final var sidebar = buildSidebar();
        setLayout(new BorderLayout());
        add(sidebar,       BorderLayout.WEST);
        add(contentPanel,  BorderLayout.CENTER);
    }

    private JPanel buildSidebar() {
        // Largura fixa: 220px, fundo BG_SECONDARY
        // Topo: logo "Cola Aí" — FONT_BOLD SIZE_LG
        // Itens de navegação: Home, Álbum
        // Item ativo: barra vertical de 3px em ACCENT à esquerda
        // Rodapé: slogan em TEXT_MUTED SIZE_XS
    }

    public void showScreen(String screenName) {
        cardLayout.show(contentPanel, screenName);
    }
}
```

---

## 2. RoundedPanel

Painel com bordas arredondadas desenhadas via `Graphics2D`.

```java
// sticker_tracker/ui/components/RoundedPanel.java
public class RoundedPanel extends JPanel {

    private final int radius;
    private Color backgroundColor;

    public RoundedPanel(int radius) {
        this.radius          = radius;
        this.backgroundColor = Theme.BG_CARD;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        final var g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(backgroundColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        g2.dispose();
        super.paintComponent(g);
    }

    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        repaint();
    }
}
```

---

## 3. RoundedButton

Botão com hover state e três variantes.

```java
// sticker_tracker/ui/components/RoundedButton.java
public class RoundedButton extends JButton {

    public enum Variant { PRIMARY, SECONDARY, GHOST }

    private final Variant variant;
    private boolean hovered = false;

    public RoundedButton(String text, Variant variant) {
        super(text);
        this.variant = variant;
        setOpaque(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFont(Theme.FONT_MEDIUM.deriveFont(Theme.SIZE_SM));

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
            @Override public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        final var g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(resolveBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), Theme.RADIUS_MD, Theme.RADIUS_MD);
        g2.dispose();
        super.paintComponent(g);
    }

    private Color resolveBackground() {
        return switch (variant) {
            case PRIMARY   -> hovered ? Theme.ACCENT_HOVER : Theme.ACCENT;
            case SECONDARY -> hovered ? Theme.BG_HOVER     : Theme.BG_CARD;
            case GHOST     -> hovered ? Theme.BG_HOVER     : new Color(0, 0, 0, 0);
        };
    }
}
```

---

## 4. StickerCard

Card de figurinha com estado coletada vs não coletada.

```java
// sticker_tracker/ui/components/StickerCard.java
public class StickerCard extends RoundedPanel {

    private static final int CARD_WIDTH  = 100;
    private static final int CARD_HEIGHT = 140;

    private final Sticker sticker;
    private final boolean collected;
    private final int quantity;

    public StickerCard(Sticker sticker, boolean collected, int quantity) {
        super(Theme.RADIUS_MD);
        this.sticker   = sticker;
        this.collected = collected;
        this.quantity  = quantity;

        setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        setBackgroundColor(collected ? Theme.BG_CARD : Theme.BG_SECONDARY);
        buildCard();
    }

    private void buildCard() {
        setLayout(new BorderLayout(0, Theme.SPACE_XS));
        setBorder(BorderFactory.createEmptyBorder(
            Theme.SPACE_SM, Theme.SPACE_SM, Theme.SPACE_SM, Theme.SPACE_SM
        ));
        add(buildImageArea(), BorderLayout.CENTER);
        add(buildInfoArea(),  BorderLayout.SOUTH);
    }

    private JPanel buildImageArea() {
        // Área 80x80
        // Se imageUrl != null: carrega via SwingWorker, exibe quando pronto
        // Se null: placeholder com código centralizado em FONT_MONO
        // Se não coletada: alpha 0.35f aplicado no paintComponent
    }

    private JPanel buildInfoArea() {
        // Código — FONT_MONO SIZE_XS TEXT_MUTED
        // Nome truncado — FONT_REGULAR SIZE_XS
        //   TEXT_PRIMARY se coletada, TEXT_MUTED se não coletada
        // Badge "x{quantity}" em ACCENT se quantity > 1
    }
}
```

---

## 5. ProgressBarCustom

```java
// sticker_tracker/ui/components/ProgressBarCustom.java
public class ProgressBarCustom extends JPanel {

    private double percentage = 0.0;

    public ProgressBarCustom() {
        setOpaque(false);
        setPreferredSize(new Dimension(0, 8));
    }

    public void updateProgress(Progress progress) {
        this.percentage = progress.percentage();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        final var g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Track
        g2.setColor(Theme.BG_HOVER);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());

        // Fill
        final int fillWidth = (int) (getWidth() * percentage / 100);
        if (fillWidth > 0) {
            g2.setColor(Theme.ACCENT);
            g2.fillRoundRect(0, 0, fillWidth, getHeight(), getHeight(), getHeight());
        }

        g2.dispose();
    }
}
```

---

## 6. FilterBar

```java
// sticker_tracker/ui/components/FilterBar.java
public class FilterBar extends JPanel {

    public enum Filter { ALL, COLLECTED, MISSING, REPEATED }

    private Filter activeFilter = Filter.ALL;
    private Consumer<Filter> onFilterChange;

    public FilterBar() {
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.LEFT, Theme.SPACE_SM, 0));
        buildFilters();
    }

    public void setOnFilterChange(Consumer<Filter> callback) {
        this.onFilterChange = callback;
    }

    private void buildFilters() {
        for (var filter : Filter.values()) {
            final var btn = new RoundedButton(labelFor(filter), RoundedButton.Variant.SECONDARY);
            btn.addActionListener(e -> {
                activeFilter = filter;
                refreshButtonStates();
                if (onFilterChange != null) onFilterChange.accept(filter);
            });
            add(btn);
        }
    }

    private String labelFor(Filter filter) {
        return switch (filter) {
            case ALL       -> "Todas";
            case COLLECTED -> "Coletadas";
            case MISSING   -> "Faltando";
            case REPEATED  -> "Repetidas";
        };
    }

    private void refreshButtonStates() {
        // atualiza visual dos botões para refletir activeFilter
    }
}
```

---

## 7. WrapLayout

Grid que quebra linha automaticamente — Java não tem flexbox nativo.

```java
// sticker_tracker/ui/components/WrapLayout.java
//
// Baseado na implementação de Rob Camick (domínio público).
// Fonte: https://tips4java.wordpress.com/2008/11/06/wrap-layout/
//
// Extensão de FlowLayout que recalcula o layout respeitando a largura
// do container pai e quebra os componentes em múltiplas linhas.
// Necessário para o grid de StickerCards no AlbumScreen.

public class WrapLayout extends FlowLayout {

    public WrapLayout() { super(); }
    public WrapLayout(int align) { super(align); }
    public WrapLayout(int align, int hgap, int vgap) { super(align, hgap, vgap); }

    @Override
    public Dimension preferredLayoutSize(Container target) {
        return layoutSize(target, true);
    }

    @Override
    public Dimension minimumLayoutSize(Container target) {
        return layoutSize(target, false);
    }

    private Dimension layoutSize(Container target, boolean preferred) {
        // implementação que calcula altura real considerando quebras de linha
        // baseada na largura disponível do container
    }
}
```

---

## 8. Checklist de Conclusão

- [ ] `AppFrame` abre com sidebar e área de conteúdo
- [ ] Navegação entre telas via `showScreen()` funcionando
- [ ] `RoundedPanel` com antialiasing correto
- [ ] `RoundedButton` com hover nas 3 variantes
- [ ] `StickerCard` — coletada e não coletada visualmente distintas
- [ ] `ProgressBarCustom` com fill proporcional
- [ ] `FilterBar` troca estado ativo e dispara callback
- [ ] `WrapLayout` quebra cards em múltiplas linhas corretamente
- [ ] Zero valores hardcoded — tudo via `Theme.*`
-e 

---

# spec-06 — HomeScreen

> Tela principal do app. Progresso da coleção, recentes e repetidas.
> Primeira tela exibida ao abrir o app.

---

## 1. Layout Geral

```
┌─────────────────────────────────────────────────────┐
│  Cola Aí          A cola que o professor não pode.. │  ← Header
├─────────────────────────────────────────────────────┤
│  ┌───────────────────────────────────────────────┐  │
│  │  Sua Coleção                          9.2%    │  │  ← Progress Card
│  │  ████░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░    │  │
│  │  95 coletadas · 939 faltando                  │  │
│  │  [▼ Ver por seção]                            │  │
│  └───────────────────────────────────────────────┘  │
│                                                     │
│  Adicionadas Recentemente                           │
│  ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐    │
│  │BRA14 │ │MEX3  │ │ARG7  │ │FRA1  │ │...   │    │
│  └──────┘ └──────┘ └──────┘ └──────┘ └──────┘    │
│                                                     │
│  Repetidas (12 figurinhas · 18 disponíveis)        │
│  │ MEX3  Johan Vasquez          x2              │  │
│  │ BRA14 Vinicius Júnior        x3              │  │
│  [📋 Copiar lista para WhatsApp]                    │
└─────────────────────────────────────────────────────┘
```

---

## 2. Carregamento de Dados

Dados carregados via `SwingWorker` — nunca bloquear a EDT.

```java
private void loadData() {
    final var worker = new SwingWorker<HomeData, Void>() {

        @Override
        protected HomeData doInBackground() {
            final var progress = stickerRepository.findProgress(DEFAULT_COLLECTION_ID);
            final var bySection = stickerRepository.findProgressBySection(DEFAULT_COLLECTION_ID);
            final var recent   = userStickerRepository.findRecent(10);
            final var repeated = userStickerRepository.findRepeated();
            final var sections = sectionRepository.findByCollection(DEFAULT_COLLECTION_ID);
            return new HomeData(progress, bySection, recent, repeated, sections);
        }

        @Override
        protected void done() {
            try {
                updateUI(get());
            } catch (Exception e) {
                showErrorState();
            }
        }
    };
    worker.execute();
}

private record HomeData(
    Progress progress,
    Map<String, Progress> bySection,
    List<UserSticker> recent,
    List<UserSticker> repeated,
    List<Section> sections
) {}
```

---

## 3. Progress Card (expansível)

```java
private RoundedPanel buildProgressCard() {
    // Linha superior: "Sua Coleção" à esquerda + "9.2%" à direita em ACCENT
    // ProgressBarCustom
    // "95 coletadas · 939 faltando" em TEXT_SECONDARY
    // Botão ghost "Ver por seção ▼" que expande/colapsa o painel de seções
}
```

Painel de seções expandido:
```
Brazil      ████████░░  16/20  80%
Mexico      ████░░░░░░   8/20  40%
...
```
Cada linha tem um `ProgressBarCustom` com altura 4px.

---

## 4. Seção Recentes

```java
private JPanel buildRecentSection() {
    // Título "Adicionadas Recentemente" — FONT_SEMIBOLD SIZE_BASE
    // JScrollPane horizontal com StickerCards compactos (80x110)
    // userStickerRepository.findRecent(10)
    // Estado vazio: "Nenhuma figurinha ainda. Vá ao Álbum para começar."
}
```

---

## 5. Seção Repetidas + CTA WhatsApp

```java
private JPanel buildRepeatedSection() {
    // Título "Repetidas (X figurinhas · Y disponíveis)"
    // Lista: código | nome | badge xN
    // Máximo 5 itens visíveis, scroll se mais
    // Botão PRIMARY "Copiar lista para WhatsApp"
}

private void copyWhatsAppMessage() {
    final var repeated = userStickerRepository.findRepeated();

    final var codes = repeated.stream()
        .map(us -> stickerRepository.findById(us.getStickerId()))
        .flatMap(Optional::stream)
        .map(Sticker::getCode)
        .collect(Collectors.joining(", "));

    final var message = "Tenho essas repetidas da Copa 2026: " + codes
        + "\nMe chama para trocar! 🔁";

    final var clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(new StringSelection(message), null);

    // Feedback: botão muda para "✓ Copiado!" por 2 segundos via javax.swing.Timer
}
```

---

## 6. Estados da Tela

| Estado | Comportamento |
|---|---|
| Carregando | Label "Carregando..." central |
| Vazio | Mensagem de boas-vindas + instrução para ir ao Álbum |
| Normal | Layout completo |
| Erro | Mensagem de erro + botão "Tentar novamente" |

---

## 7. Checklist de Conclusão

- [ ] Progress card com percentual e barra
- [ ] Expansão por seção mostra progresso individual
- [ ] Recentes em scroll horizontal com StickerCards
- [ ] Lista de repetidas com badge de quantidade
- [ ] Botão WhatsApp copia mensagem formatada
- [ ] Feedback visual "Copiado!" por 2 segundos
- [ ] Estado vazio exibido corretamente
- [ ] SwingWorker — UI não trava durante carregamento
-e 

---

# spec-07 — AlbumScreen

> Grid completo do álbum com filtros, busca e gerenciamento de figurinhas.
> É aqui que o usuário marca o que tem e quanto tem de cada figurinha.

---

## 1. Layout Geral

```
┌─────────────────────────────────────────────────────────┐
│  Álbum  Copa do Mundo 2026                   95 / 1034  │  ← Header
│  [Todas] [Coletadas] [Faltando] [Repetidas]  [🔍 busca] │  ← Toolbar
├─────────────────────────────────────────────────────────┤
│  🇧🇷 Brazil (16/20)                                      │  ← Section Header
│  ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐         │
│  │BRA1  │ │BRA2  │ │BRA3 ░│ │BRA4 ░│ │...   │  ...    │  ← StickerCards
│  └──────┘ └──────┘ └──────┘ └──────┘ └──────┘         │
│  🇲🇽 Mexico (8/20)                                       │
│  ┌──────┐ ┌──────┐  ...                                │
└─────────────────────────────────────────────────────────┘
```

Cards opacos = não coletadas.

---

## 2. Carregamento de Dados

```java
private void loadAlbumData() {
    final var worker = new SwingWorker<AlbumData, Void>() {

        @Override
        protected AlbumData doInBackground() {
            final var sections  = sectionRepository.findByCollection(DEFAULT_COLLECTION_ID);
            final var stickers  = stickerRepository.findByCollection(DEFAULT_COLLECTION_ID);
            final var collected = userStickerRepository.findAll();
            return new AlbumData(sections, stickers, collected);
        }

        @Override
        protected void done() {
            try {
                renderAlbum(get());
            } catch (Exception e) {
                showErrorState();
            }
        }
    };
    worker.execute();
}

private record AlbumData(
    List<Section> sections,
    List<Sticker> stickers,
    List<UserSticker> collected
) {}
```

---

## 3. Cache de Imagens

Imagens de figurinhas carregadas sob demanda e cacheadas em memória.
Sem cache: scroll recarrega a mesma imagem centenas de vezes — UI laga.

```java
// Cache compartilhado entre todos os StickerCards
private static final Map<String, ImageIcon> IMAGE_CACHE = new HashMap<>();

private void loadImageAsync(String imageUrl, JLabel imageLabel) {
    if (IMAGE_CACHE.containsKey(imageUrl)) {
        imageLabel.setIcon(IMAGE_CACHE.get(imageUrl));
        return;
    }

    final var worker = new SwingWorker<ImageIcon, Void>() {
        @Override
        protected ImageIcon doInBackground() throws Exception {
            final var url   = new URL(imageUrl);
            final var image = ImageIO.read(url);
            final var scaled = image.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        }

        @Override
        protected void done() {
            try {
                final var icon = get();
                IMAGE_CACHE.put(imageUrl, icon);
                imageLabel.setIcon(icon);
            } catch (Exception e) {
                // mantém placeholder — falha silenciosa
            }
        }
    };
    worker.execute();
}
```

---

## 4. Grid por Seção

```java
private JPanel buildAlbumGrid(AlbumData data) {
    final var container = new JPanel();
    container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
    container.setBackground(Theme.BG_PRIMARY);

    // Mapas para lookup rápido O(1)
    final var collectedMap = data.collected().stream()
        .collect(Collectors.toMap(UserSticker::getStickerId, us -> us));

    for (var section : data.sections()) {
        final var sectionStickers = data.stickers().stream()
            .filter(s -> s.getSectionId().equals(section.getId()))
            .collect(Collectors.toList());

        container.add(buildSectionHeader(section, sectionStickers, collectedMap));
        container.add(buildSectionGrid(sectionStickers, collectedMap));
    }

    return container;
}

private JPanel buildSectionGrid(List<Sticker> stickers, Map<String, UserSticker> collectedMap) {
    final var panel = new JPanel(new WrapLayout(FlowLayout.LEFT, Theme.SPACE_SM, Theme.SPACE_SM));
    panel.setBackground(Theme.BG_PRIMARY);

    for (var sticker : stickers) {
        final var userSticker = collectedMap.get(sticker.getId());
        final boolean collected = userSticker != null;
        final int quantity = collected ? userSticker.getQuantity() : 0;

        final var card = new StickerCard(sticker, collected, quantity);
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openStickerDialog(sticker, Optional.ofNullable(userSticker));
            }
        });
        panel.add(card);
    }

    return panel;
}
```

---

## 5. Dialog de Gerenciamento

```java
private void openStickerDialog(Sticker sticker, Optional<UserSticker> userSticker) {
    final var dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), true);
    dialog.setTitle(sticker.getCode() + " — " + sticker.getName());
    dialog.setSize(320, 200);
    dialog.setLocationRelativeTo(this);

    final int currentQty = userSticker.map(UserSticker::getQuantity).orElse(0);
    final var spinner = new JSpinner(new SpinnerNumberModel(currentQty, 0, 99, 1));

    final var saveBtn = new RoundedButton("Salvar", RoundedButton.Variant.PRIMARY);
    saveBtn.addActionListener(e -> {
        final int newQty = (int) spinner.getValue();

        if (newQty == 0 && userSticker.isPresent()) {
            userStickerRepository.deleteUserSticker(userSticker.get().getId());
        } else if (newQty > 0 && userSticker.isEmpty()) {
            userStickerRepository.createUserSticker(sticker.getId());
            if (newQty > 1) {
                // updateQuantity direto se quantidade > 1
            }
        } else if (newQty > 0) {
            // UPDATE quantity para o novo valor
        }

        dialog.dispose();
        refreshAlbum();
    });

    // layout do dialog com spinner e botões
    dialog.setVisible(true);
}
```

---

## 6. Filtros e Busca

```java
private List<Sticker> applyFilter(
    List<Sticker> all,
    Map<String, UserSticker> collectedMap,
    FilterBar.Filter filter,
    String searchTerm
) {
    var filtered = switch (filter) {
        case ALL       -> all;
        case COLLECTED -> all.stream()
            .filter(s -> collectedMap.containsKey(s.getId()))
            .collect(Collectors.toList());
        case MISSING   -> all.stream()
            .filter(s -> !collectedMap.containsKey(s.getId()))
            .collect(Collectors.toList());
        case REPEATED  -> all.stream()
            .filter(s -> collectedMap.containsKey(s.getId())
                      && collectedMap.get(s.getId()).isRepeated())
            .collect(Collectors.toList());
    };

    if (!searchTerm.isBlank()) {
        final var term = searchTerm.toLowerCase();
        filtered = filtered.stream()
            .filter(s -> s.getCode().toLowerCase().contains(term)
                      || s.getName().toLowerCase().contains(term))
            .collect(Collectors.toList());
    }

    return filtered;
}
```

### Debounce na busca (evita refiltrar a cada tecla)

```java
private javax.swing.Timer searchDebounce;

private void onSearchChanged(String term) {
    if (searchDebounce != null && searchDebounce.isRunning()) {
        searchDebounce.stop();
    }
    searchDebounce = new javax.swing.Timer(300, e -> refreshGrid(term));
    searchDebounce.setRepeats(false);
    searchDebounce.start();
}
```

---

## 7. Estados da Tela

| Estado | Comportamento |
|---|---|
| Carregando | Label "Carregando álbum..." |
| Filtro sem resultado | "Nenhuma figurinha para este filtro" |
| Busca sem resultado | "Nenhum resultado para '{termo}'" |
| Normal | Grid completo por seção |

---

## 8. Checklist de Conclusão

- [ ] Grid exibe todas as seções em display_order
- [ ] Flag da seção carrega de `assets/flags/{prefix}.png`
- [ ] Cards coletados vs não coletados visualmente distintos
- [ ] WrapLayout quebra cards corretamente ao redimensionar janela
- [ ] Cache de imagens — scroll suave sem lag
- [ ] Filtros funcionam para os 4 estados
- [ ] Busca com debounce de 300ms
- [ ] Dialog de quantidade abre ao clicar no card
- [ ] Salvar atualiza banco e refresca o grid
- [ ] HomeScreen atualiza após mudança no álbum
-e 

---

# spec-08 — Documentação Acadêmica

> Documento técnico para entrega na disciplina.
> Escrito em Markdown — conversão posterior para LaTeX/ABNT.
> Foco nas decisões de engenharia, independente do código-fonte.

---

## 1. Estrutura do Documento

```
docs/artigo/
├── 00-capa.md
├── 01-resumo.md
├── 02-introducao.md
├── 03-banco-de-dados.md
├── 04-ambiente.md
├── 05-estrutura-de-arquivos.md
├── 06-interface.md
├── 07-desafios-tecnicos.md   ← seção dedicada aos desafios
├── 08-conclusao.md
└── 09-referencias.md
```

---

## 2. Conteúdo de Cada Seção

### 00 — Capa
- Título: **Cola Aí — Tracker de Figurinhas da Copa do Mundo 2026**
- Subtítulo: *"A cola que o professor não pode reprovar."*
- Instituição: Instituto Mauá de Tecnologia
- Disciplina: Programação Orientada a Objetos em Java
- Autores, data, semestre

---

### 01 — Resumo
150–200 palavras cobrindo:
- O que é o app e o problema que resolve
- Tecnologias (Java, Swing, MySQL, JDBC, Docker)
- Principais decisões arquiteturais
- Resultado entregue

---

### 02 — Introdução
- Contexto: Copa do Mundo FIFA 2026 e a febre de figurinhas no Brasil
- Problema: sem ferramenta, o colecionador não sabe o que falta nem o que pode trocar
- Solução: app desktop com rastreamento de coleção e geração de lista de trocas
- Escopo: o que o app faz e o que foi conscientemente deixado de fora

---

### 03 — Banco de Dados

**Tom:** Decisões de modelagem como escolhas de engenharia.

- Por que `collections` existe com apenas um álbum em produção — não otimizar para o caso simples quando o custo de generalização é zero
- Por que `code` não é PK — o mesmo código "BRA1" pode existir em Copa 2026 e Copa 2030; UUID como PK e `code` único por `collection_id` resolve sem complexidade extra
- A tabela `sections` como abstração — permite que times, figurinhas especiais (FWC, Panini) e regionais sejam tratados uniformemente pelo sistema
- `quantity >= 1` em `user_stickers` — ausência de registro representa "não tem"; um registro representa "tem"; quantity >= 2 representa "repetida para troca". Elimina flags booleanas redundantes; constraint no banco garante integridade
- Separação `migrations/` vs `seed/` — migrations definem estrutura (DDL) e nunca mudam; seeds inserem dados e podem ser reexecutados. Misturar tornaria difícil recriar só a estrutura
- Numeração sequencial das migrations (01, 02...) — ordem explícita e previsível; sem dependência de parsing de timestamp

---

### 04 — Ambiente

**Tom:** Justificar escolhas de infraestrutura como decisões de engenharia.

- Docker para isolamento do banco — reprodutibilidade total; qualquer pessoa clona e sobe com um comando; sem configuração manual do MySQL
- Por que não containerizar o app Java — Swing requer display do sistema operacional; rodar UI em container exige X11 forwarding sem benefício para o projeto
- Volume Docker que persiste dados entre reinicializações — banco não é efêmero
- JDBC explícito sem ORM — requisito da disciplina e benefício real: queries visíveis, fluxo de dados rastreável, zero magia escondida
- Padrão Repository encapsula JDBC — mantém código de negócio limpo sem abrir mão do controle sobre SQL

---

### 05 — Estrutura de Arquivos

**Tom:** Separação de responsabilidades como princípio, não convenção.

- Layered Architecture vs MVC clássico — quatro camadas com responsabilidades distintas: o que o sistema conhece (domain), como acessa dados (data), como se conecta à infraestrutura (infra), como se apresenta (ui)
- `domain` sem dependências externas — classes puras em Java; testáveis e portáveis
- `migrations/` dentro de `db/` — evolução do schema junto da responsabilidade que o utiliza
- Convenções de nomenclatura como contrato — `find*`, `create*`, `delete*`, `update*` em todos os repositórios; qualquer desenvolvedor sabe onde procurar sem ler a implementação
- `snake_case` no banco, `camelCase` no Java, `PascalCase` para classes — cada contexto usa a convenção da sua linguagem

---

### 06 — Interface Gráfica

**Tom:** UI como sistema de design, não lista de telas.

- Design system centralizado em `Theme.java` — tokens de cor, tipografia e espaçamento; mudança visual em um arquivo reflete em toda a interface
- Fontes customizadas (Geist) carregadas do classpath — aparência consistente em qualquer máquina, sem depender de instalação local
- Componentes atômicos e reutilizáveis — `RoundedPanel` e `RoundedButton` são átomos sem contexto de negócio; `StickerCard` combina átomos com lógica de apresentação; telas são composições de componentes
- FlatLaf como base de tema — aparência moderna sobre Swing; zero geração automática de código
- `paintComponent()` + `Graphics2D` para elementos customizados — bordas arredondadas, barra de progresso, alpha em cards não coletados; tudo desenhado explicitamente

---

### 07 — Desafios Técnicos

**Tom:** Honesto sobre o que foi difícil e como foi resolvido.

#### WrapLayout — Grid que quebra linha

O Swing não possui um layout equivalente ao `flexbox` do CSS. O `FlowLayout` padrão não quebra linha corretamente ao redimensionar a janela — os cards de figurinha transbordavam para fora do container.

A solução foi implementar `WrapLayout`, uma extensão de `FlowLayout` que recalcula o tamanho preferido considerando a largura disponível do container pai. A implementação é baseada no trabalho de Rob Camick (domínio público, amplamente referenciada na comunidade Java). O código foi adaptado e integrado ao projeto com a devida atribuição na documentação e nos comentários do arquivo.

**Por que não existe nativamente:** o Swing foi projetado antes de layouts fluidos serem comuns em interfaces desktop. Soluções modernas como JavaFX têm `FlowPane` com comportamento equivalente.

---

#### Cache de Imagens com HashMap

Com 1034 figurinhas no grid, carregar cada imagem toda vez que o usuário faz scroll tornava a interface inutilizável — lag visível e consumo desnecessário de I/O.

A solução foi um `HashMap<String, ImageIcon>` compartilhado entre os cards do `AlbumScreen`. Na primeira vez que uma imagem é requisitada, ela é carregada de forma assíncrona via `SwingWorker` e armazenada no cache. Nas requisições seguintes, o retorno é imediato.

`HashMap` não foi ensinado explicitamente na disciplina, mas é uma estrutura de dados fundamental para qualquer linguagem. A decisão de usá-lo veio da necessidade prática: sem cache a tela era inutilizável; com cache o scroll ficou fluido. A lógica é simples — chave é a URL da imagem, valor é o `ImageIcon` já carregado.

---

#### SwingWorker — Threading na UI

O Swing tem uma regra fundamental: toda operação de I/O (banco de dados, rede, disco) que ocorre na EDT (Event Dispatch Thread — a thread que renderiza a UI) trava a interface durante a operação.

Na prática: sem `SwingWorker`, abrir o AlbumScreen com 1034 figurinhas travaria a janela por vários segundos. Com `SwingWorker`, a query ao banco roda em uma thread separada e a UI continua responsiva; quando os dados chegam, o método `done()` é chamado de volta na EDT para atualizar os componentes com segurança.

`SwingWorker` não foi ensinado na disciplina, mas sua necessidade é inevitável em qualquer app com banco de dados e interface gráfica. A alternativa — não usá-lo — produziria uma UI travada e inapresentável.

---

#### Separação Migrations / Seed

O padrão ensinado na disciplina não aborda evolução de schema de banco de dados. O projeto adotou a convenção de separar arquivos DDL (que definem estrutura) de arquivos DML com dados iniciais (seed).

A decisão de rodar os SQLs manualmente em vez de automatizá-los em Java foi intencional: mantém o JDBC do app focado em operações de negócio, torna o processo de setup transparente e auditável, e evita adicionar complexidade de runtime para um problema que ocorre uma única vez.

---

### 08 — Conclusão

- Síntese das decisões e o que elas proporcionaram
- O que o projeto demonstra além do requisito mínimo
- Limitações da versão atual (single-user, sem sync, sem login)
- Evoluções naturais: múltiplos usuários, troca via rede, suporte a outros álbuns

---

### 09 — Referências

Formato ABNT NBR 6023:
- Documentação Java SE 17 — Oracle
- FlatLaf — FormDev Software GmbH
- MySQL Connector/J 8.x — Oracle
- Docker Documentation — Docker Inc.
- Catálogo Panini WC 2026 — github.com/danieltartaro/sticker-swap
- WrapLayout — Rob Camick, tips4java.wordpress.com, 2008

---

## 3. Apresentação PowerPoint

### Estrutura dos Slides

| # | Título | Foco |
|---|---|---|
| 1 | Abertura | Logo Cola Aí + slogan + nomes |
| 2 | O Problema | A dor do colecionador em uma frase |
| 3 | A Solução | O que o app faz em 3 bullets |
| 4 | Demo — HomeScreen | Screenshot com dados reais |
| 5 | Demo — AlbumScreen | Screenshot com grid |
| 6 | Banco de Dados | Diagrama ER + decisões de modelagem |
| 7 | Ambiente | Docker + JDBC — por que essas escolhas |
| 8 | Estrutura de Arquivos | Diagrama de camadas |
| 9 | Interface | Design system + componentes atômicos |
| 10 | Desafios Técnicos | WrapLayout, HashMap, SwingWorker |
| 11 | Conclusão | Aprendizados + próximos passos |

### Guia visual
- Fundo `#0A0A0A` — consistente com o app
- Fonte Geist — mesma do app
- Destaque em verde `#22C55E`
- Máximo 4 bullets por slide
- Screenshots grandes, sem texto em cima

---

## 4. Checklist de Conclusão

- [ ] Todos os `.md` do artigo criados
- [ ] Seção 07 cobre WrapLayout, HashMap, SwingWorker e Migrations com honestidade técnica
- [ ] Cada seção técnica descreve decisões, não implementação
- [ ] Nenhuma seção depende de código para fazer sentido
- [ ] PowerPoint com 11 slides conforme estrutura
- [ ] Referências em formato ABNT NBR 6023
- [ ] WrapLayout de Rob Camick referenciado corretamente
-e 

---

