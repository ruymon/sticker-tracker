package sticker_tracker.domain;

import java.time.LocalDateTime;

public final class UserSticker {

    private static final int COLLECTED_QUANTITY = 1;

    private final String id;
    private final String stickerId;
    private final int quantity;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public UserSticker(
        String id,
        String stickerId,
        int quantity,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
        this.id = id;
        this.stickerId = stickerId;
        this.quantity = quantity;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public String getStickerId() {
        return stickerId;
    }

    public int getQuantity() {
        return quantity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public boolean isRepeated() {
        return quantity > COLLECTED_QUANTITY;
    }

    public int repeatedCount() {
        return quantity - COLLECTED_QUANTITY;
    }
}
