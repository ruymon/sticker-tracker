package sticker_tracker.ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import sticker_tracker.ui.Theme;

public final class StatCard extends RoundedPanel {

    public StatCard(String labelText, String valueText, String descriptionText, String markerText) {
        this(labelText, valueText, descriptionText, markerText, Theme.BG_SECONDARY, Theme.TEXT_SECONDARY);
    }

    public StatCard(
        String labelText,
        String valueText,
        String descriptionText,
        String markerText,
        Color markerBackgroundColor,
        Color markerTextColor
    ) {
        super(Theme.RADIUS_LG);
        setAlignmentX(LEFT_ALIGNMENT);
        setLayout(new BorderLayout(Theme.SPACE_MD, Theme.SPACE_SM));
        setBorder(BorderFactory.createEmptyBorder(
            Theme.SPACE_MD,
            Theme.SPACE_MD,
            Theme.SPACE_MD,
            Theme.SPACE_MD
        ));

        add(buildTopRow(labelText, markerText, markerBackgroundColor, markerTextColor), BorderLayout.NORTH);
        add(buildValue(valueText), BorderLayout.CENTER);
        add(buildDescription(descriptionText), BorderLayout.SOUTH);
    }

    private JPanel buildTopRow(
        String labelText,
        String markerText,
        Color markerBackgroundColor,
        Color markerTextColor
    ) {
        final var topRow = new JPanel(new BorderLayout(Theme.SPACE_SM, Theme.SPACE_NONE));
        topRow.setOpaque(false);

        final var label = new JLabel(labelText);
        label.setForeground(Theme.TEXT_SECONDARY);
        label.setFont(Theme.FONT_REGULAR.deriveFont(Theme.SIZE_SM));

        final var marker = new Badge(markerText, markerBackgroundColor, markerTextColor);

        topRow.add(label, BorderLayout.WEST);
        topRow.add(marker, BorderLayout.EAST);

        return topRow;
    }

    private JLabel buildValue(String valueText) {
        final var value = new JLabel(valueText);
        value.setForeground(Theme.TEXT_PRIMARY);
        value.setFont(Theme.FONT_BOLD.deriveFont(Theme.SIZE_XL));

        return value;
    }

    private JLabel buildDescription(String descriptionText) {
        final var description = new JLabel(descriptionText);
        description.setForeground(Theme.TEXT_SECONDARY);
        description.setFont(Theme.FONT_REGULAR.deriveFont(Theme.SIZE_XS));

        return description;
    }
}
