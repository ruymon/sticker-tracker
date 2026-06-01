package sticker_tracker;

import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import sticker_tracker.ui.AppFrame;
import sticker_tracker.ui.FontLoader;
import sticker_tracker.ui.Theme;

public final class Main {

    private Main() {}

    public static void main(String[] args) {
        FontLoader.load();
        FlatDarkLaf.setup();
        configureFlatLaf();

        SwingUtilities.invokeLater(() -> {
            final var frame = new AppFrame();
            frame.setVisible(true);
        });
    }

    private static void configureFlatLaf() {
        UIManager.put("defaultFont", Theme.FONT_REGULAR.deriveFont(Theme.SIZE_BASE));
        UIManager.put("Panel.background", Theme.BG_PRIMARY);
        UIManager.put("Button.arc", Theme.RADIUS_MD);
        UIManager.put("Component.arc", Theme.RADIUS_MD);
        UIManager.put("ScrollBar.thumbArc", Theme.RADIUS_SM);
        UIManager.put("ScrollBar.width", 6);
    }
}
