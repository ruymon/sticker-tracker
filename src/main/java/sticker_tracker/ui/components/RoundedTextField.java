package sticker_tracker.ui.components;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JTextField;
import sticker_tracker.ui.Theme;

public final class RoundedTextField extends JTextField {

    public RoundedTextField(String text, int columns) {
        super(text, columns);

        setOpaque(false);
        setBackground(Theme.BG_CARD);
        setForeground(Theme.TEXT_PRIMARY);
        setCaretColor(Theme.ACCENT);
        setFont(Theme.FONT_REGULAR.deriveFont(Theme.SIZE_BASE));
        setBorder(BorderFactory.createEmptyBorder(
            Theme.SPACE_SM,
            Theme.SPACE_MD,
            Theme.SPACE_SM,
            Theme.SPACE_MD
        ));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        final var graphics2d = (Graphics2D) graphics.create();
        graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2d.setColor(Theme.BG_CARD);
        graphics2d.fillRoundRect(0, 0, getWidth(), getHeight(), Theme.RADIUS_MD, Theme.RADIUS_MD);
        graphics2d.dispose();

        super.paintComponent(graphics);
    }

    @Override
    protected void paintBorder(Graphics graphics) {
        final var graphics2d = (Graphics2D) graphics.create();
        graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2d.setColor(Theme.BORDER);
        graphics2d.drawRoundRect(
            0,
            0,
            getWidth() - 1,
            getHeight() - 1,
            Theme.RADIUS_MD,
            Theme.RADIUS_MD
        );
        graphics2d.dispose();
    }
}
