package sticker_tracker.ui.screens.home.sections;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import sticker_tracker.domain.Sticker;
import sticker_tracker.domain.UserSticker;
import sticker_tracker.ui.Theme;
import sticker_tracker.ui.components.EmptyState;
import sticker_tracker.ui.components.RoundedButton;
import sticker_tracker.ui.screens.home.HomeData;

public final class RepeatedStickersSection extends JPanel {

    private RoundedButton copyButton;

    public RepeatedStickersSection(HomeData homeData) {
        setOpaque(false);
        setAlignmentX(LEFT_ALIGNMENT);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(buildTitleLabel(homeData));
        add(Box.createVerticalStrut(Theme.SPACE_SM));

        if (homeData.repeatedUserStickers().isEmpty()) {
            add(new EmptyState("Nenhuma repetida.", "Quando tiver repetidas, elas aparecem aqui."));
            return;
        }

        add(buildRepeatedList(homeData));
        add(Box.createVerticalStrut(Theme.SPACE_MD));
        add(buildCopyButton(homeData));
    }

    private JLabel buildTitleLabel(HomeData homeData) {
        final var repeatedStickerCount = homeData.repeatedUserStickers().size();
        final var availableStickerCount = homeData.repeatedUserStickers().stream()
            .mapToInt(UserSticker::repeatedCount)
            .sum();

        final var title = new JLabel(
            "Repetidas ("
                + repeatedStickerCount
                + " figurinhas · "
                + availableStickerCount
                + " disponíveis)"
        );
        title.setForeground(Theme.TEXT_PRIMARY);
        title.setFont(Theme.FONT_SEMIBOLD.deriveFont(Theme.SIZE_BASE));

        return title;
    }

    private JScrollPane buildRepeatedList(HomeData homeData) {
        final var repeatedList = new JPanel();
        repeatedList.setOpaque(false);
        repeatedList.setLayout(new BoxLayout(repeatedList, BoxLayout.Y_AXIS));

        for (final var userSticker : homeData.repeatedUserStickers()) {
            final var sticker = homeData.repeatedStickers().get(userSticker.getStickerId());

            if (sticker != null) {
                repeatedList.add(buildRepeatedRow(sticker, userSticker));
            }
        }

        final var listHeight = Theme.REPEATED_LIST_ROW_HEIGHT * Theme.REPEATED_LIST_VISIBLE_ROWS;
        final var scrollPane = new JScrollPane(repeatedList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(Theme.SPACE_NONE, listHeight));
        scrollPane.getVerticalScrollBar().setUnitIncrement(Theme.SCROLL_UNIT_INCREMENT);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);

        return scrollPane;
    }

    private JPanel buildRepeatedRow(Sticker sticker, UserSticker userSticker) {
        final var repeatedRow = new JPanel(new GridBagLayout());
        repeatedRow.setOpaque(false);
        repeatedRow.setBorder(BorderFactory.createEmptyBorder(
            Theme.SPACE_SM,
            Theme.SPACE_SM,
            Theme.SPACE_SM,
            Theme.SPACE_SM
        ));

        final var constraints = new GridBagConstraints();
        constraints.insets = new Insets(Theme.SPACE_NONE, Theme.SPACE_SM, Theme.SPACE_NONE, Theme.SPACE_SM);
        constraints.gridy = 0;
        constraints.weighty = 0;

        constraints.gridx = 0;
        constraints.weightx = 0;
        constraints.anchor = GridBagConstraints.WEST;
        final var codeLabel = new JLabel(sticker.getCode());
        codeLabel.setForeground(Theme.TEXT_MUTED);
        codeLabel.setFont(Theme.FONT_MONO.deriveFont(Theme.SIZE_SM));
        repeatedRow.add(codeLabel, constraints);

        constraints.gridx = 1;
        constraints.weightx = 1.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        final var nameLabel = new JLabel(sticker.getName());
        nameLabel.setForeground(Theme.TEXT_PRIMARY);
        nameLabel.setFont(Theme.FONT_REGULAR.deriveFont(Theme.SIZE_SM));
        repeatedRow.add(nameLabel, constraints);

        constraints.gridx = 2;
        constraints.weightx = 0;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.EAST;
        final var quantityLabel = new JLabel("x" + userSticker.getQuantity());
        quantityLabel.setForeground(Theme.ACCENT);
        quantityLabel.setFont(Theme.FONT_MONO.deriveFont(Theme.SIZE_SM));
        repeatedRow.add(quantityLabel, constraints);

        return repeatedRow;
    }

    private RoundedButton buildCopyButton(HomeData homeData) {
        copyButton = new RoundedButton("Copiar lista para WhatsApp", RoundedButton.Variant.PRIMARY);
        copyButton.addActionListener(actionEvent -> copyWhatsAppMessage(homeData));

        return copyButton;
    }

    private void copyWhatsAppMessage(HomeData homeData) {
        final var repeatedCodes = homeData.repeatedUserStickers().stream()
            .map(userSticker -> Optional.ofNullable(homeData.repeatedStickers().get(userSticker.getStickerId())))
            .flatMap(Optional::stream)
            .map(Sticker::getCode)
            .collect(Collectors.joining(", "));
        final var message = "Tenho essas repetidas da Copa 2026: " + repeatedCodes
            + "\nMe chama para trocar!";
        final var clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        clipboard.setContents(new StringSelection(message), null);
        showCopyFeedback();
    }

    private void showCopyFeedback() {
        copyButton.setText("Copiado!");

        final var feedbackTimer = new Timer(
            Theme.COPY_FEEDBACK_DURATION_MS,
            actionEvent -> copyButton.setText("Copiar lista para WhatsApp")
        );
        feedbackTimer.setRepeats(false);
        feedbackTimer.start();
    }
}
