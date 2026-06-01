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
