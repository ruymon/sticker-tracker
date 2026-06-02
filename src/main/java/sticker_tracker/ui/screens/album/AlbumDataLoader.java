package sticker_tracker.ui.screens.album;

import java.util.function.Consumer;
import javax.swing.SwingWorker;
import sticker_tracker.config.SeederConfig;
import sticker_tracker.data.repository.CollectionRepository;
import sticker_tracker.data.repository.SectionRepository;
import sticker_tracker.data.repository.StickerRepository;
import sticker_tracker.data.repository.UserStickerRepository;
import sticker_tracker.infra.db.DatabaseConnection;

public final class AlbumDataLoader {

    public void load(Consumer<AlbumData> onSuccess, Consumer<Exception> onError) {
        final var worker = new SwingWorker<AlbumData, Void>() {
            @Override
            protected AlbumData doInBackground() {
                final var collectionRepository = createCollectionRepository();
                final var sectionRepository = createSectionRepository();
                final var stickerRepository = createStickerRepository();
                final var userStickerRepository = createUserStickerRepository();

                final var collectionName = collectionRepository
                    .findById(SeederConfig.DEFAULT_COLLECTION_ID)
                    .map(collection -> collection.getName())
                    .orElse("Álbum");
                final var sections = sectionRepository.findByCollection(SeederConfig.DEFAULT_COLLECTION_ID);
                final var stickers = stickerRepository.findByCollection(SeederConfig.DEFAULT_COLLECTION_ID);
                final var collectedUserStickers = userStickerRepository.findAll();

                return new AlbumData(collectionName, sections, stickers, collectedUserStickers);
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

    public void saveQuantity(
        String stickerId,
        int quantity,
        Runnable onSuccess,
        Consumer<Exception> onError
    ) {
        final var worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                createUserStickerRepository().updateQuantity(stickerId, quantity);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    onSuccess.run();
                } catch (Exception exception) {
                    onError.accept(exception);
                }
            }
        };
        worker.execute();
    }

    private CollectionRepository createCollectionRepository() {
        return new CollectionRepository(DatabaseConnection.getInstance());
    }

    private SectionRepository createSectionRepository() {
        return new SectionRepository(DatabaseConnection.getInstance());
    }

    private StickerRepository createStickerRepository() {
        return new StickerRepository(DatabaseConnection.getInstance());
    }

    private UserStickerRepository createUserStickerRepository() {
        return new UserStickerRepository(DatabaseConnection.getInstance());
    }
}
