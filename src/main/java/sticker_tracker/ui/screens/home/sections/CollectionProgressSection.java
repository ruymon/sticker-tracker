package sticker_tracker.ui.screens.home.sections;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.text.DecimalFormat;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import sticker_tracker.domain.Progress;
import sticker_tracker.domain.Section;
import sticker_tracker.ui.Theme;
import sticker_tracker.ui.components.ProgressBarCustom;
import sticker_tracker.ui.components.RoundedButton;
import sticker_tracker.ui.components.RoundedPanel;
import sticker_tracker.ui.screens.home.HomeData;

public final class CollectionProgressSection extends RoundedPanel {

    private final DecimalFormat percentageFormat;
    private final JPanel sectionProgressPanel;
    private final RoundedButton sectionToggleButton;

    private boolean sectionProgressVisible;

    public CollectionProgressSection(HomeData homeData) {
        super(Theme.RADIUS_LG);
        this.percentageFormat = new DecimalFormat("0.0");
        this.sectionProgressVisible = false;
        this.sectionProgressPanel = buildSectionProgressPanel(homeData);
        this.sectionToggleButton = new RoundedButton("Ver por seção", RoundedButton.Variant.GHOST);

        setAlignmentX(LEFT_ALIGNMENT);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(
            Theme.SPACE_LG,
            Theme.SPACE_LG,
            Theme.SPACE_LG,
            Theme.SPACE_LG
        ));

        sectionProgressPanel.setVisible(false);
        sectionToggleButton.addActionListener(actionEvent -> toggleSectionProgress());

        add(buildTitleRow(homeData.progress()));
        add(Box.createVerticalStrut(Theme.SPACE_MD));
        add(buildProgressBar(homeData.progress()));
        add(Box.createVerticalStrut(Theme.SPACE_SM));
        add(buildSummaryLabel(homeData.progress()));
        add(Box.createVerticalStrut(Theme.SPACE_SM));
        add(sectionToggleButton);
        add(sectionProgressPanel);
    }

    private JPanel buildTitleRow(Progress progress) {
        final var titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);

        final var title = new JLabel("Sua Coleção");
        title.setForeground(Theme.TEXT_PRIMARY);
        title.setFont(Theme.FONT_SEMIBOLD.deriveFont(Theme.SIZE_MD));

        final var percentage = new JLabel(percentageFormat.format(progress.percentage()) + "%");
        percentage.setForeground(Theme.ACCENT);
        percentage.setFont(Theme.FONT_SEMIBOLD.deriveFont(Theme.SIZE_BASE));
        percentage.setHorizontalAlignment(SwingConstants.RIGHT);

        titleRow.add(title, BorderLayout.WEST);
        titleRow.add(percentage, BorderLayout.EAST);

        return titleRow;
    }

    private ProgressBarCustom buildProgressBar(Progress progress) {
        final var progressBar = new ProgressBarCustom();
        progressBar.updateProgress(progress);

        return progressBar;
    }

    private JLabel buildSummaryLabel(Progress progress) {
        final var summary = new JLabel(progress.collected() + " coletadas · " + progress.missing() + " faltando");
        summary.setForeground(Theme.TEXT_SECONDARY);
        summary.setFont(Theme.FONT_REGULAR.deriveFont(Theme.SIZE_SM));

        return summary;
    }

    private JPanel buildSectionProgressPanel(HomeData homeData) {
        final var sectionPanel = new JPanel();
        sectionPanel.setOpaque(false);
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));

        for (final var section : homeData.sections()) {
            final var progress = homeData.progressBySection().get(section.getId());

            if (progress != null) {
                sectionPanel.add(Box.createVerticalStrut(Theme.SPACE_SM));
                sectionPanel.add(buildSectionProgressRow(section, progress));
            }
        }

        return sectionPanel;
    }

    private JPanel buildSectionProgressRow(Section section, Progress progress) {
        final var progressRow = new JPanel(new BorderLayout(Theme.SPACE_MD, Theme.SPACE_NONE));
        progressRow.setOpaque(false);

        final var sectionName = new JLabel(section.getName());
        sectionName.setForeground(Theme.TEXT_SECONDARY);
        sectionName.setFont(Theme.FONT_REGULAR.deriveFont(Theme.SIZE_SM));
        sectionName.setPreferredSize(new Dimension(Theme.SIDEBAR_WIDTH, Theme.SPACE_LG));

        final var progressBar = new ProgressBarCustom();
        progressBar.setPreferredSize(new Dimension(Theme.SPACE_NONE, Theme.SECTION_PROGRESS_BAR_HEIGHT));
        progressBar.updateProgress(progress);

        final var progressText = new JLabel(
            progress.collected() + "/" + progress.total()
                + "  "
                + percentageFormat.format(progress.percentage())
                + "%"
        );
        progressText.setForeground(Theme.TEXT_MUTED);
        progressText.setFont(Theme.FONT_REGULAR.deriveFont(Theme.SIZE_XS));
        progressText.setHorizontalAlignment(SwingConstants.RIGHT);
        progressText.setPreferredSize(new Dimension(Theme.SIDEBAR_WIDTH, Theme.SPACE_LG));

        progressRow.add(sectionName, BorderLayout.WEST);
        progressRow.add(progressBar, BorderLayout.CENTER);
        progressRow.add(progressText, BorderLayout.EAST);

        return progressRow;
    }

    private void toggleSectionProgress() {
        sectionProgressVisible = !sectionProgressVisible;
        sectionProgressPanel.setVisible(sectionProgressVisible);
        sectionToggleButton.setText(sectionProgressVisible ? "Ocultar seções" : "Ver por seção");
        revalidate();
        repaint();
    }
}
