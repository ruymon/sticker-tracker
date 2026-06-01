# spec-07 — AlbumScreen

> Grid completo do álbum com filtros, busca e gerenciamento de figurinhas.
> É aqui que o usuário marca o que tem e quanto tem de cada figurinha.

---

## 1. Layout Geral

```
┌─────────────────────────────────────────────────────────┐
│  Álbum  Copa do Mundo 2026                   95 / 1034  │  ← Header
│  [Todas] [Coletadas] [Faltando] [Repetidas]  [🔍 busca] │  ← Toolbar
├─────────────────────────────────────────────────────────┤
│  🇧🇷 Brazil (16/20)                                      │  ← Section Header
│  ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐         │
│  │BRA1  │ │BRA2  │ │BRA3 ░│ │BRA4 ░│ │...   │  ...    │  ← StickerCards
│  └──────┘ └──────┘ └──────┘ └──────┘ └──────┘         │
│  🇲🇽 Mexico (8/20)                                       │
│  ┌──────┐ ┌──────┐  ...                                │
└─────────────────────────────────────────────────────────┘
```

Cards opacos = não coletadas.

---

## 2. Carregamento de Dados

```java
private void loadAlbumData() {
    final var worker = new SwingWorker<AlbumData, Void>() {

        @Override
        protected AlbumData doInBackground() {
            final var sections  = sectionRepository.findByCollection(DEFAULT_COLLECTION_ID);
            final var stickers  = stickerRepository.findByCollection(DEFAULT_COLLECTION_ID);
            final var collected = userStickerRepository.findAll();
            return new AlbumData(sections, stickers, collected);
        }

        @Override
        protected void done() {
            try {
                renderAlbum(get());
            } catch (Exception e) {
                showErrorState();
            }
        }
    };
    worker.execute();
}

private record AlbumData(
    List<Section> sections,
    List<Sticker> stickers,
    List<UserSticker> collected
) {}
```

---

## 3. Cache de Imagens

Imagens de figurinhas carregadas sob demanda e cacheadas em memória.
Sem cache: scroll recarrega a mesma imagem centenas de vezes — UI laga.

```java
// Cache compartilhado entre todos os StickerCards
private static final Map<String, ImageIcon> IMAGE_CACHE = new HashMap<>();

private void loadImageAsync(String imageUrl, JLabel imageLabel) {
    if (IMAGE_CACHE.containsKey(imageUrl)) {
        imageLabel.setIcon(IMAGE_CACHE.get(imageUrl));
        return;
    }

    final var worker = new SwingWorker<ImageIcon, Void>() {
        @Override
        protected ImageIcon doInBackground() throws Exception {
            final var url   = new URL(imageUrl);
            final var image = ImageIO.read(url);
            final var scaled = image.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        }

        @Override
        protected void done() {
            try {
                final var icon = get();
                IMAGE_CACHE.put(imageUrl, icon);
                imageLabel.setIcon(icon);
            } catch (Exception e) {
                // mantém placeholder — falha silenciosa
            }
        }
    };
    worker.execute();
}
```

---

## 4. Grid por Seção

```java
private JPanel buildAlbumGrid(AlbumData data) {
    final var container = new JPanel();
    container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
    container.setBackground(Theme.BG_PRIMARY);

    // Mapas para lookup rápido O(1)
    final var collectedMap = data.collected().stream()
        .collect(Collectors.toMap(UserSticker::getStickerId, us -> us));

    for (var section : data.sections()) {
        final var sectionStickers = data.stickers().stream()
            .filter(s -> s.getSectionId().equals(section.getId()))
            .collect(Collectors.toList());

        container.add(buildSectionHeader(section, sectionStickers, collectedMap));
        container.add(buildSectionGrid(sectionStickers, collectedMap));
    }

    return container;
}

private JPanel buildSectionGrid(List<Sticker> stickers, Map<String, UserSticker> collectedMap) {
    final var panel = new JPanel(new WrapLayout(FlowLayout.LEFT, Theme.SPACE_SM, Theme.SPACE_SM));
    panel.setBackground(Theme.BG_PRIMARY);

    for (var sticker : stickers) {
        final var userSticker = collectedMap.get(sticker.getId());
        final boolean collected = userSticker != null;
        final int quantity = collected ? userSticker.getQuantity() : 0;

        final var card = new StickerCard(sticker, collected, quantity);
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openStickerDialog(sticker, Optional.ofNullable(userSticker));
            }
        });
        panel.add(card);
    }

    return panel;
}
```

---

## 5. Dialog de Gerenciamento

```java
private void openStickerDialog(Sticker sticker, Optional<UserSticker> userSticker) {
    final var dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), true);
    dialog.setTitle(sticker.getCode() + " — " + sticker.getName());
    dialog.setSize(320, 200);
    dialog.setLocationRelativeTo(this);

    final int currentQty = userSticker.map(UserSticker::getQuantity).orElse(0);
    final var spinner = new JSpinner(new SpinnerNumberModel(currentQty, 0, 99, 1));

    final var saveBtn = new RoundedButton("Salvar", RoundedButton.Variant.PRIMARY);
    saveBtn.addActionListener(e -> {
        final int newQty = (int) spinner.getValue();

        if (newQty == 0 && userSticker.isPresent()) {
            userStickerRepository.deleteUserSticker(userSticker.get().getId());
        } else if (newQty > 0 && userSticker.isEmpty()) {
            userStickerRepository.createUserSticker(sticker.getId());
            if (newQty > 1) {
                // updateQuantity direto se quantidade > 1
            }
        } else if (newQty > 0) {
            // UPDATE quantity para o novo valor
        }

        dialog.dispose();
        refreshAlbum();
    });

    // layout do dialog com spinner e botões
    dialog.setVisible(true);
}
```

---

## 6. Filtros e Busca

```java
private List<Sticker> applyFilter(
    List<Sticker> all,
    Map<String, UserSticker> collectedMap,
    FilterBar.Filter filter,
    String searchTerm
) {
    var filtered = switch (filter) {
        case ALL       -> all;
        case COLLECTED -> all.stream()
            .filter(s -> collectedMap.containsKey(s.getId()))
            .collect(Collectors.toList());
        case MISSING   -> all.stream()
            .filter(s -> !collectedMap.containsKey(s.getId()))
            .collect(Collectors.toList());
        case REPEATED  -> all.stream()
            .filter(s -> collectedMap.containsKey(s.getId())
                      && collectedMap.get(s.getId()).isRepeated())
            .collect(Collectors.toList());
    };

    if (!searchTerm.isBlank()) {
        final var term = searchTerm.toLowerCase();
        filtered = filtered.stream()
            .filter(s -> s.getCode().toLowerCase().contains(term)
                      || s.getName().toLowerCase().contains(term))
            .collect(Collectors.toList());
    }

    return filtered;
}
```

### Debounce na busca (evita refiltrar a cada tecla)

```java
private javax.swing.Timer searchDebounce;

private void onSearchChanged(String term) {
    if (searchDebounce != null && searchDebounce.isRunning()) {
        searchDebounce.stop();
    }
    searchDebounce = new javax.swing.Timer(300, e -> refreshGrid(term));
    searchDebounce.setRepeats(false);
    searchDebounce.start();
}
```

---

## 7. Estados da Tela

| Estado | Comportamento |
|---|---|
| Carregando | Label "Carregando álbum..." |
| Filtro sem resultado | "Nenhuma figurinha para este filtro" |
| Busca sem resultado | "Nenhum resultado para '{termo}'" |
| Normal | Grid completo por seção |

---

## 8. Checklist de Conclusão

- [ ] Grid exibe todas as seções em display_order
- [ ] Flag da seção carrega de `assets/flags/{prefix}.png`
- [ ] Cards coletados vs não coletados visualmente distintos
- [ ] WrapLayout quebra cards corretamente ao redimensionar janela
- [ ] Cache de imagens — scroll suave sem lag
- [ ] Filtros funcionam para os 4 estados
- [ ] Busca com debounce de 300ms
- [ ] Dialog de quantidade abre ao clicar no card
- [ ] Salvar atualiza banco e refresca o grid
- [ ] HomeScreen atualiza após mudança no álbum
