package sticker_tracker.ui.screens.home;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.swing.SwingWorker;
import sticker_tracker.config.AlbumEconomicsConfig;
import sticker_tracker.config.SeederConfig;
import sticker_tracker.data.repository.SectionRepository;
import sticker_tracker.data.repository.StickerRepository;
import sticker_tracker.data.repository.UserStickerRepository;
import sticker_tracker.domain.Progress;
import sticker_tracker.domain.Sticker;
import sticker_tracker.domain.UserSticker;
import sticker_tracker.infra.db.DatabaseConnection;

public final class HomeDataLoader {

    private static final int RECENT_LIMIT = 10;

    public void load(Consumer<HomeData> onSuccess, Consumer<Exception> onError) {
        final var worker = new SwingWorker<HomeData, Void>() {
            @Override
            protected HomeData doInBackground() {
                final var stickerRepository = createStickerRepository();
                final var userStickerRepository = createUserStickerRepository();
                final var sectionRepository = createSectionRepository();

                final var progress = stickerRepository.findProgress(SeederConfig.DEFAULT_COLLECTION_ID);
                final var progressBySection = stickerRepository.findProgressBySection(SeederConfig.DEFAULT_COLLECTION_ID);
                final var recentUserStickers = userStickerRepository.findRecent(RECENT_LIMIT);
                final var userStickers = userStickerRepository.findAll();
                final var repeatedUserStickers = userStickerRepository.findRepeated();
                final var sections = sectionRepository.findByCollection(SeederConfig.DEFAULT_COLLECTION_ID);
                final var recentStickers = findStickersByUserStickers(stickerRepository, recentUserStickers);
                final var repeatedStickers = findStickersByUserStickers(stickerRepository, repeatedUserStickers);
                final var metrics = buildMetrics(progress, userStickers);
                final var whatsAppTradeMessage = buildWhatsAppTradeMessage(repeatedUserStickers, repeatedStickers);

                return new HomeData(
                    progress,
                    metrics,
                    whatsAppTradeMessage,
                    progressBySection,
                    recentUserStickers,
                    sections,
                    recentStickers
                );
            }

            @Override
            protected void done() {
                try {
                    onSuccess.accept(get());
                } catch (Exception exception) {
                    onError.accept(exception);
                }
            }
        };
        worker.execute();
    }

    private StickerRepository createStickerRepository() {
        return new StickerRepository(DatabaseConnection.getInstance());
    }

    private UserStickerRepository createUserStickerRepository() {
        return new UserStickerRepository(DatabaseConnection.getInstance());
    }

    private SectionRepository createSectionRepository() {
        return new SectionRepository(DatabaseConnection.getInstance());
    }

    private Map<String, Sticker> findStickersByUserStickers(
        StickerRepository stickerRepository,
        List<UserSticker> userStickers
    ) {
        return userStickers.stream()
            .map(UserSticker::getStickerId)
            .distinct()
            .map(stickerRepository::findById)
            .flatMap(Optional::stream)
            .collect(Collectors.toMap(Sticker::getId, sticker -> sticker));
    }

    private String buildWhatsAppTradeMessage(
        List<UserSticker> repeatedUserStickers,
        Map<String, Sticker> repeatedStickers
    ) {
        final var repeatedCodes = repeatedUserStickers.stream()
            .map(userSticker -> Optional.ofNullable(repeatedStickers.get(userSticker.getStickerId())))
            .flatMap(Optional::stream)
            .map(Sticker::getCode)
            .collect(Collectors.joining(", "));

        if (repeatedCodes.isBlank()) {
            return "Ainda não tenho figurinhas repetidas da Copa 2026 para trocar.";
        }

        return "Tenho essas repetidas da Copa 2026: " + repeatedCodes
            + "\nMe chama para trocar!";
    }

    private HomeMetrics buildMetrics(Progress progress, List<UserSticker> userStickers) {
        final var ownedStickerQuantity = userStickers.stream()
            .mapToInt(UserSticker::getQuantity)
            .sum();
        final var repeatedCount = userStickers.stream()
            .mapToInt(userSticker -> Math.max(userSticker.repeatedCount(), 0))
            .sum();
        final var estimatedBoughtEnvelopeCount = ceilDivide(
            ownedStickerQuantity,
            AlbumEconomicsConfig.STICKERS_PER_ENVELOPE
        );
        final var estimatedMissingEnvelopeCount = ceilDivide(
            progress.missing(),
            AlbumEconomicsConfig.STICKERS_PER_ENVELOPE
        );
        final var estimatedSpentInCents = estimatedBoughtEnvelopeCount
            * AlbumEconomicsConfig.ENVELOPE_PRICE_IN_CENTS;
        final var estimatedCostPerNewStickerInCents = progress.collected() == 0
            ? 0
            : (int) Math.round((double) estimatedSpentInCents / progress.collected());

        return new HomeMetrics(
            progress.collected(),
            progress.missing(),
            repeatedCount,
            ownedStickerQuantity,
            estimatedBoughtEnvelopeCount,
            estimatedMissingEnvelopeCount,
            estimatedSpentInCents,
            estimatedCostPerNewStickerInCents
        );
    }

    private int ceilDivide(int value, int divisor) {
        if (value <= 0) {
            return 0;
        }

        return (value + divisor - 1) / divisor;
    }
}
