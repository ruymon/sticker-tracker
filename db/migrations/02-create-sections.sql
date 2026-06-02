-- 02 - Create Sections Table

CREATE TABLE sections (
    id            CHAR(36)                          NOT NULL DEFAULT (UUID()),
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
    FOREIGN KEY (collection_id) REFERENCES collections(id),
    CONSTRAINT chk_sections_id_uuid CHECK (IS_UUID(id)),
    CONSTRAINT chk_sections_collection_id_uuid CHECK (IS_UUID(collection_id))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
