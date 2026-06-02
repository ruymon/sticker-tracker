package sticker_tracker.ui;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.IOException;

public final class FontLoader {

    private FontLoader() {}

    public static void load() {
        Theme.FONT_REGULAR = loadFont("/assets/fonts/Geist-Regular.ttf");
        Theme.FONT_MEDIUM = loadFont("/assets/fonts/Geist-Medium.ttf");
        Theme.FONT_SEMIBOLD = loadFont("/assets/fonts/Geist-SemiBold.ttf");
        Theme.FONT_BOLD = loadFont("/assets/fonts/Geist-Bold.ttf");
        Theme.FONT_MONO = loadFont("/assets/fonts/GeistMono-Regular.ttf");
    }

    private static Font loadFont(String path) {
        try (var stream = FontLoader.class.getResourceAsStream(path)) {
            if (stream == null) {
                throw new IOException("Font resource not found");
            }

            final var font = Font.createFont(Font.TRUETYPE_FONT, stream);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);

            return font;
        } catch (Exception e) {
            System.err.println("Font not found: " + path + " - using fallback");
            return new Font("SansSerif", Font.PLAIN, 14);
        }
    }
}
