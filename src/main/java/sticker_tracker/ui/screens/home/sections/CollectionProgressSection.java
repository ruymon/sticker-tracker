package sticker_tracker.ui.screens.home.sections;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.text.DecimalFormat;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import sticker_tracker.domain.Progress;
import sticker_tracker.ui.Theme;
import sticker_tracker.ui.components.Badge;
import sticker_tracker.ui.components.ProgressBarCustom;
import sticker_tracker.ui.components.RoundedPanel;
import sticker_tracker.ui.screens.home.HomeData;

public final class CollectionProgressSection extends RoundedPanel {

    private static final int STICKERS_PER_PACK = 7;

    private final DecimalFormat percentageFormat;

    public CollectionProgressSection(HomeData homeData) {
        super(Theme.RADIUS_LG);
        this.percentageFormat = new DecimalFormat("0.0");

        setAlignmentX(LEFT_ALIGNMENT);
        setPreferredSize(new Dimension(Theme.SPACE_NONE, Theme.DASHBOARD_PROGRESS_CARD_HEIGHT));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.DASHBOARD_PROGRESS_CARD_HEIGHT));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(
            Theme.CARD_PADDING,
            Theme.CARD_PADDING,
            Theme.CARD_PADDING,
            Theme.CARD_PADDING
        ));

        add(buildTitleRow(homeData.progress()));
        add(Box.createVerticalStrut(Theme.SPACE_MD));
        add(buildMainMetric(homeData.progress()));
        add(Box.createVerticalGlue());
        add(Box.createVerticalStrut(Theme.SPACE_MD));
        add(buildProgressBar(homeData.progress()));
        add(Box.createVerticalStrut(Theme.SPACE_SM));
        add(buildFooterRow(homeData.progress()));
    }

    private JPanel buildTitleRow(Progress progress) {
        final var titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);

        final var title = new JLabel("PROGRESSO DO ÁLBUM");
        title.setForeground(Theme.TEXT_SECONDARY);
        title.setFont(Theme.FONT_SEMIBOLD.deriveFont(Theme.SIZE_XS));

        final var percentage = new Badge(
            percentageFormat.format(progress.percentage()) + "%",
            Theme.ACCENT_MUTED,
            Theme.ACCENT_HOVER
        );

        titleRow.add(title, BorderLayout.WEST);
        titleRow.add(percentage, BorderLayout.EAST);

        return titleRow;
    }

    private JPanel buildMainMetric(Progress progress) {
        final var metric = new JPanel(new FlowLayout(FlowLayout.LEFT, Theme.SPACE_NONE, Theme.SPACE_NONE));
        metric.setOpaque(false);

        final var collected = new JLabel(String.valueOf(progress.collected()));
        collected.setForeground(Theme.TEXT_PRIMARY);
        collected.setFont(Theme.FONT_BOLD.deriveFont(Theme.SIZE_DISPLAY));

        final var total = new JLabel(" / " + progress.total());
        total.setForeground(Theme.TEXT_SECONDARY);
        total.setFont(Theme.FONT_SEMIBOLD.deriveFont(Theme.SIZE_LG));

        metric.add(collected);
        metric.add(total);

        return metric;
    }

    private ProgressBarCustom buildProgressBar(Progress progress) {
        final var progressBar = new ProgressBarCustom();
        progressBar.updateProgress(progress);

        return progressBar;
    }

    private JPanel buildFooterRow(Progress progress) {
        final var footerRow = new JPanel(new BorderLayout());
        footerRow.setOpaque(false);

        final var missingLabel = buildFooterLabel(progress.missing() + " figurinhas faltando", SwingConstants.LEFT);
        final var packEstimate = (int) Math.ceil((double) progress.missing() / STICKERS_PER_PACK);
        final var packLabel = buildFooterLabel("~ " + packEstimate + " pacotes", SwingConstants.RIGHT);

        footerRow.add(missingLabel, BorderLayout.WEST);
        footerRow.add(packLabel, BorderLayout.EAST);

        return footerRow;
    }

    private JLabel buildFooterLabel(String text, int horizontalAlignment) {
        final var label = new JLabel(text, horizontalAlignment);
        label.setForeground(Theme.TEXT_SECONDARY);
        label.setFont(Theme.FONT_REGULAR.deriveFont(Theme.SIZE_SM));

        return label;
    }
}
