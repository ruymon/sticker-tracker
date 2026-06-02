package sticker_tracker.ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import sticker_tracker.ui.Theme;

public final class Badge extends RoundedPanel {

    public Badge(String text, Color backgroundColor, Color textColor) {
        super(Theme.RADIUS_MD);
        setBackgroundColor(backgroundColor);
        setBorderColor(backgroundColor);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(
            Theme.SPACE_XS,
            Theme.SPACE_SM,
            Theme.SPACE_XS,
            Theme.SPACE_SM
        ));

        final var label = new JLabel(text, SwingConstants.CENTER);
        label.setForeground(textColor);
        label.setFont(Theme.FONT_SEMIBOLD.deriveFont(Theme.SIZE_SM));
        add(label, BorderLayout.CENTER);
    }
}
