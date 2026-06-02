package sticker_tracker.ui.components;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import sticker_tracker.ui.Theme;

public final class FlagBadge extends JPanel {

    private static final String FLAG_RESOURCE_PATH = "/assets/flags/";
    private static final int DEFAULT_SIZE = 32;
    private static final int FALLBACK_MAX_LENGTH = 3;

    private final int size;
    private final String fallbackText;
    private final Image flagImage;

    public FlagBadge(String flagAsset, String fallbackText) {
        this(flagAsset, fallbackText, DEFAULT_SIZE);
    }

    public FlagBadge(String flagAsset, String fallbackText, int size) {
        this.size = size;
        this.fallbackText = normalizeFallbackText(fallbackText);
        this.flagImage = loadFlagImage(flagAsset);

        final var badgeSize = new Dimension(size, size);
        setPreferredSize(badgeSize);
        setMinimumSize(badgeSize);
        setMaximumSize(badgeSize);
        setOpaque(false);
    }

    private Image loadFlagImage(String flagAsset) {
        if (flagAsset == null || flagAsset.isBlank()) {
            return null;
        }

        final var flagResource = getClass().getResource(FLAG_RESOURCE_PATH + flagAsset);
        if (flagResource == null) {
            return null;
        }

        return new ImageIcon(flagResource).getImage();
    }

    private String normalizeFallbackText(String fallbackText) {
        if (fallbackText == null || fallbackText.isBlank()) {
            return "?";
        }

        final var normalizedText = fallbackText.trim().toUpperCase();
        if (normalizedText.length() <= FALLBACK_MAX_LENGTH) {
            return normalizedText;
        }

        return normalizedText.substring(0, FALLBACK_MAX_LENGTH);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        final var graphics2d = (Graphics2D) graphics.create();
        graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        if (flagImage == null) {
            paintFallbackBadge(graphics2d);
        } else {
            paintFlagImage(graphics2d);
        }

        graphics2d.dispose();
    }

    private void paintFallbackBadge(Graphics2D graphics2d) {
        graphics2d.setColor(Theme.ACCENT_MUTED);
        graphics2d.fillOval(0, 0, size - 1, size - 1);

        graphics2d.setColor(Theme.ACCENT_HOVER);
        graphics2d.setFont(Theme.FONT_BOLD.deriveFont(Theme.SIZE_XS));
        final var fontMetrics = graphics2d.getFontMetrics();
        final var textWidth = fontMetrics.stringWidth(fallbackText);
        final var textX = (size - textWidth) / 2;
        final var textY = ((size - fontMetrics.getHeight()) / 2) + fontMetrics.getAscent();

        graphics2d.drawString(fallbackText, textX, textY);
    }

    private void paintFlagImage(Graphics2D graphics2d) {
        final Shape previousClip = graphics2d.getClip();
        graphics2d.setClip(new Ellipse2D.Double(0, 0, size, size));
        graphics2d.drawImage(flagImage, 0, 0, size, size, this);
        graphics2d.setClip(previousClip);
    }
}
