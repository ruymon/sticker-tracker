package sticker_tracker.ui.screens.home.sections;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.JPanel;
import sticker_tracker.ui.Theme;
import sticker_tracker.ui.components.StatCard;
import sticker_tracker.ui.screens.home.HomeData;

public final class HomeStatsSection extends JPanel {

    private static final int STAT_COLUMN_COUNT = 4;
    private static final Locale BRAZIL_LOCALE = Locale.forLanguageTag("pt-BR");

    public HomeStatsSection(HomeData homeData) {
        setOpaque(false);
        setAlignmentX(LEFT_ALIGNMENT);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.DASHBOARD_STATS_ROW_HEIGHT));
        setLayout(new GridLayout(1, STAT_COLUMN_COUNT, Theme.SPACE_MD, Theme.SPACE_NONE));

        final var metrics = homeData.metrics();

        add(new StatCard(
            "Coletadas",
            String.valueOf(metrics.collectedCount()),
            metrics.repeatedCount() + " repetidas",
            "OK",
            Theme.ACCENT,
            Theme.BG_CARD
        ));
        add(new StatCard(
            "Faltando",
            String.valueOf(metrics.missingCount()),
            "~ " + formatEnvelopeCount(metrics.estimatedMissingEnvelopeCount()),
            "x"
        ));
        add(new StatCard(
            "Gasto",
            formatCurrency(metrics.estimatedSpentInCents()),
            formatEnvelopeCount(metrics.estimatedBoughtEnvelopeCount()),
            "R$"
        ));
        add(new StatCard(
            "R$ por nova",
            formatCurrency(metrics.estimatedCostPerNewStickerInCents()),
            metrics.collectedCount() + " novas",
            "up"
        ));
    }

    private String formatCurrency(int cents) {
        final var currencyFormat = NumberFormat.getCurrencyInstance(BRAZIL_LOCALE);
        return currencyFormat.format(cents / 100.0);
    }

    private String formatEnvelopeCount(int envelopeCount) {
        if (envelopeCount == 1) {
            return "1 envelope";
        }

        return envelopeCount + " envelopes";
    }
}
