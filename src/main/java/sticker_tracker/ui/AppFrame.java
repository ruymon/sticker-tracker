package sticker_tracker.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import sticker_tracker.constants.AppConfig;

public final class AppFrame extends JFrame {

    private static final int MIN_WIDTH = 1100;
    private static final int MIN_HEIGHT = 720;

    public AppFrame() {
        setTitle(AppConfig.APP_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        setSize(MIN_WIDTH, MIN_HEIGHT);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BG_PRIMARY);

        buildLayout();
    }

    private void buildLayout() {
        final var panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.BG_PRIMARY);
        panel.setBorder(BorderFactory.createEmptyBorder(
            Theme.SPACE_XL,
            Theme.SPACE_XL,
            Theme.SPACE_XL,
            Theme.SPACE_XL
        ));

        final var label = new JLabel(AppConfig.APP_NAME, SwingConstants.CENTER);
        label.setForeground(Theme.TEXT_PRIMARY);
        label.setFont(Theme.FONT_BOLD.deriveFont(Theme.SIZE_2XL));

        panel.add(label, BorderLayout.CENTER);
        setContentPane(panel);
    }
}
