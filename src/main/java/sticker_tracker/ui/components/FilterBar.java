package sticker_tracker.ui.components;

import java.awt.FlowLayout;
import java.util.EnumMap;
import java.util.function.Consumer;
import javax.swing.JPanel;
import sticker_tracker.ui.Theme;

public class FilterBar extends JPanel {

    public enum Filter {
        ALL,
        COLLECTED,
        MISSING,
        REPEATED
    }

    private final EnumMap<Filter, RoundedButton> buttons;
    private Filter activeFilter;
    private Consumer<Filter> onFilterChange;

    public FilterBar() {
        this.buttons = new EnumMap<>(Filter.class);
        this.activeFilter = Filter.ALL;

        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.LEFT, Theme.SPACE_SM, Theme.SPACE_NONE));
        buildFilters();
        refreshButtonStates();
    }

    public void setOnFilterChange(Consumer<Filter> callback) {
        this.onFilterChange = callback;
    }

    private void buildFilters() {
        for (final var filter : Filter.values()) {
            final var button = new RoundedButton(labelFor(filter), RoundedButton.Variant.SECONDARY);
            button.addActionListener(actionEvent -> {
                activeFilter = filter;
                refreshButtonStates();

                if (onFilterChange != null) {
                    onFilterChange.accept(filter);
                }
            });

            buttons.put(filter, button);
            add(button);
        }
    }

    private String labelFor(Filter filter) {
        return switch (filter) {
            case ALL -> "Todas";
            case COLLECTED -> "Coletadas";
            case MISSING -> "Faltando";
            case REPEATED -> "Repetidas";
        };
    }

    private void refreshButtonStates() {
        for (final var filterButtonEntry : buttons.entrySet()) {
            filterButtonEntry.getValue().setActive(filterButtonEntry.getKey() == activeFilter);
        }
    }
}
