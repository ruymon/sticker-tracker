package sticker_tracker.ui.components;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;
import sticker_tracker.domain.Progress;
import sticker_tracker.ui.Theme;

public class ProgressBarCustom extends JPanel {

    private double percentage;

    public ProgressBarCustom() {
        this.percentage = Theme.PERCENTAGE_EMPTY;
        setOpaque(false);
        setPreferredSize(new Dimension(Theme.SPACE_NONE, Theme.PROGRESS_BAR_HEIGHT));
    }

    public void updateProgress(Progress progress) {
        this.percentage = progress.percentage();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        final var graphics2d = (Graphics2D) graphics.create();
        graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        graphics2d.setColor(Theme.BG_HOVER);
        graphics2d.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());

        final int fillWidth = (int) (getWidth() * percentage / Theme.PERCENTAGE_FULL);
        if (fillWidth > Theme.SPACE_NONE) {
            graphics2d.setColor(Theme.ACCENT);
            graphics2d.fillRoundRect(0, 0, fillWidth, getHeight(), getHeight(), getHeight());
        }

        graphics2d.dispose();
    }
}
