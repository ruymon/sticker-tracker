package sticker_tracker.ui;

import java.awt.Color;
import java.awt.Font;

public final class Theme {

    private Theme() {}

    public static final Color BG_PRIMARY = Color.decode("#0A0A0A");
    public static final Color BG_SECONDARY = Color.decode("#141414");
    public static final Color BG_CARD = Color.decode("#1C1C1C");
    public static final Color BG_HOVER = Color.decode("#242424");

    public static final Color TEXT_PRIMARY = Color.decode("#F5F5F5");
    public static final Color TEXT_SECONDARY = Color.decode("#A3A3A3");
    public static final Color TEXT_MUTED = Color.decode("#525252");

    public static final Color ACCENT = Color.decode("#22C55E");
    public static final Color ACCENT_HOVER = Color.decode("#16A34A");
    public static final Color ACCENT_MUTED = Color.decode("#14532D");

    public static final Color BORDER = Color.decode("#262626");
    public static final Color BORDER_FOCUS = Color.decode("#404040");

    public static final Color SUCCESS = Color.decode("#22C55E");
    public static final Color WARNING = Color.decode("#EAB308");
    public static final Color DANGER = Color.decode("#EF4444");
    public static final Color TRANSPARENT = new Color(0, 0, 0, 0);

    public static Font FONT_REGULAR;
    public static Font FONT_MEDIUM;
    public static Font FONT_SEMIBOLD;
    public static Font FONT_BOLD;
    public static Font FONT_MONO;

    public static final float SIZE_XS = 11f;
    public static final float SIZE_SM = 12f;
    public static final float SIZE_BASE = 14f;
    public static final float SIZE_MD = 16f;
    public static final float SIZE_LG = 20f;
    public static final float SIZE_XL = 24f;
    public static final float SIZE_2XL = 32f;

    public static final int SPACE_XS = 4;
    public static final int SPACE_SM = 8;
    public static final int SPACE_MD = 16;
    public static final int SPACE_LG = 24;
    public static final int SPACE_XL = 32;
    public static final int SPACE_2XL = 48;

    public static final int RADIUS_SM = 6;
    public static final int RADIUS_MD = 12;
    public static final int RADIUS_LG = 16;
    public static final int RADIUS_XL = 24;

    public static final int WINDOW_MIN_WIDTH = 1100;
    public static final int WINDOW_MIN_HEIGHT = 720;
    public static final int SIDEBAR_WIDTH = 220;
    public static final int SPACE_NONE = 0;
    public static final int NAVIGATION_INDICATOR_WIDTH = 3;
    public static final int STICKER_CARD_WIDTH = 100;
    public static final int STICKER_CARD_HEIGHT = 140;
    public static final int STICKER_IMAGE_SIZE = 80;
    public static final int STICKER_NAME_MAXIMUM_LENGTH = 18;
    public static final int PROGRESS_BAR_HEIGHT = 8;
    public static final int SECTION_PROGRESS_BAR_HEIGHT = 4;
    public static final int REPEATED_LIST_ROW_HEIGHT = 36;
    public static final int REPEATED_LIST_VISIBLE_ROWS = 5;
    public static final int COPY_FEEDBACK_DURATION_MS = 2000;
    public static final int SCROLL_BAR_WIDTH = 6;
    public static final int SCROLL_UNIT_INCREMENT = 16;
    public static final int WRAP_LAYOUT_SCROLL_ADJUSTMENT = 1;
    public static final int WRAP_LAYOUT_DOUBLE_GAP_MULTIPLIER = 2;

    public static final float DISABLED_ALPHA = 0.35f;
    public static final double PERCENTAGE_EMPTY = 0.0;
    public static final double PERCENTAGE_FULL = 100.0;
}
