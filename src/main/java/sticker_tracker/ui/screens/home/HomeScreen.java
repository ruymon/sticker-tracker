package sticker_tracker.ui.screens.home;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import sticker_tracker.ui.Theme;
import sticker_tracker.ui.components.EmptyState;
import sticker_tracker.ui.screens.home.sections.CollectionProgressSection;
import sticker_tracker.ui.screens.home.sections.HeaderSection;
import sticker_tracker.ui.screens.home.sections.RecentStickersSection;
import sticker_tracker.ui.screens.home.sections.RepeatedStickersSection;

public final class HomeScreen extends JPanel {

    private final HomeDataLoader homeDataLoader;

    public HomeScreen() {
        this.homeDataLoader = new HomeDataLoader();

        setOpaque(true);
        setBackground(Theme.BG_PRIMARY);
        setLayout(new BorderLayout());

        loadData();
    }

    public void refreshData() {
        loadData();
    }

    private void loadData() {
        showLoadingState();
        homeDataLoader.load(this::showDataState, exception -> showErrorState());
    }

    private void showDataState(HomeData homeData) {
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

    private void showErrorState() {
        showContent(new EmptyState(
            "Não foi possível carregar a Home.",
            "Tente carregar os dados novamente.",
            "Tentar novamente",
            this::loadData
        ));
    }

    private JScrollPane buildScrollableContent(HomeData homeData) {
        final var contentPanel = new JPanel();
        contentPanel.setBackground(Theme.BG_PRIMARY);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(
            Theme.SPACE_XL,
            Theme.SPACE_XL,
            Theme.SPACE_XL,
            Theme.SPACE_XL
        ));

        contentPanel.add(new HeaderSection());
        contentPanel.add(Box.createVerticalStrut(Theme.SPACE_LG));
        contentPanel.add(new CollectionProgressSection(homeData));
        contentPanel.add(Box.createVerticalStrut(Theme.SPACE_LG));
        contentPanel.add(new RecentStickersSection(homeData));
        contentPanel.add(Box.createVerticalStrut(Theme.SPACE_LG));
        contentPanel.add(new RepeatedStickersSection(homeData));

        final var scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(Theme.SCROLL_UNIT_INCREMENT);
        scrollPane.getViewport().setBackground(Theme.BG_PRIMARY);

        return scrollPane;
    }

    private void showContent(Component component) {
        removeAll();
        add(component, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
}
