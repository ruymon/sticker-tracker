package sticker_tracker.ui.screens.album.sections;

import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import sticker_tracker.ui.Theme;

public final class AlbumHeaderSection extends JPanel {

    public AlbumHeaderSection(String collectionName, int collectedCount, int totalCount) {
        setOpaque(false);
        setAlignmentX(LEFT_ALIGNMENT);
        setLayout(new BorderLayout());

        final var titlePanel = new JPanel(new BorderLayout(Theme.SPACE_MD, Theme.SPACE_NONE));
        titlePanel.setOpaque(false);

        final var title = new JLabel("Álbum");
        title.setForeground(Theme.TEXT_PRIMARY);
        title.setFont(Theme.FONT_BOLD.deriveFont(Theme.SIZE_XL));

        final var collection = new JLabel(collectionName);
        collection.setForeground(Theme.TEXT_SECONDARY);
        collection.setFont(Theme.FONT_REGULAR.deriveFont(Theme.SIZE_BASE));

        final var progress = new JLabel(collectedCount + " / " + totalCount);
        progress.setForeground(Theme.ACCENT);
        progress.setFont(Theme.FONT_SEMIBOLD.deriveFont(Theme.SIZE_BASE));
        progress.setHorizontalAlignment(SwingConstants.RIGHT);

        titlePanel.add(title, BorderLayout.WEST);
        titlePanel.add(collection, BorderLayout.CENTER);

        add(titlePanel, BorderLayout.WEST);
        add(progress, BorderLayout.EAST);
    }
}
