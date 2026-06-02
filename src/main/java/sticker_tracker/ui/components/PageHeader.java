package sticker_tracker.ui.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import sticker_tracker.ui.Theme;

public class PageHeader extends JPanel {

    public PageHeader(String titleText, String subtitleText) {
        this(titleText, subtitleText, null);
    }

    public PageHeader(String titleText, String subtitleText, Component actionComponent) {
        setOpaque(false);
        setAlignmentX(LEFT_ALIGNMENT);
        setLayout(new BorderLayout(Theme.SPACE_LG, Theme.SPACE_NONE));
        setPreferredSize(new Dimension(Theme.SPACE_NONE, Theme.PAGE_HEADER_HEIGHT));
        setMinimumSize(new Dimension(Theme.SPACE_NONE, Theme.PAGE_HEADER_HEIGHT));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.PAGE_HEADER_HEIGHT));

        add(buildTitlePanel(titleText, subtitleText), BorderLayout.WEST);

        if (actionComponent != null) {
            add(actionComponent, BorderLayout.EAST);
        }
    }

    private JPanel buildTitlePanel(String titleText, String subtitleText) {
        final var titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        final var title = new JLabel(titleText);
        title.setForeground(Theme.TEXT_PRIMARY);
        title.setFont(Theme.FONT_BOLD.deriveFont(Theme.SIZE_2XL));
        title.setAlignmentX(LEFT_ALIGNMENT);

        final var subtitle = new JLabel(subtitleText);
        subtitle.setForeground(Theme.TEXT_SECONDARY);
        subtitle.setFont(Theme.FONT_REGULAR.deriveFont(Theme.SIZE_BASE));
        subtitle.setAlignmentX(LEFT_ALIGNMENT);

        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(Theme.SPACE_XS));
        titlePanel.add(subtitle);

        return titlePanel;
    }
}
