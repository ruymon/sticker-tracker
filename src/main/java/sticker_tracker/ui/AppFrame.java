package sticker_tracker.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import sticker_tracker.config.AppConfig;
import sticker_tracker.config.NavigationConfig;
import sticker_tracker.ui.screens.album.AlbumScreen;
import sticker_tracker.ui.screens.home.HomeScreen;

public final class AppFrame extends JFrame {

    private final CardLayout cardLayout;
    private final JPanel contentPanel;
    private final Map<String, JPanel> navigationIndicators;
    private final Map<String, JLabel> navigationLabels;
    private final HomeScreen homeScreen;
    private final AlbumScreen albumScreen;

    private String activeScreenName;

    public AppFrame() {
        setTitle(AppConfig.APP_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(Theme.WINDOW_MIN_WIDTH, Theme.WINDOW_MIN_HEIGHT));
        setSize(Theme.WINDOW_MIN_WIDTH, Theme.WINDOW_MIN_HEIGHT);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BG_PRIMARY);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        navigationIndicators = new HashMap<>();
        navigationLabels = new HashMap<>();
        homeScreen = new HomeScreen();
        albumScreen = new AlbumScreen();
        activeScreenName = NavigationConfig.SCREEN_HOME;

        buildLayout();
    }

    private void buildLayout() {
        contentPanel.setBackground(Theme.BG_PRIMARY);
        contentPanel.add(homeScreen, NavigationConfig.SCREEN_HOME);
        contentPanel.add(albumScreen, NavigationConfig.SCREEN_ALBUM);

        setLayout(new BorderLayout());
        add(buildSidebar(), BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        refreshNavigation();
    }

    private JPanel buildSidebar() {
        final var sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(Theme.SIDEBAR_WIDTH, Theme.WINDOW_MIN_HEIGHT));
        sidebar.setBackground(Theme.BG_SECONDARY);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(javax.swing.BorderFactory.createEmptyBorder(
            Theme.SPACE_XL,
            Theme.SPACE_MD,
            Theme.SPACE_MD,
            Theme.SPACE_MD
        ));

        final var logo = new JLabel(AppConfig.APP_NAME);
        logo.setAlignmentX(LEFT_ALIGNMENT);
        logo.setForeground(Theme.TEXT_PRIMARY);
        logo.setFont(Theme.FONT_BOLD.deriveFont(Theme.SIZE_LG));

        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(Theme.SPACE_XL));
        sidebar.add(buildNavigationItem(NavigationConfig.SCREEN_HOME, NavigationConfig.SCREEN_HOME_LABEL));
        sidebar.add(Box.createVerticalStrut(Theme.SPACE_SM));
        sidebar.add(buildNavigationItem(NavigationConfig.SCREEN_ALBUM, NavigationConfig.SCREEN_ALBUM_LABEL));
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(buildSlogan());

        return sidebar;
    }

    private JPanel buildNavigationItem(String screenName, String labelText) {
        final var navigationItem = new JPanel(new BorderLayout(Theme.SPACE_SM, Theme.SPACE_NONE));
        navigationItem.setAlignmentX(LEFT_ALIGNMENT);
        navigationItem.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.SPACE_2XL));
        navigationItem.setBackground(Theme.BG_SECONDARY);
        navigationItem.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        final var navigationIndicator = new JPanel();
        navigationIndicator.setPreferredSize(new Dimension(Theme.NAVIGATION_INDICATOR_WIDTH, Theme.SPACE_LG));

        final var navigationLabel = new JLabel(labelText);
        navigationLabel.setFont(Theme.FONT_MEDIUM.deriveFont(Theme.SIZE_BASE));

        navigationItem.add(navigationIndicator, BorderLayout.WEST);
        navigationItem.add(navigationLabel, BorderLayout.CENTER);
        navigationItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                showScreen(screenName);
            }
        });

        navigationIndicators.put(screenName, navigationIndicator);
        navigationLabels.put(screenName, navigationLabel);

        return navigationItem;
    }

    private JLabel buildSlogan() {
        final var slogan = new JLabel("<html>" + AppConfig.APP_SLOGAN + "</html>");
        slogan.setAlignmentX(LEFT_ALIGNMENT);
        slogan.setForeground(Theme.TEXT_MUTED);
        slogan.setFont(Theme.FONT_REGULAR.deriveFont(Theme.SIZE_XS));

        return slogan;
    }

    public void showScreen(String screenName) {
        activeScreenName = screenName;
        cardLayout.show(contentPanel, screenName);
        refreshActiveScreen();
        refreshNavigation();
    }

    private void refreshActiveScreen() {
        if (NavigationConfig.SCREEN_HOME.equals(activeScreenName)) {
            homeScreen.refreshData();
        }
    }

    private void refreshNavigation() {
        for (final var navigationIndicatorEntry : navigationIndicators.entrySet()) {
            final var screenName = navigationIndicatorEntry.getKey();
            final var screenIsActive = screenName.equals(activeScreenName);
            final var navigationIndicator = navigationIndicatorEntry.getValue();
            final var navigationLabel = navigationLabels.get(screenName);

            navigationIndicator.setBackground(screenIsActive ? Theme.ACCENT : Theme.BG_SECONDARY);
            navigationLabel.setForeground(screenIsActive ? Theme.TEXT_PRIMARY : Theme.TEXT_SECONDARY);
        }
    }
}
