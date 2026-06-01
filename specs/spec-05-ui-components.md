# spec-05 — Componentes de UI Base

> Componentes reutilizáveis e AppFrame principal.
> Nenhuma tela deve ser implementada antes desta spec estar concluída.
> Zero valores hardcoded — tudo via Theme.*

---

## 1. AppFrame — Janela Principal

```java
// sticker_tracker/ui/AppFrame.java
public final class AppFrame extends JFrame {

    private final CardLayout cardLayout;
    private final JPanel contentPanel;

    public AppFrame() {
        setTitle("Cola Aí");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 720));
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BG_PRIMARY);

        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        buildLayout();
    }

    private void buildLayout() {
        final var sidebar = buildSidebar();
        setLayout(new BorderLayout());
        add(sidebar,       BorderLayout.WEST);
        add(contentPanel,  BorderLayout.CENTER);
    }

    private JPanel buildSidebar() {
        // Largura fixa: 220px, fundo BG_SECONDARY
        // Topo: logo "Cola Aí" — FONT_BOLD SIZE_LG
        // Itens de navegação: Home, Álbum
        // Item ativo: barra vertical de 3px em ACCENT à esquerda
        // Rodapé: slogan em TEXT_MUTED SIZE_XS
    }

    public void showScreen(String screenName) {
        cardLayout.show(contentPanel, screenName);
    }
}
```

---

## 2. RoundedPanel

Painel com bordas arredondadas desenhadas via `Graphics2D`.

```java
// sticker_tracker/ui/components/RoundedPanel.java
public class RoundedPanel extends JPanel {

    private final int radius;
    private Color backgroundColor;

    public RoundedPanel(int radius) {
        this.radius          = radius;
        this.backgroundColor = Theme.BG_CARD;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        final var g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(backgroundColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        g2.dispose();
        super.paintComponent(g);
    }

    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        repaint();
    }
}
```

---

## 3. RoundedButton

Botão com hover state e três variantes.

```java
// sticker_tracker/ui/components/RoundedButton.java
public class RoundedButton extends JButton {

    public enum Variant { PRIMARY, SECONDARY, GHOST }

    private final Variant variant;
    private boolean hovered = false;

    public RoundedButton(String text, Variant variant) {
        super(text);
        this.variant = variant;
        setOpaque(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFont(Theme.FONT_MEDIUM.deriveFont(Theme.SIZE_SM));

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
            @Override public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        final var g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(resolveBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), Theme.RADIUS_MD, Theme.RADIUS_MD);
        g2.dispose();
        super.paintComponent(g);
    }

    private Color resolveBackground() {
        return switch (variant) {
            case PRIMARY   -> hovered ? Theme.ACCENT_HOVER : Theme.ACCENT;
            case SECONDARY -> hovered ? Theme.BG_HOVER     : Theme.BG_CARD;
            case GHOST     -> hovered ? Theme.BG_HOVER     : new Color(0, 0, 0, 0);
        };
    }
}
```

---

## 4. StickerCard

Card de figurinha com estado coletada vs não coletada.

```java
// sticker_tracker/ui/components/StickerCard.java
public class StickerCard extends RoundedPanel {

    private static final int CARD_WIDTH  = 100;
    private static final int CARD_HEIGHT = 140;

    private final Sticker sticker;
    private final boolean collected;
    private final int quantity;

    public StickerCard(Sticker sticker, boolean collected, int quantity) {
        super(Theme.RADIUS_MD);
        this.sticker   = sticker;
        this.collected = collected;
        this.quantity  = quantity;

        setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        setBackgroundColor(collected ? Theme.BG_CARD : Theme.BG_SECONDARY);
        buildCard();
    }

    private void buildCard() {
        setLayout(new BorderLayout(0, Theme.SPACE_XS));
        setBorder(BorderFactory.createEmptyBorder(
            Theme.SPACE_SM, Theme.SPACE_SM, Theme.SPACE_SM, Theme.SPACE_SM
        ));
        add(buildImageArea(), BorderLayout.CENTER);
        add(buildInfoArea(),  BorderLayout.SOUTH);
    }

    private JPanel buildImageArea() {
        // Área 80x80
        // Se imageUrl != null: carrega via SwingWorker, exibe quando pronto
        // Se null: placeholder com código centralizado em FONT_MONO
        // Se não coletada: alpha 0.35f aplicado no paintComponent
    }

    private JPanel buildInfoArea() {
        // Código — FONT_MONO SIZE_XS TEXT_MUTED
        // Nome truncado — FONT_REGULAR SIZE_XS
        //   TEXT_PRIMARY se coletada, TEXT_MUTED se não coletada
        // Badge "x{quantity}" em ACCENT se quantity > 1
    }
}
```

---

## 5. ProgressBarCustom

```java
// sticker_tracker/ui/components/ProgressBarCustom.java
public class ProgressBarCustom extends JPanel {

    private double percentage = 0.0;

    public ProgressBarCustom() {
        setOpaque(false);
        setPreferredSize(new Dimension(0, 8));
    }

    public void updateProgress(Progress progress) {
        this.percentage = progress.percentage();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        final var g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Track
        g2.setColor(Theme.BG_HOVER);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());

        // Fill
        final int fillWidth = (int) (getWidth() * percentage / 100);
        if (fillWidth > 0) {
            g2.setColor(Theme.ACCENT);
            g2.fillRoundRect(0, 0, fillWidth, getHeight(), getHeight(), getHeight());
        }

        g2.dispose();
    }
}
```

---

## 6. FilterBar

```java
// sticker_tracker/ui/components/FilterBar.java
public class FilterBar extends JPanel {

    public enum Filter { ALL, COLLECTED, MISSING, REPEATED }

    private Filter activeFilter = Filter.ALL;
    private Consumer<Filter> onFilterChange;

    public FilterBar() {
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.LEFT, Theme.SPACE_SM, 0));
        buildFilters();
    }

    public void setOnFilterChange(Consumer<Filter> callback) {
        this.onFilterChange = callback;
    }

    private void buildFilters() {
        for (var filter : Filter.values()) {
            final var btn = new RoundedButton(labelFor(filter), RoundedButton.Variant.SECONDARY);
            btn.addActionListener(e -> {
                activeFilter = filter;
                refreshButtonStates();
                if (onFilterChange != null) onFilterChange.accept(filter);
            });
            add(btn);
        }
    }

    private String labelFor(Filter filter) {
        return switch (filter) {
            case ALL       -> "Todas";
            case COLLECTED -> "Coletadas";
            case MISSING   -> "Faltando";
            case REPEATED  -> "Repetidas";
        };
    }

    private void refreshButtonStates() {
        // atualiza visual dos botões para refletir activeFilter
    }
}
```

---

## 7. WrapLayout

Grid que quebra linha automaticamente — Java não tem flexbox nativo.

```java
// sticker_tracker/ui/components/WrapLayout.java
//
// Baseado na implementação de Rob Camick (domínio público).
// Fonte: https://tips4java.wordpress.com/2008/11/06/wrap-layout/
//
// Extensão de FlowLayout que recalcula o layout respeitando a largura
// do container pai e quebra os componentes em múltiplas linhas.
// Necessário para o grid de StickerCards no AlbumScreen.

public class WrapLayout extends FlowLayout {

    public WrapLayout() { super(); }
    public WrapLayout(int align) { super(align); }
    public WrapLayout(int align, int hgap, int vgap) { super(align, hgap, vgap); }

    @Override
    public Dimension preferredLayoutSize(Container target) {
        return layoutSize(target, true);
    }

    @Override
    public Dimension minimumLayoutSize(Container target) {
        return layoutSize(target, false);
    }

    private Dimension layoutSize(Container target, boolean preferred) {
        // implementação que calcula altura real considerando quebras de linha
        // baseada na largura disponível do container
    }
}
```

---

## 8. Checklist de Conclusão

- [ ] `AppFrame` abre com sidebar e área de conteúdo
- [ ] Navegação entre telas via `showScreen()` funcionando
- [ ] `RoundedPanel` com antialiasing correto
- [ ] `RoundedButton` com hover nas 3 variantes
- [ ] `StickerCard` — coletada e não coletada visualmente distintas
- [ ] `ProgressBarCustom` com fill proporcional
- [ ] `FilterBar` troca estado ativo e dispara callback
- [ ] `WrapLayout` quebra cards em múltiplas linhas corretamente
- [ ] Zero valores hardcoded — tudo via `Theme.*`
