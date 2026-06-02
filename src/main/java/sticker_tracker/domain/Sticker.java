package sticker_tracker.domain;

import java.time.LocalDateTime;

public final class Sticker {

    private final String id;
    private final String collectionId;
    private final String sectionId;
    private final String code;
    private final Integer number;
    private final String name;
    private final String imageUrl;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public Sticker(
        String id,
        String collectionId,
        String sectionId,
        String code,
        Integer number,
        String name,
        String imageUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
        this.id = id;
        this.collectionId = collectionId;
        this.sectionId = sectionId;
        this.code = code;
        this.number = number;
        this.name = name;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public String getSectionId() {
        return sectionId;
    }

    public String getCode() {
        return code;
    }

    public Integer getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
