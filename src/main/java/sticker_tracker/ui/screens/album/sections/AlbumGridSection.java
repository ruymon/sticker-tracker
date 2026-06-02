package sticker_tracker.ui.screens.album.sections;

import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import sticker_tracker.domain.Section;
import sticker_tracker.domain.Sticker;
import sticker_tracker.domain.UserSticker;
import sticker_tracker.ui.Theme;
import sticker_tracker.ui.components.StickerCard;
import sticker_tracker.ui.components.WrapLayout;
import sticker_tracker.ui.screens.album.AlbumData;

public final class AlbumGridSection extends JPanel {

    private static final int FLAG_ICON_WIDTH = 24;
    private static final int FLAG_ICON_HEIGHT = 16;

    private final AlbumData albumData;
    private final List<Sticker> visibleStickers;
    private final Map<String, UserSticker> collectedByStickerId;
    private final BiConsumer<Sticker, Optional<UserSticker>> onStickerSelected;

    public AlbumGridSection(
        AlbumData albumData,
        List<Sticker> visibleStickers,
        Map<String, UserSticker> collectedByStickerId,
        BiConsumer<Sticker, Optional<UserSticker>> onStickerSelected
    ) {
        this.albumData = albumData;
        this.visibleStickers = visibleStickers;
        this.collectedByStickerId = collectedByStickerId;
        this.onStickerSelected = onStickerSelected;

        setOpaque(false);
        setAlignmentX(LEFT_ALIGNMENT);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        buildSections();
    }

    private void buildSections() {
        for (final var section : albumData.sections()) {
            final var visibleSectionStickers = stickersForSection(visibleStickers, section);

            if (visibleSectionStickers.isEmpty()) {
                continue;
            }

            final var allSectionStickers = stickersForSection(albumData.stickers(), section);

            add(buildSectionHeader(section, allSectionStickers));
            add(Box.createVerticalStrut(Theme.SPACE_SM));
            add(buildSectionGrid(visibleSectionStickers));
            add(Box.createVerticalStrut(Theme.SPACE_LG));
        }
    }

    private List<Sticker> stickersForSection(List<Sticker> stickers, Section section) {
        return stickers.stream()
            .filter(sticker -> sticker.getSectionId().equals(section.getId()))
            .toList();
    }

    private JPanel buildSectionHeader(Section section, List<Sticker> sectionStickers) {
        final var sectionHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, Theme.SPACE_SM, Theme.SPACE_NONE));
        sectionHeader.setOpaque(false);
        sectionHeader.setAlignmentX(LEFT_ALIGNMENT);

        final var flag = new JLabel();
        configureFlag(flag, section);

        final var collectedCount = sectionStickers.stream()
            .filter(sticker -> collectedByStickerId.containsKey(sticker.getId()))
            .count();
        final var title = new JLabel(section.getName() + " (" + collectedCount + "/" + sectionStickers.size() + ")");
        title.setForeground(Theme.TEXT_PRIMARY);
        title.setFont(Theme.FONT_SEMIBOLD.deriveFont(Theme.SIZE_BASE));

        sectionHeader.add(flag);
        sectionHeader.add(title);

        return sectionHeader;
    }

    private void configureFlag(JLabel flag, Section section) {
        flag.setForeground(Theme.TEXT_MUTED);
        flag.setFont(Theme.FONT_MONO.deriveFont(Theme.SIZE_XS));

        final var flagAsset = section.getFlagAsset();
        if (flagAsset == null || flagAsset.isBlank()) {
            flag.setText(section.getPrefix());
            return;
        }

        final var flagResource = getClass().getResource("/assets/flags/" + flagAsset);
        if (flagResource == null) {
            flag.setText(section.getPrefix());
            return;
        }

        final var flagImage = new ImageIcon(flagResource)
            .getImage()
            .getScaledInstance(FLAG_ICON_WIDTH, FLAG_ICON_HEIGHT, Image.SCALE_SMOOTH);
        flag.setIcon(new ImageIcon(flagImage));
    }

    private JPanel buildSectionGrid(List<Sticker> sectionStickers) {
        final var sectionGrid = new JPanel(new WrapLayout(FlowLayout.LEFT, Theme.SPACE_SM, Theme.SPACE_SM));
        sectionGrid.setOpaque(false);
        sectionGrid.setAlignmentX(LEFT_ALIGNMENT);

        for (final var sticker : sectionStickers) {
            final var userSticker = collectedByStickerId.get(sticker.getId());
            final var stickerCard = new StickerCard(
                sticker,
                userSticker != null,
                userSticker == null ? 0 : userSticker.getQuantity()
            );
            stickerCard.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            stickerCard.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent mouseEvent) {
                    onStickerSelected.accept(sticker, Optional.ofNullable(userSticker));
                }
            });

            sectionGrid.add(stickerCard);
        }

        return sectionGrid;
    }
}
