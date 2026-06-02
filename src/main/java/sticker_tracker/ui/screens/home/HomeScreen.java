package sticker_tracker.ui.screens.home;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import sticker_tracker.ui.Theme;
import sticker_tracker.ui.components.EmptyState;
import sticker_tracker.ui.screens.home.sections.ActivitySection;
import sticker_tracker.ui.screens.home.sections.CollectionProgressSection;
import sticker_tracker.ui.screens.home.sections.HeaderSection;
import sticker_tracker.ui.screens.home.sections.HomeStatsSection;
import sticker_tracker.ui.screens.home.sections.TeamsProgressSection;

public final class HomeScreen extends JPanel {

    private final HomeDataLoader homeDataLoader;
    private final Runnable onOpenAlbum;

    private HomeData currentHomeData;
    private boolean loadingData;

    public HomeScreen() {
        this(() -> {});
    }

    public HomeScreen(Runnable onOpenAlbum) {
        this.homeDataLoader = new HomeDataLoader();
        this.onOpenAlbum = onOpenAlbum == null ? () -> {} : onOpenAlbum;

        setOpaque(true);
        setBackground(Theme.BG_PRIMARY);
        setLayout(new BorderLayout());

        loadData(true);
    }

    public void refreshData() {
        loadData(currentHomeData == null);
    }

    private void loadData(boolean showLoading) {
        if (loadingData) {
            return;
        }

        loadingData = true;

        if (showLoading) {
            showLoadingState();
        }

        homeDataLoader.load(this::showDataState, this::showErrorState);
    }

    private void showDataState(HomeData homeData) {
        loadingData = false;
        currentHomeData = homeData;

        if (homeData.isEmpty()) {
            showEmptyState();
            return;
        }

        showContent(buildScrollableContent(homeData));
    }

    private void showLoadingState() {
        final var loadingLabel = new JLabel("Carregando...", SwingConstants.CENTER);
        loadingLabel.setForeground(Theme.TEXT_SECONDARY);
        loadingLabel.setFont(Theme.FONT_REGULAR.deriveFont(Theme.SIZE_BASE));

        showContent(loadingLabel);
    }

    private void showEmptyState() {
        showContent(new EmptyState("Nenhuma figurinha ainda.", "Vá ao Álbum para começar."));
    }

    private void showErrorState(Exception exception) {
        loadingData = false;

        if (currentHomeData == null) {
            showErrorContent();
        }
    }

    private void showErrorContent() {
        showContent(new EmptyState(
            "Não foi possível carregar a Home.",
            "Tente carregar os dados novamente.",
            "Tentar novamente",
            () -> loadData(true)
        ));
    }

    private JScrollPane buildScrollableContent(HomeData homeData) {
        final var contentPanel = new ViewportWidthPanel();
        contentPanel.setBackground(Theme.BG_PRIMARY);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(
            Theme.PAGE_PADDING,
            Theme.PAGE_PADDING,
            Theme.PAGE_PADDING,
            Theme.PAGE_PADDING
        ));

        contentPanel.add(new HeaderSection(homeData.whatsAppTradeMessage(), onOpenAlbum));
        contentPanel.add(Box.createVerticalStrut(Theme.SPACE_LG));
        contentPanel.add(new CollectionProgressSection(homeData));
        contentPanel.add(Box.createVerticalStrut(Theme.SPACE_LG));
        contentPanel.add(new HomeStatsSection(homeData));
        contentPanel.add(Box.createVerticalStrut(Theme.SPACE_LG));
        contentPanel.add(buildDashboardBottomRow(homeData));

        final var scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(Theme.SCROLL_UNIT_INCREMENT);
        scrollPane.getViewport().setBackground(Theme.BG_PRIMARY);

        return scrollPane;
    }

    private JPanel buildDashboardBottomRow(HomeData homeData) {
        final var bottomRow = new JPanel(new GridBagLayout());
        bottomRow.setOpaque(false);
        bottomRow.setAlignmentX(LEFT_ALIGNMENT);
        bottomRow.setPreferredSize(new Dimension(Theme.SPACE_NONE, Theme.DASHBOARD_BOTTOM_ROW_HEIGHT));
        bottomRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.DASHBOARD_BOTTOM_ROW_HEIGHT));

        final var constraints = new GridBagConstraints();
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1.0;

        constraints.gridx = 0;
        constraints.weightx = 0.62;
        constraints.insets = new Insets(Theme.SPACE_NONE, Theme.SPACE_NONE, Theme.SPACE_NONE, Theme.SPACE_SM);
        bottomRow.add(new TeamsProgressSection(homeData), constraints);

        constraints.gridx = 1;
        constraints.weightx = 0.38;
        constraints.insets = new Insets(Theme.SPACE_NONE, Theme.SPACE_SM, Theme.SPACE_NONE, Theme.SPACE_NONE);
        bottomRow.add(new ActivitySection(homeData), constraints);

        return bottomRow;
    }

    private void showContent(Component component) {
        removeAll();
        add(component, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private static final class ViewportWidthPanel extends JPanel implements Scrollable {

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(
            Rectangle visibleRectangle,
            int orientation,
            int direction
        ) {
            return Theme.SCROLL_UNIT_INCREMENT;
        }

        @Override
        public int getScrollableBlockIncrement(
            Rectangle visibleRectangle,
            int orientation,
            int direction
        ) {
            return visibleRectangle.height;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }
}
