package sticker_tracker.ui.components;

import java.awt.GridBagLayout;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import sticker_tracker.ui.Theme;

public final class EmptyState extends JPanel {

    public EmptyState(String title, String description) {
        this(title, description, null, null);
    }

    public EmptyState(String title, String description, String actionText, Runnable action) {
        setOpaque(false);
        setLayout(new GridBagLayout());

        final var contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        final var titleLabel = new JLabel(title);
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        titleLabel.setForeground(Theme.TEXT_PRIMARY);
        titleLabel.setFont(Theme.FONT_SEMIBOLD.deriveFont(Theme.SIZE_LG));

        final var descriptionLabel = new JLabel(description);
        descriptionLabel.setAlignmentX(CENTER_ALIGNMENT);
        descriptionLabel.setForeground(Theme.TEXT_SECONDARY);
        descriptionLabel.setFont(Theme.FONT_REGULAR.deriveFont(Theme.SIZE_BASE));

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(Theme.SPACE_SM));
        contentPanel.add(descriptionLabel);

        if (actionText != null && action != null) {
            final var actionButton = new RoundedButton(actionText, RoundedButton.Variant.PRIMARY);
            actionButton.setAlignmentX(CENTER_ALIGNMENT);
            actionButton.addActionListener(actionEvent -> action.run());

            contentPanel.add(Box.createVerticalStrut(Theme.SPACE_MD));
            contentPanel.add(actionButton);
        }

        add(contentPanel);
    }
}
