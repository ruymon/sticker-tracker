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
}
