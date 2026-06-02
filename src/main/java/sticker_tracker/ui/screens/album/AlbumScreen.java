package sticker_tracker.ui.screens.album;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import sticker_tracker.domain.Sticker;
import sticker_tracker.domain.UserSticker;
import sticker_tracker.ui.Theme;
import sticker_tracker.ui.components.EmptyState;
import sticker_tracker.ui.components.FilterBar;
import sticker_tracker.ui.screens.album.sections.AlbumGridSection;
import sticker_tracker.ui.screens.album.sections.AlbumHeaderSection;
import sticker_tracker.ui.screens.album.sections.AlbumToolbarSection;

public final class AlbumScreen extends JPanel {

    private static final int SEARCH_DEBOUNCE_MS = 300;

    private final AlbumDataLoader albumDataLoader;

    private AlbumData currentAlbumData;
    private FilterBar.Filter activeFilter;
    private String searchTerm;
    private JPanel gridPanel;
    private Timer searchDebounceTimer;

    public AlbumScreen() {
        this.albumDataLoader = new AlbumDataLoader();
        this.activeFilter = FilterBar.Filter.ALL;
        this.searchTerm = "";

        setOpaque(true);
        setBackground(Theme.BG_PRIMARY);
        setLayout(new BorderLayout());

        loadAlbumData();
    }

    public void refreshAlbum() {
        loadAlbumData(false);
    }

    private void loadAlbumData() {
        loadAlbumData(true);
    }

    private void loadAlbumData(boolean showLoading) {
        if (showLoading || currentAlbumData == null) {
            showLoadingState();
        }

        albumDataLoader.load(this::showAlbumData, exception -> showErrorState());
    }

    private void showLoadingState() {
        final var loadingLabel = new JLabel("Carregando álbum...", SwingConstants.CENTER);
        loadingLabel.setForeground(Theme.TEXT_SECONDARY);
        loadingLabel.setFont(Theme.FONT_REGULAR.deriveFont(Theme.SIZE_BASE));

        showContent(loadingLabel);
    }

    private void showErrorState() {
        showContent(new EmptyState(
            "Não foi possível carregar o álbum.",
            "Tente carregar os dados novamente.",
            "Tentar novamente",
            this::loadAlbumData
        ));
    }

    private void showAlbumData(AlbumData albumData) {
        this.currentAlbumData = albumData;
        showContent(buildScrollableContent(albumData));
        refreshGrid();
    }

    private JScrollPane buildScrollableContent(AlbumData albumData) {
        final var contentPanel = new JPanel();
        contentPanel.setBackground(Theme.BG_PRIMARY);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(
            Theme.SPACE_XL,
            Theme.SPACE_XL,
            Theme.SPACE_XL,
            Theme.SPACE_XL
        ));

        gridPanel = new JPanel(new BorderLayout());
        gridPanel.setOpaque(false);

        contentPanel.add(new AlbumHeaderSection(
            albumData.collectionName(),
            albumData.collectedCount(),
            albumData.totalCount()
        ));
        contentPanel.add(Box.createVerticalStrut(Theme.SPACE_MD));
        contentPanel.add(new AlbumToolbarSection(
            activeFilter,
            searchTerm,
            this::onFilterChanged,
            this::onSearchChanged
        ));
        contentPanel.add(Box.createVerticalStrut(Theme.SPACE_LG));
        contentPanel.add(gridPanel);

        final var scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(Theme.SCROLL_UNIT_INCREMENT);
        scrollPane.getViewport().setBackground(Theme.BG_PRIMARY);

        return scrollPane;
    }

    private void onFilterChanged(FilterBar.Filter filter) {
        activeFilter = filter;
        refreshGrid();
    }

    private void onSearchChanged(String searchValue) {
        if (searchDebounceTimer != null && searchDebounceTimer.isRunning()) {
            searchDebounceTimer.stop();
        }

        searchDebounceTimer = new Timer(SEARCH_DEBOUNCE_MS, actionEvent -> {
            searchTerm = searchValue.trim();
            refreshGrid();
        });
        searchDebounceTimer.setRepeats(false);
        searchDebounceTimer.start();
    }

    private void refreshGrid() {
        if (currentAlbumData == null || gridPanel == null) {
            return;
        }

        final var collectedByStickerId = currentAlbumData.collectedByStickerId();
        final var filteredStickers = applyFilter(currentAlbumData.stickers(), collectedByStickerId);

        gridPanel.removeAll();

        if (filteredStickers.isEmpty()) {
            gridPanel.add(buildNoResultsState(), BorderLayout.CENTER);
        } else {
            gridPanel.add(new AlbumGridSection(
                currentAlbumData,
                filteredStickers,
                collectedByStickerId,
                this::openStickerDialog
            ), BorderLayout.CENTER);
        }

        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private Component buildNoResultsState() {
        if (!searchTerm.isBlank()) {
            return new EmptyState("Nenhum resultado.", "Nenhum resultado para \"" + searchTerm + "\".");
        }

        return new EmptyState("Nenhuma figurinha.", "Nenhuma figurinha para este filtro.");
    }

    private List<Sticker> applyFilter(
        List<Sticker> stickers,
        Map<String, UserSticker> collectedByStickerId
    ) {
        final var filteredByStatus = switch (activeFilter) {
            case ALL -> stickers;
            case COLLECTED -> stickers.stream()
                .filter(sticker -> collectedByStickerId.containsKey(sticker.getId()))
                .toList();
            case MISSING -> stickers.stream()
                .filter(sticker -> !collectedByStickerId.containsKey(sticker.getId()))
                .toList();
            case REPEATED -> stickers.stream()
                .filter(sticker -> {
                    final var userSticker = collectedByStickerId.get(sticker.getId());
                    return userSticker != null && userSticker.isRepeated();
                })
                .toList();
        };

        if (searchTerm.isBlank()) {
            return filteredByStatus;
        }

        final var normalizedSearchTerm = searchTerm.toLowerCase(Locale.ROOT);
        return filteredByStatus.stream()
            .filter(sticker -> sticker.getCode().toLowerCase(Locale.ROOT).contains(normalizedSearchTerm)
                || sticker.getName().toLowerCase(Locale.ROOT).contains(normalizedSearchTerm))
            .toList();
    }

    private void openStickerDialog(Sticker sticker, java.util.Optional<UserSticker> userSticker) {
        StickerQuantityDialog.show(
            this,
            sticker,
            userSticker,
            newQuantity -> saveStickerQuantity(sticker.getId(), newQuantity)
        );
    }

    private void saveStickerQuantity(String stickerId, int quantity) {
        albumDataLoader.saveQuantity(stickerId, quantity, this::refreshAlbum, exception -> showErrorState());
    }

    private void showContent(Component component) {
        removeAll();
        add(component, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
}
