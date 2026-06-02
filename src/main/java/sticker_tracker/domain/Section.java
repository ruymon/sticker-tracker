package sticker_tracker.domain;

import java.time.LocalDateTime;

public final class Section {

    private final String id;
    private final String collectionId;
    private final String prefix;
    private final String name;
    private final SectionType type;
    private final String flagAsset;
    private final int displayOrder;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public Section(
        String id,
        String collectionId,
        String prefix,
        String name,
        SectionType type,
        String flagAsset,
        int displayOrder,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
        this.id = id;
        this.collectionId = collectionId;
        this.prefix = prefix;
        this.name = name;
        this.type = type;
        this.flagAsset = flagAsset;
        this.displayOrder = displayOrder;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getName() {
        return name;
    }

    public SectionType getType() {
        return type;
    }

    public String getFlagAsset() {
        return flagAsset;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
