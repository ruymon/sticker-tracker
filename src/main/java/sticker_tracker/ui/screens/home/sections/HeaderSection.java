package sticker_tracker.ui.screens.home.sections;

import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import sticker_tracker.config.AppConfig;
import sticker_tracker.ui.Theme;

public final class HeaderSection extends JPanel {

    public HeaderSection() {
        setOpaque(false);
        setAlignmentX(LEFT_ALIGNMENT);
        setLayout(new BorderLayout());

        final var appName = new JLabel(AppConfig.APP_NAME);
        appName.setForeground(Theme.TEXT_PRIMARY);
        appName.setFont(Theme.FONT_BOLD.deriveFont(Theme.SIZE_XL));

        final var slogan = new JLabel(AppConfig.APP_SLOGAN);
        slogan.setForeground(Theme.TEXT_MUTED);
        slogan.setFont(Theme.FONT_REGULAR.deriveFont(Theme.SIZE_SM));
        slogan.setHorizontalAlignment(SwingConstants.RIGHT);

        add(appName, BorderLayout.WEST);
        add(slogan, BorderLayout.EAST);
    }
}
