package sticker_tracker.ui.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;
import sticker_tracker.ui.Theme;

public class RoundedPanel extends JPanel {

    private static final float BORDER_WIDTH = 1f;

    private final int radius;
    private Color backgroundColor;
    private Color borderColor;

    public RoundedPanel(int radius) {
        this.radius = radius;
        this.backgroundColor = Theme.BG_CARD;
        this.borderColor = Theme.BORDER;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        final var graphics2d = (Graphics2D) graphics.create();
        graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2d.setColor(backgroundColor);
        graphics2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        graphics2d.dispose();

        super.paintComponent(graphics);

        final var borderGraphics = (Graphics2D) graphics.create();
        borderGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        borderGraphics.setColor(borderColor);
        borderGraphics.setStroke(new java.awt.BasicStroke(BORDER_WIDTH));
        borderGraphics.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        borderGraphics.dispose();
    }

    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        repaint();
    }

    public void setBorderColor(Color color) {
        this.borderColor = color;
        repaint();
    }
}
