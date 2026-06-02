package sticker_tracker.ui.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;
import sticker_tracker.ui.Theme;

public class RoundedPanel extends JPanel {

    private final int radius;
    private Color backgroundColor;

    public RoundedPanel(int radius) {
        this.radius = radius;
        this.backgroundColor = Theme.BG_CARD;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        final var graphics2d = (Graphics2D) graphics.create();
        graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2d.setColor(backgroundColor);
        graphics2d.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        graphics2d.dispose();

        super.paintComponent(graphics);
    }

    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        repaint();
    }
}
