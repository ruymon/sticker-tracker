package sticker_tracker.ui.components;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import sticker_tracker.ui.Theme;

public class RoundedButton extends JButton {

    public enum Variant {
        PRIMARY,
        SECONDARY,
        GHOST
    }

    private final Variant variant;
    private boolean hovered;
    private boolean activeState;

    public RoundedButton(String text, Variant variant) {
        super(text);
        this.variant = variant;
        this.hovered = false;
        this.activeState = false;

        setOpaque(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFont(Theme.FONT_MEDIUM.deriveFont(Theme.SIZE_SM));
        setForeground(Theme.TEXT_PRIMARY);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                hovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                hovered = false;
                repaint();
            }
        });
    }

    public void setActive(boolean activeState) {
        this.activeState = activeState;
        setForeground(activeState ? Theme.ACCENT : Theme.TEXT_PRIMARY);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        final var graphics2d = (Graphics2D) graphics.create();
        graphics2d.setRenderingHint(
            java.awt.RenderingHints.KEY_ANTIALIASING,
            java.awt.RenderingHints.VALUE_ANTIALIAS_ON
        );
        graphics2d.setColor(resolveBackground());
        graphics2d.fillRoundRect(0, 0, getWidth(), getHeight(), Theme.RADIUS_MD, Theme.RADIUS_MD);
        graphics2d.dispose();

        super.paintComponent(graphics);
    }

    private Color resolveBackground() {
        if (activeState) {
            return switch (variant) {
                case PRIMARY -> Theme.ACCENT;
                case SECONDARY, GHOST -> Theme.ACCENT_MUTED;
            };
        }

        return switch (variant) {
            case PRIMARY -> hovered ? Theme.ACCENT_HOVER : Theme.ACCENT;
            case SECONDARY -> hovered ? Theme.BG_HOVER : Theme.BG_CARD;
            case GHOST -> hovered ? Theme.BG_HOVER : Theme.TRANSPARENT;
        };
    }
}
