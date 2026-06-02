package sticker_tracker.ui.screens.home;

import java.util.List;
import java.util.Map;
import sticker_tracker.domain.Progress;
import sticker_tracker.domain.Section;
import sticker_tracker.domain.Sticker;
import sticker_tracker.domain.UserSticker;

public record HomeData(
    Progress progress,
    Map<String, Progress> progressBySection,
    List<UserSticker> recentUserStickers,
    List<UserSticker> repeatedUserStickers,
    List<Section> sections,
    Map<String, Sticker> recentStickers,
    Map<String, Sticker> repeatedStickers
) {
    public boolean isEmpty() {
        return progress.collected() == 0 && recentUserStickers.isEmpty();
    }
}
