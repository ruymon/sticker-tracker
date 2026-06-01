package sticker_tracker.ui;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;

public final class FontLoader {

    private FontLoader() {}

    public static void load() {
        Theme.FONT_REGULAR = loadFont("/assets/fonts/Geist-Regular.ttf", Font.PLAIN);
        Theme.FONT_MEDIUM = loadFont("/assets/fonts/Geist-Medium.ttf", Font.PLAIN);
        Theme.FONT_SEMIBOLD = loadFont("/assets/fonts/Geist-SemiBold.ttf", Font.BOLD);
        Theme.FONT_BOLD = loadFont("/assets/fonts/Geist-Bold.ttf", Font.BOLD);
        Theme.FONT_MONO = loadFont("/assets/fonts/GeistMono-Regular.ttf", Font.PLAIN);
    }

    private static Font loadFont(String path, int fallbackStyle) {
        try (var stream = FontLoader.class.getResourceAsStream(path)) {
            if (stream == null) {
                throw new IOException("Font resource not found");
            }

            final var font = Font.createFont(Font.TRUETYPE_FONT, stream);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);

            return font;
        } catch (FontFormatException | IOException e) {
            System.err.println("Font not found: " + path + " - using fallback");
            return new Font("SansSerif", fallbackStyle, 14);
        }
    }
}
