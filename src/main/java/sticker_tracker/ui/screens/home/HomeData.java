package sticker_tracker.ui.screens.home;

import java.util.List;
import java.util.Map;
import sticker_tracker.domain.Progress;
import sticker_tracker.domain.Section;
import sticker_tracker.domain.Sticker;
import sticker_tracker.domain.UserSticker;

public record HomeData(
    Progress progress,
    HomeMetrics metrics,
    String whatsAppTradeMessage,
    Map<String, Progress> progressBySection,
    List<UserSticker> recentUserStickers,
    List<Section> sections,
    Map<String, Sticker> recentStickers
) {
    public boolean isEmpty() {
        return progress.total() == 0 && sections.isEmpty();
    }
}
