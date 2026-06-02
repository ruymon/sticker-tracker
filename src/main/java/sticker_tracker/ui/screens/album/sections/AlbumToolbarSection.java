package sticker_tracker.ui.screens.album.sections;

import java.awt.BorderLayout;
import java.util.function.Consumer;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import sticker_tracker.ui.Theme;
import sticker_tracker.ui.components.FilterBar;

public final class AlbumToolbarSection extends JPanel {

    public AlbumToolbarSection(
        FilterBar.Filter activeFilter,
        String searchTerm,
        Consumer<FilterBar.Filter> onFilterChange,
        Consumer<String> onSearchChange
    ) {
        setOpaque(false);
        setAlignmentX(LEFT_ALIGNMENT);
        setLayout(new BorderLayout(Theme.SPACE_MD, Theme.SPACE_NONE));

        final var filterBar = new FilterBar();
        filterBar.setActiveFilter(activeFilter);
        filterBar.setOnFilterChange(onFilterChange);

        final var searchField = new JTextField(searchTerm, 18);
        searchField.putClientProperty("JTextField.placeholderText", "Buscar");
        searchField.setForeground(Theme.TEXT_PRIMARY);
        searchField.setFont(Theme.FONT_REGULAR.deriveFont(Theme.SIZE_BASE));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                onSearchChange.accept(searchField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                onSearchChange.accept(searchField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                onSearchChange.accept(searchField.getText());
            }
        });

        add(filterBar, BorderLayout.WEST);
        add(searchField, BorderLayout.EAST);
    }
}
