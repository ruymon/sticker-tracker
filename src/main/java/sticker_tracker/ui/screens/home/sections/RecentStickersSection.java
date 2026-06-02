package sticker_tracker.ui.screens.home.sections;

import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import sticker_tracker.ui.Theme;
import sticker_tracker.ui.components.EmptyState;
import sticker_tracker.ui.components.StickerCard;
import sticker_tracker.ui.screens.home.HomeData;

public final class RecentStickersSection extends JPanel {

    public RecentStickersSection(HomeData homeData) {
        setOpaque(false);
        setAlignmentX(LEFT_ALIGNMENT);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(buildTitleLabel());
        add(Box.createVerticalStrut(Theme.SPACE_SM));

        if (homeData.recentUserStickers().isEmpty()) {
            add(new EmptyState("Nenhuma figurinha ainda.", "Vá ao Álbum para começar."));
            return;
        }

        add(buildRecentCards(homeData));
    }

    private JLabel buildTitleLabel() {
        final var title = new JLabel("Adicionadas Recentemente");
        title.setForeground(Theme.TEXT_PRIMARY);
        title.setFont(Theme.FONT_SEMIBOLD.deriveFont(Theme.SIZE_BASE));

        return title;
    }

    private JScrollPane buildRecentCards(HomeData homeData) {
        final var recentCards = new JPanel(new FlowLayout(FlowLayout.LEFT, Theme.SPACE_SM, Theme.SPACE_SM));
        recentCards.setOpaque(false);

        for (final var userSticker : homeData.recentUserStickers()) {
            final var sticker = homeData.recentStickers().get(userSticker.getStickerId());

            if (sticker != null) {
                recentCards.add(new StickerCard(sticker, true, userSticker.getQuantity()));
            }
        }

        final var scrollPane = new JScrollPane(recentCards);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(Theme.SCROLL_UNIT_INCREMENT);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);

        return scrollPane;
    }
}
