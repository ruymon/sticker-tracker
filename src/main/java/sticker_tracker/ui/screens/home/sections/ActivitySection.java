package sticker_tracker.ui.screens.home.sections;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import sticker_tracker.domain.Section;
import sticker_tracker.domain.Sticker;
import sticker_tracker.domain.UserSticker;
import sticker_tracker.ui.Theme;
import sticker_tracker.ui.components.Badge;
import sticker_tracker.ui.components.EmptyState;
import sticker_tracker.ui.components.FlagBadge;
import sticker_tracker.ui.components.RoundedPanel;
import sticker_tracker.ui.screens.home.HomeData;

public final class ActivitySection extends RoundedPanel {

    private static final int ACTIVITY_FLAG_SIZE = 32;
    private static final int MINUTES_PER_HOUR = 60;
    private static final int HOURS_PER_DAY = 24;

    public ActivitySection(HomeData homeData) {
        super(Theme.RADIUS_LG);
        setAlignmentX(LEFT_ALIGNMENT);
        setLayout(new BorderLayout(Theme.SPACE_NONE, Theme.SPACE_LG));
        setBorder(BorderFactory.createEmptyBorder(
            Theme.CARD_PADDING,
            Theme.CARD_PADDING,
            Theme.CARD_PADDING,
            Theme.CARD_PADDING
        ));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildContent(homeData), BorderLayout.CENTER);
    }

    private JLabel buildHeader() {
        final var title = new JLabel("Atividade");
        title.setForeground(Theme.TEXT_PRIMARY);
        title.setFont(Theme.FONT_SEMIBOLD.deriveFont(Theme.SIZE_BASE));

        return title;
    }

    private Component buildContent(HomeData homeData) {
        final var sectionsById = buildSectionsById(homeData);

        for (final var userSticker : homeData.recentUserStickers()) {
            final var sticker = homeData.recentStickers().get(userSticker.getStickerId());

            if (sticker != null) {
                return buildActivityRow(userSticker, sticker, sectionsById.get(sticker.getSectionId()));
            }
        }

        return new EmptyState("Nenhuma atividade.", "As últimas figurinhas aparecem aqui.");
    }

    private Map<String, Section> buildSectionsById(HomeData homeData) {
        final var sectionsById = new HashMap<String, Section>();

        for (final var section : homeData.sections()) {
            sectionsById.put(section.getId(), section);
        }

        return sectionsById;
    }

    private JPanel buildActivityRow(UserSticker userSticker, Sticker sticker, Section section) {
        final var activityRow = new JPanel(new BorderLayout(Theme.SPACE_MD, Theme.SPACE_NONE));
        activityRow.setOpaque(false);

        final var flagBadge = new FlagBadge(
            section == null ? null : section.getFlagAsset(),
            section == null ? sticker.getCode() : section.getPrefix(),
            ACTIVITY_FLAG_SIZE
        );

        activityRow.add(flagBadge, BorderLayout.WEST);
        activityRow.add(buildActivityText(userSticker, sticker, section), BorderLayout.CENTER);
        activityRow.add(buildStatusBadge(), BorderLayout.EAST);

        return activityRow;
    }

    private JPanel buildStatusBadge() {
        final var badgePanel = new JPanel(new FlowLayout(
            FlowLayout.RIGHT,
            Theme.SPACE_NONE,
            Theme.SPACE_NONE
        ));
        badgePanel.setOpaque(false);
        badgePanel.add(new Badge("Novo", Theme.ACCENT_MUTED, Theme.ACCENT_HOVER));

        return badgePanel;
    }

    private JPanel buildActivityText(UserSticker userSticker, Sticker sticker, Section section) {
        final var textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        final var title = new JLabel(buildActivityTitle(sticker, section));
        title.setForeground(Theme.TEXT_PRIMARY);
        title.setFont(Theme.FONT_SEMIBOLD.deriveFont(Theme.SIZE_SM));

        final var time = new JLabel(formatElapsedTime(userSticker.getUpdatedAt()));
        time.setForeground(Theme.TEXT_SECONDARY);
        time.setFont(Theme.FONT_REGULAR.deriveFont(Theme.SIZE_XS));

        textPanel.add(title);
        textPanel.add(Box.createVerticalStrut(Theme.SPACE_XS));
        textPanel.add(time);

        return textPanel;
    }

    private String buildActivityTitle(Sticker sticker, Section section) {
        if (section == null) {
            return sticker.getName() + " · " + sticker.getCode();
        }

        if (sticker.getNumber() == null) {
            return section.getName() + " · " + sticker.getCode();
        }

        return section.getName() + " · " + section.getPrefix() + " - " + sticker.getNumber();
    }

    private String formatElapsedTime(LocalDateTime updatedAt) {
        final var elapsed = Duration.between(updatedAt, LocalDateTime.now());

        if (elapsed.isNegative() || elapsed.toMinutes() < 1) {
            return "agora";
        }

        if (elapsed.toMinutes() < MINUTES_PER_HOUR) {
            return elapsed.toMinutes() + "min atrás";
        }

        if (elapsed.toHours() < HOURS_PER_DAY) {
            return elapsed.toHours() + "h atrás";
        }

        return elapsed.toDays() + "d atrás";
    }
}
