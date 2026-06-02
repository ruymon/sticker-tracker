package sticker_tracker.ui.screens.home;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.swing.SwingWorker;
import sticker_tracker.config.SeederConfig;
import sticker_tracker.data.repository.SectionRepository;
import sticker_tracker.data.repository.StickerRepository;
import sticker_tracker.data.repository.UserStickerRepository;
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
                final var repeatedUserStickers = userStickerRepository.findRepeated();
                final var sections = sectionRepository.findByCollection(SeederConfig.DEFAULT_COLLECTION_ID);
                final var recentStickers = findStickersByUserStickers(stickerRepository, recentUserStickers);
                final var repeatedStickers = findStickersByUserStickers(stickerRepository, repeatedUserStickers);

                return new HomeData(
                    progress,
                    progressBySection,
                    recentUserStickers,
                    repeatedUserStickers,
                    sections,
                    recentStickers,
                    repeatedStickers
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
}
