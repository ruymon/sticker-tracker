package sticker_tracker.ui.screens.album;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import sticker_tracker.domain.Section;
import sticker_tracker.domain.Sticker;
import sticker_tracker.domain.UserSticker;

public record AlbumData(
    String collectionName,
    List<Section> sections,
    List<Sticker> stickers,
    List<UserSticker> collectedUserStickers
) {
    public int collectedCount() {
        return collectedUserStickers.size();
    }

    public int totalCount() {
        return stickers.size();
    }

    public AlbumData withCollectedUserStickers(List<UserSticker> updatedCollectedUserStickers) {
        return new AlbumData(collectionName, sections, stickers, updatedCollectedUserStickers);
    }

    public Map<String, UserSticker> collectedByStickerId() {
        return collectedUserStickers.stream()
            .collect(Collectors.toMap(
                UserSticker::getStickerId,
                Function.identity(),
                (currentUserSticker, repeatedUserSticker) -> currentUserSticker
            ));
    }
}
