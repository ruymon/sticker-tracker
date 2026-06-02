package sticker_tracker.ui.screens.home;

public record HomeMetrics(
    int collectedCount,
    int missingCount,
    int repeatedCount,
    int ownedStickerQuantity,
    int estimatedBoughtEnvelopeCount,
    int estimatedMissingEnvelopeCount,
    int estimatedSpentInCents,
    int estimatedCostPerNewStickerInCents
) {}
