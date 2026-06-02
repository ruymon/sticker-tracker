-- 03 - Create Stickers Table

CREATE TABLE stickers (
    id            CHAR(36)     NOT NULL DEFAULT (UUID()),
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
    FOREIGN KEY (section_id)    REFERENCES sections(id),
    CONSTRAINT chk_stickers_id_uuid CHECK (IS_UUID(id)),
    CONSTRAINT chk_stickers_collection_id_uuid CHECK (IS_UUID(collection_id)),
    CONSTRAINT chk_stickers_section_id_uuid CHECK (IS_UUID(section_id)),
    CONSTRAINT chk_stickers_number_positive CHECK (number IS NULL OR number >= 1)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
