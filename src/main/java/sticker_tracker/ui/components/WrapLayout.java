package sticker_tracker.ui.components;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import javax.swing.JViewport;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import sticker_tracker.ui.Theme;

// Based on Rob Camick's public-domain WrapLayout.
// Source: https://tips4java.wordpress.com/2008/11/06/wrap-layout/
public class WrapLayout extends FlowLayout {

    public WrapLayout() {
        super();
    }

    public WrapLayout(int align) {
        super(align);
    }

    public WrapLayout(int align, int horizontalGap, int verticalGap) {
        super(align, horizontalGap, verticalGap);
    }

    @Override
    public Dimension preferredLayoutSize(Container target) {
        return layoutSize(target, true);
    }

    @Override
    public Dimension minimumLayoutSize(Container target) {
        final var minimum = layoutSize(target, false);
        minimum.width -= getHgap() + Theme.WRAP_LAYOUT_SCROLL_ADJUSTMENT;

        return minimum;
    }

    private Dimension layoutSize(Container target, boolean preferred) {
        synchronized (target.getTreeLock()) {
            int targetWidth = findAvailableWidth(target);

            if (targetWidth == 0) {
                targetWidth = Integer.MAX_VALUE;
            }

            final int horizontalGap = getHgap();
            final int verticalGap = getVgap();
            final Insets insets = target.getInsets();
            final int horizontalInsetsAndGap = insets.left
                + insets.right
                + (horizontalGap * Theme.WRAP_LAYOUT_DOUBLE_GAP_MULTIPLIER);
            final int maxWidth = targetWidth - horizontalInsetsAndGap;
            final var dimension = new Dimension(0, 0);

            int rowWidth = 0;
            int rowHeight = 0;

            for (int index = 0; index < target.getComponentCount(); index++) {
                final Component component = target.getComponent(index);

                if (!component.isVisible()) {
                    continue;
                }

                final Dimension componentSize = preferred
                    ? component.getPreferredSize()
                    : component.getMinimumSize();

                if (rowWidth + componentSize.width > maxWidth) {
                    addRow(dimension, rowWidth, rowHeight);
                    rowWidth = 0;
                    rowHeight = 0;
                }

                if (rowWidth != 0) {
                    rowWidth += horizontalGap;
                }

                rowWidth += componentSize.width;
                rowHeight = Math.max(rowHeight, componentSize.height);
            }

            addRow(dimension, rowWidth, rowHeight);

            dimension.width += horizontalInsetsAndGap;
            dimension.height += insets.top
                + insets.bottom
                + (verticalGap * Theme.WRAP_LAYOUT_DOUBLE_GAP_MULTIPLIER);

            final var scrollPane = SwingUtilities.getAncestorOfClass(JScrollPane.class, target);
            if (scrollPane != null && target.isValid()) {
                dimension.width -= horizontalGap + Theme.WRAP_LAYOUT_SCROLL_ADJUSTMENT;
            }

            return dimension;
        }
    }

    private void addRow(Dimension dimension, int rowWidth, int rowHeight) {
        dimension.width = Math.max(dimension.width, rowWidth);

        if (dimension.height > 0) {
            dimension.height += getVgap();
        }

        dimension.height += rowHeight;
    }

    private int findAvailableWidth(Container target) {
        if (target.getSize().width > 0) {
            return target.getSize().width;
        }

        final var viewport = SwingUtilities.getAncestorOfClass(JViewport.class, target);
        if (viewport != null && viewport.getWidth() > 0) {
            return viewport.getWidth();
        }

        var parent = target.getParent();
        while (parent != null) {
            if (parent.getWidth() > 0) {
                return parent.getWidth();
            }

            parent = parent.getParent();
        }

        return 0;
    }
}
