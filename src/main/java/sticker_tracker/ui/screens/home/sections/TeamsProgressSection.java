package sticker_tracker.ui.screens.home.sections;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Comparator;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import sticker_tracker.domain.Progress;
import sticker_tracker.domain.Section;
import sticker_tracker.domain.SectionType;
import sticker_tracker.ui.Theme;
import sticker_tracker.ui.components.EmptyState;
import sticker_tracker.ui.components.FlagBadge;
import sticker_tracker.ui.components.ProgressBarCustom;
import sticker_tracker.ui.components.RoundedPanel;
import sticker_tracker.ui.screens.home.HomeData;

public final class TeamsProgressSection extends RoundedPanel {

    private static final int VISIBLE_TEAM_COUNT = 16;
    private static final int TEAM_ROW_COUNT = 2;
    private static final int TEAM_COLUMN_COUNT = 8;
    private static final int TEAM_FLAG_SIZE = 32;
    private static final int TEAM_ITEM_WIDTH = 44;
    private static final int TEAM_ITEM_HEIGHT = 82;

    public TeamsProgressSection(HomeData homeData) {
        super(Theme.RADIUS_LG);
        setAlignmentX(LEFT_ALIGNMENT);
        setLayout(new BorderLayout(Theme.SPACE_NONE, Theme.SPACE_LG));
        setBorder(BorderFactory.createEmptyBorder(
            Theme.CARD_PADDING,
            Theme.CARD_PADDING,
            Theme.CARD_PADDING,
            Theme.CARD_PADDING
        ));

        final var teamSections = findTeamSections(homeData);

        add(buildHeader(teamSections.size()), BorderLayout.NORTH);

        if (teamSections.isEmpty()) {
            add(new EmptyState("Nenhum time.", "Os times aparecem aqui depois da carga inicial."), BorderLayout.CENTER);
            return;
        }

        add(buildTeamGrid(homeData, teamSections), BorderLayout.CENTER);
    }

    private List<Section> findTeamSections(HomeData homeData) {
        return homeData.sections().stream()
            .filter(section -> SectionType.TEAM.equals(section.getType()))
            .sorted(Comparator.comparingInt(Section::getDisplayOrder))
            .toList();
    }

    private JPanel buildHeader(int teamCount) {
        final var header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        final var title = new JLabel("Times");
        title.setForeground(Theme.TEXT_PRIMARY);
        title.setFont(Theme.FONT_SEMIBOLD.deriveFont(Theme.SIZE_BASE));

        final var allTeams = new JLabel("Todos " + teamCount + " ->", SwingConstants.RIGHT);
        allTeams.setForeground(Theme.ACCENT_HOVER);
        allTeams.setFont(Theme.FONT_SEMIBOLD.deriveFont(Theme.SIZE_SM));

        header.add(title, BorderLayout.WEST);
        header.add(allTeams, BorderLayout.EAST);

        return header;
    }

    private JPanel buildTeamGrid(HomeData homeData, List<Section> teamSections) {
        final var teamGrid = new JPanel(new GridLayout(
            TEAM_ROW_COUNT,
            TEAM_COLUMN_COUNT,
            Theme.SPACE_SM,
            Theme.SPACE_MD
        ));
        teamGrid.setOpaque(false);

        teamSections.stream()
            .limit(VISIBLE_TEAM_COUNT)
            .forEach(section -> teamGrid.add(new TeamProgressItem(
                section,
                homeData.progressBySection().get(section.getId())
            )));

        return teamGrid;
    }

    private static final class TeamProgressItem extends JPanel {

        TeamProgressItem(Section section, Progress progress) {
            setOpaque(false);
            setPreferredSize(new Dimension(TEAM_ITEM_WIDTH, TEAM_ITEM_HEIGHT));
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            final var flagBadge = new FlagBadge(section.getFlagAsset(), section.getPrefix(), TEAM_FLAG_SIZE);
            flagBadge.setAlignmentX(Component.CENTER_ALIGNMENT);

            final var prefix = new JLabel(section.getPrefix(), SwingConstants.CENTER);
            prefix.setAlignmentX(Component.CENTER_ALIGNMENT);
            prefix.setForeground(Theme.TEXT_PRIMARY);
            prefix.setFont(Theme.FONT_BOLD.deriveFont(Theme.SIZE_XS));

            final var progressBar = new ProgressBarCustom();
            progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
            progressBar.setPreferredSize(new Dimension(TEAM_ITEM_WIDTH, Theme.SECTION_PROGRESS_BAR_HEIGHT));
            progressBar.setMaximumSize(new Dimension(TEAM_ITEM_WIDTH, Theme.SECTION_PROGRESS_BAR_HEIGHT));
            progressBar.updateProgress(resolveProgress(progress));

            final var progressText = new JLabel(buildProgressText(progress), SwingConstants.CENTER);
            progressText.setAlignmentX(Component.CENTER_ALIGNMENT);
            progressText.setForeground(Theme.TEXT_SECONDARY);
            progressText.setFont(Theme.FONT_REGULAR.deriveFont(Theme.SIZE_XS));

            add(flagBadge);
            add(Box.createVerticalStrut(Theme.SPACE_XS));
            add(prefix);
            add(Box.createVerticalStrut(Theme.SPACE_XS));
            add(progressBar);
            add(Box.createVerticalStrut(Theme.SPACE_XS));
            add(progressText);
        }

        private static Progress resolveProgress(Progress progress) {
            if (progress == null) {
                return new Progress(0, 0);
            }

            return progress;
        }

        private static String buildProgressText(Progress progress) {
            final var resolvedProgress = resolveProgress(progress);
            return resolvedProgress.collected() + "/" + resolvedProgress.total();
        }
    }
}
