package sticker_tracker.ui.screens.album;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntConsumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import sticker_tracker.domain.Sticker;
import sticker_tracker.domain.UserSticker;
import sticker_tracker.ui.Theme;
import sticker_tracker.ui.components.RoundedButton;
import sticker_tracker.ui.components.RoundedPanel;

public final class StickerQuantityDialog {

    private static final int DIALOG_WIDTH = 320;
    private static final int DIALOG_HEIGHT = 270;
    private static final int MINIMUM_QUANTITY = 0;
    private static final int MAXIMUM_QUANTITY = 99;
    private static final int QUANTITY_STEP = 1;
    private static final int COUNTER_BUTTON_WIDTH = 44;
    private static final int COUNTER_CONTROL_HEIGHT = 44;
    private static final int COUNTER_VALUE_WIDTH = 72;

    private StickerQuantityDialog() {}

    public static void show(
        Component parent,
        Sticker sticker,
        Optional<UserSticker> userSticker,
        IntConsumer onSave
    ) {
        final var owner = SwingUtilities.getWindowAncestor(parent);
        final var dialog = new JDialog(owner, Dialog.ModalityType.APPLICATION_MODAL);
        final int currentQuantity = userSticker.map(UserSticker::getQuantity).orElse(MINIMUM_QUANTITY);
        final var quantityControl = new QuantityControl(currentQuantity);

        dialog.setTitle(sticker.getCode() + " - " + sticker.getName());
        dialog.setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        dialog.setLocationRelativeTo(parent);
        dialog.setContentPane(buildContent(dialog, sticker, quantityControl, onSave));
        dialog.setVisible(true);
    }

    private static JPanel buildContent(
        JDialog dialog,
        Sticker sticker,
        QuantityControl quantityControl,
        IntConsumer onSave
    ) {
        final var contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Theme.BG_PRIMARY);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(
            Theme.SPACE_MD,
            Theme.SPACE_LG,
            Theme.SPACE_MD,
            Theme.SPACE_LG
        ));

        contentPanel.add(buildInfoPanel(sticker, quantityControl), BorderLayout.CENTER);
        contentPanel.add(buildActionsPanel(dialog, quantityControl, onSave), BorderLayout.SOUTH);

        return contentPanel;
    }

    private static JPanel buildInfoPanel(Sticker sticker, QuantityControl quantityControl) {
        final var infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        final var codeLabel = new JLabel(sticker.getCode());
        codeLabel.setForeground(Theme.ACCENT);
        codeLabel.setFont(Theme.FONT_MONO.deriveFont(Theme.SIZE_MD));

        final var nameLabel = new JLabel(sticker.getName());
        nameLabel.setForeground(Theme.TEXT_PRIMARY);
        nameLabel.setFont(Theme.FONT_SEMIBOLD.deriveFont(Theme.SIZE_BASE));

        final var quantityLabel = new JLabel("Quantidade");
        quantityLabel.setForeground(Theme.TEXT_SECONDARY);
        quantityLabel.setFont(Theme.FONT_REGULAR.deriveFont(Theme.SIZE_SM));
        quantityLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        quantityControl.panel().setAlignmentX(Component.LEFT_ALIGNMENT);

        infoPanel.add(codeLabel);
        infoPanel.add(Box.createVerticalStrut(Theme.SPACE_XS));
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(Theme.SPACE_LG));
        infoPanel.add(quantityLabel);
        infoPanel.add(Box.createVerticalStrut(Theme.SPACE_XS));
        infoPanel.add(quantityControl.panel());

        return infoPanel;
    }

    private static JPanel buildActionsPanel(
        JDialog dialog,
        QuantityControl quantityControl,
        IntConsumer onSave
    ) {
        final var actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, Theme.SPACE_SM, Theme.SPACE_NONE));
        actionsPanel.setOpaque(false);
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(
            Theme.SPACE_MD,
            Theme.SPACE_NONE,
            Theme.SPACE_NONE,
            Theme.SPACE_NONE
        ));

        final var cancelButton = new RoundedButton("Cancelar", RoundedButton.Variant.GHOST);
        cancelButton.addActionListener(actionEvent -> dialog.dispose());

        final var saveButton = new RoundedButton("Salvar", RoundedButton.Variant.PRIMARY);
        saveButton.addActionListener(actionEvent -> {
            onSave.accept(quantityControl.quantity());
            dialog.dispose();
        });

        actionsPanel.add(cancelButton);
        actionsPanel.add(saveButton);

        return actionsPanel;
    }

    private static final class QuantityControl {

        private final AtomicInteger quantity;
        private final JPanel panel;
        private final JLabel quantityLabel;
        private final RoundedButton decreaseButton;
        private final RoundedButton increaseButton;

        private QuantityControl(int initialQuantity) {
            this.quantity = new AtomicInteger(initialQuantity);
            this.panel = new JPanel(new FlowLayout(FlowLayout.LEFT, Theme.SPACE_SM, Theme.SPACE_NONE));
            this.quantityLabel = new JLabel();
            this.decreaseButton = new RoundedButton("-", RoundedButton.Variant.SECONDARY);
            this.increaseButton = new RoundedButton("+", RoundedButton.Variant.SECONDARY);

            build();
            refresh();
        }

        private JPanel panel() {
            return panel;
        }

        private int quantity() {
            return quantity.get();
        }

        private void build() {
            panel.setOpaque(false);

            decreaseButton.setPreferredSize(new Dimension(COUNTER_BUTTON_WIDTH, COUNTER_CONTROL_HEIGHT));
            decreaseButton.addActionListener(actionEvent -> decreaseQuantity());

            increaseButton.setPreferredSize(new Dimension(COUNTER_BUTTON_WIDTH, COUNTER_CONTROL_HEIGHT));
            increaseButton.addActionListener(actionEvent -> increaseQuantity());

            final var valuePanel = new RoundedPanel(Theme.RADIUS_MD);
            valuePanel.setBackgroundColor(Theme.BG_SECONDARY);
            valuePanel.setPreferredSize(new Dimension(COUNTER_VALUE_WIDTH, COUNTER_CONTROL_HEIGHT));
            valuePanel.setLayout(new BorderLayout());
            valuePanel.setBorder(BorderFactory.createEmptyBorder(
                Theme.SPACE_NONE,
                Theme.SPACE_MD,
                Theme.SPACE_NONE,
                Theme.SPACE_MD
            ));

            quantityLabel.setHorizontalAlignment(SwingConstants.CENTER);
            quantityLabel.setForeground(Theme.TEXT_PRIMARY);
            quantityLabel.setFont(Theme.FONT_MONO.deriveFont(Theme.SIZE_BASE));
            valuePanel.add(quantityLabel, BorderLayout.CENTER);

            panel.add(decreaseButton);
            panel.add(valuePanel);
            panel.add(increaseButton);
        }

        private void decreaseQuantity() {
            if (quantity.get() <= MINIMUM_QUANTITY) {
                return;
            }

            quantity.addAndGet(-QUANTITY_STEP);
            refresh();
        }

        private void increaseQuantity() {
            if (quantity.get() >= MAXIMUM_QUANTITY) {
                return;
            }

            quantity.addAndGet(QUANTITY_STEP);
            refresh();
        }

        private void refresh() {
            quantityLabel.setText(String.valueOf(quantity.get()));
            decreaseButton.setEnabled(quantity.get() > MINIMUM_QUANTITY);
            increaseButton.setEnabled(quantity.get() < MAXIMUM_QUANTITY);
        }
    }
}
