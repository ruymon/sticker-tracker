package sticker_tracker.ui.screens.home.sections;

import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import javax.swing.JPanel;
import javax.swing.Timer;
import sticker_tracker.ui.Theme;
import sticker_tracker.ui.components.PageHeader;
import sticker_tracker.ui.components.RoundedButton;

public final class HeaderSection extends PageHeader {

    private static final String SUBTITLE = "Acompanhe um resumo";

    public HeaderSection(String whatsAppTradeMessage, Runnable onOpenAlbum) {
        super(
            "Visão Geral",
            SUBTITLE,
            buildActionPanel(whatsAppTradeMessage, onOpenAlbum)
        );
    }

    private static JPanel buildActionPanel(String whatsAppTradeMessage, Runnable onOpenAlbum) {
        final var actionPanel = new JPanel(new FlowLayout(
            FlowLayout.RIGHT,
            Theme.SPACE_SM,
            Theme.SPACE_NONE
        ));
        actionPanel.setOpaque(false);

        actionPanel.add(buildCopyButton(whatsAppTradeMessage));

        final var openAlbumButton = new RoundedButton("+ Adicionar figurinhas", RoundedButton.Variant.PRIMARY);
        openAlbumButton.addActionListener(actionEvent -> onOpenAlbum.run());
        actionPanel.add(openAlbumButton);

        return actionPanel;
    }

    private static RoundedButton buildCopyButton(String whatsAppTradeMessage) {
        final var copyButton = new RoundedButton("Copiar WhatsApp", RoundedButton.Variant.SECONDARY);
        copyButton.addActionListener(actionEvent -> {
            final var clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(new StringSelection(whatsAppTradeMessage), null);
            showCopyFeedback(copyButton);
        });

        return copyButton;
    }

    private static void showCopyFeedback(RoundedButton copyButton) {
        copyButton.setText("Copiado!");

        final var feedbackTimer = new Timer(
            Theme.COPY_FEEDBACK_DURATION_MS,
            actionEvent -> copyButton.setText("Copiar WhatsApp")
        );
        feedbackTimer.setRepeats(false);
        feedbackTimer.start();
    }
}
