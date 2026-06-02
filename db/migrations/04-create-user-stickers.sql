-- 04 - Create User Stickers Table

CREATE TABLE user_stickers (
    id         CHAR(36) NOT NULL DEFAULT (UUID()),
    sticker_id CHAR(36) NOT NULL,
    quantity   INT      NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE INDEX idx_user_stickers_sticker (sticker_id),
    FOREIGN KEY (sticker_id) REFERENCES stickers(id),
    CONSTRAINT chk_user_stickers_id_uuid CHECK (IS_UUID(id)),
    CONSTRAINT chk_user_stickers_sticker_id_uuid CHECK (IS_UUID(sticker_id)),
    CONSTRAINT chk_quantity_positive CHECK (quantity >= 1)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
