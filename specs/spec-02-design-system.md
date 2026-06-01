# spec-02 — Design System

> Tokens visuais, carregamento de fontes e configuração global do tema.
> Nenhum componente de UI deve ser implementado antes desta spec estar completa.
> Zero valores hardcoded em qualquer componente — tudo referencia Theme.*

---

## 1. Theme.java

```java
// sticker_tracker/ui/Theme.java
public final class Theme {

    private Theme() {}

    // Backgrounds
    public static final Color BG_PRIMARY    = Color.decode("#0A0A0A");
    public static final Color BG_SECONDARY  = Color.decode("#141414");
    public static final Color BG_CARD       = Color.decode("#1C1C1C");
    public static final Color BG_HOVER      = Color.decode("#242424");

    // Text
    public static final Color TEXT_PRIMARY   = Color.decode("#F5F5F5");
    public static final Color TEXT_SECONDARY = Color.decode("#A3A3A3");
    public static final Color TEXT_MUTED     = Color.decode("#525252");

    // Accent — verde Copa
    public static final Color ACCENT         = Color.decode("#22C55E");
    public static final Color ACCENT_HOVER   = Color.decode("#16A34A");
    public static final Color ACCENT_MUTED   = Color.decode("#14532D");

    // Borders
    public static final Color BORDER         = Color.decode("#262626");
    public static final Color BORDER_FOCUS   = Color.decode("#404040");

    // Status
    public static final Color SUCCESS        = Color.decode("#22C55E");
    public static final Color WARNING        = Color.decode("#EAB308");
    public static final Color DANGER         = Color.decode("#EF4444");

    // Fonts — populadas pelo FontLoader
    public static Font FONT_REGULAR;
    public static Font FONT_MEDIUM;
    public static Font FONT_SEMIBOLD;
    public static Font FONT_BOLD;
    public static Font FONT_MONO;

    // Font sizes
    public static final float SIZE_XS   = 11f;
    public static final float SIZE_SM   = 12f;
    public static final float SIZE_BASE = 14f;
    public static final float SIZE_MD   = 16f;
    public static final float SIZE_LG   = 20f;
    public static final float SIZE_XL   = 24f;
    public static final float SIZE_2XL  = 32f;

    // Spacing
    public static final int SPACE_XS  = 4;
    public static final int SPACE_SM  = 8;
    public static final int SPACE_MD  = 16;
    public static final int SPACE_LG  = 24;
    public static final int SPACE_XL  = 32;
    public static final int SPACE_2XL = 48;

    // Border radius
    public static final int RADIUS_SM  = 6;
    public static final int RADIUS_MD  = 12;
    public static final int RADIUS_LG  = 16;
    public static final int RADIUS_XL  = 24;
}
```

---

## 2. FontLoader.java

```java
// sticker_tracker/ui/FontLoader.java
public final class FontLoader {

    private FontLoader() {}

    public static void load() {
        Theme.FONT_REGULAR  = loadFont("/assets/fonts/Geist-Regular.ttf");
        Theme.FONT_MEDIUM   = loadFont("/assets/fonts/Geist-Medium.ttf");
        Theme.FONT_SEMIBOLD = loadFont("/assets/fonts/Geist-SemiBold.ttf");
        Theme.FONT_BOLD     = loadFont("/assets/fonts/Geist-Bold.ttf");
        Theme.FONT_MONO     = loadFont("/assets/fonts/GeistMono-Regular.ttf");
    }

    private static Font loadFont(String path) {
        try (var stream = FontLoader.class.getResourceAsStream(path)) {
            final var font = Font.createFont(Font.TRUETYPE_FONT, stream);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
            return font;
        } catch (Exception e) {
            System.err.println("Font not found: " + path + " — using fallback");
            return new Font("SansSerif", Font.PLAIN, 14);
        }
    }
}
```

---

## 3. FlatLaf — Overrides globais

Aplicado em `Main.java` antes de qualquer JFrame:

```java
UIManager.put("defaultFont",          Theme.FONT_REGULAR.deriveFont(Theme.SIZE_BASE));
UIManager.put("Panel.background",     Theme.BG_PRIMARY);
UIManager.put("Button.arc",           Theme.RADIUS_MD);
UIManager.put("Component.arc",        Theme.RADIUS_MD);
UIManager.put("TextComponent.arc",    Theme.RADIUS_MD);
UIManager.put("ScrollBar.thumbArc",   Theme.RADIUS_SM);
UIManager.put("ScrollBar.width",      6);
UIManager.put("ScrollBar.thumb",      Theme.BG_HOVER);
UIManager.put("TextField.background", Theme.BG_SECONDARY);
UIManager.put("TextField.foreground", Theme.TEXT_PRIMARY);
UIManager.put("TextField.caretColor", Theme.ACCENT);
UIManager.put("Separator.foreground", Theme.BORDER);
```

---

## 4. Referência de Tokens

| Token | Valor | Uso |
|---|---|---|
| `BG_PRIMARY` | `#0A0A0A` | Fundo da janela principal |
| `BG_SECONDARY` | `#141414` | Sidebar, inputs |
| `BG_CARD` | `#1C1C1C` | Cards, painéis elevados |
| `BG_HOVER` | `#242424` | Hover em botões e itens |
| `TEXT_PRIMARY` | `#F5F5F5` | Texto principal |
| `TEXT_SECONDARY` | `#A3A3A3` | Texto de suporte |
| `TEXT_MUTED` | `#525252` | Placeholder, metadados |
| `ACCENT` | `#22C55E` | Verde Copa — CTAs, progresso |
| `BORDER` | `#262626` | Separadores, bordas |

---

## 5. Checklist de Conclusão

- [ ] `Theme.java` criado com todos os tokens
- [ ] `FontLoader.load()` chamado antes de qualquer Swing
- [ ] Geist Regular, Medium, SemiBold, Bold carregando sem erro
- [ ] Geist Mono carregando sem erro
- [ ] Fallback para SansSerif se fonte não encontrada
- [ ] FlatLaf dark theme aplicado globalmente
- [ ] Janela abre com fundo `#0A0A0A` e fonte Geist
