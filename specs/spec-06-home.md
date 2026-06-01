# spec-06 — HomeScreen

> Tela principal do app. Progresso da coleção, recentes e repetidas.
> Primeira tela exibida ao abrir o app.

---

## 1. Layout Geral

```
┌─────────────────────────────────────────────────────┐
│  Cola Aí          A cola que o professor não pode.. │  ← Header
├─────────────────────────────────────────────────────┤
│  ┌───────────────────────────────────────────────┐  │
│  │  Sua Coleção                          9.2%    │  │  ← Progress Card
│  │  ████░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░    │  │
│  │  95 coletadas · 939 faltando                  │  │
│  │  [▼ Ver por seção]                            │  │
│  └───────────────────────────────────────────────┘  │
│                                                     │
│  Adicionadas Recentemente                           │
│  ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐    │
│  │BRA14 │ │MEX3  │ │ARG7  │ │FRA1  │ │...   │    │
│  └──────┘ └──────┘ └──────┘ └──────┘ └──────┘    │
│                                                     │
│  Repetidas (12 figurinhas · 18 disponíveis)        │
│  │ MEX3  Johan Vasquez          x2              │  │
│  │ BRA14 Vinicius Júnior        x3              │  │
│  [📋 Copiar lista para WhatsApp]                    │
└─────────────────────────────────────────────────────┘
```

---

## 2. Carregamento de Dados

Dados carregados via `SwingWorker` — nunca bloquear a EDT.

```java
private void loadData() {
    final var worker = new SwingWorker<HomeData, Void>() {

        @Override
        protected HomeData doInBackground() {
            final var progress = stickerRepository.findProgress(DEFAULT_COLLECTION_ID);
            final var bySection = stickerRepository.findProgressBySection(DEFAULT_COLLECTION_ID);
            final var recent   = userStickerRepository.findRecent(10);
            final var repeated = userStickerRepository.findRepeated();
            final var sections = sectionRepository.findByCollection(DEFAULT_COLLECTION_ID);
            return new HomeData(progress, bySection, recent, repeated, sections);
        }

        @Override
        protected void done() {
            try {
                updateUI(get());
            } catch (Exception e) {
                showErrorState();
            }
        }
    };
    worker.execute();
}

private record HomeData(
    Progress progress,
    Map<String, Progress> bySection,
    List<UserSticker> recent,
    List<UserSticker> repeated,
    List<Section> sections
) {}
```

---

## 3. Progress Card (expansível)

```java
private RoundedPanel buildProgressCard() {
    // Linha superior: "Sua Coleção" à esquerda + "9.2%" à direita em ACCENT
    // ProgressBarCustom
    // "95 coletadas · 939 faltando" em TEXT_SECONDARY
    // Botão ghost "Ver por seção ▼" que expande/colapsa o painel de seções
}
```

Painel de seções expandido:
```
Brazil      ████████░░  16/20  80%
Mexico      ████░░░░░░   8/20  40%
...
```
Cada linha tem um `ProgressBarCustom` com altura 4px.

---

## 4. Seção Recentes

```java
private JPanel buildRecentSection() {
    // Título "Adicionadas Recentemente" — FONT_SEMIBOLD SIZE_BASE
    // JScrollPane horizontal com StickerCards compactos (80x110)
    // userStickerRepository.findRecent(10)
    // Estado vazio: "Nenhuma figurinha ainda. Vá ao Álbum para começar."
}
```

---

## 5. Seção Repetidas + CTA WhatsApp

```java
private JPanel buildRepeatedSection() {
    // Título "Repetidas (X figurinhas · Y disponíveis)"
    // Lista: código | nome | badge xN
    // Máximo 5 itens visíveis, scroll se mais
    // Botão PRIMARY "Copiar lista para WhatsApp"
}

private void copyWhatsAppMessage() {
    final var repeated = userStickerRepository.findRepeated();

    final var codes = repeated.stream()
        .map(us -> stickerRepository.findById(us.getStickerId()))
        .flatMap(Optional::stream)
        .map(Sticker::getCode)
        .collect(Collectors.joining(", "));

    final var message = "Tenho essas repetidas da Copa 2026: " + codes
        + "\nMe chama para trocar! 🔁";

    final var clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(new StringSelection(message), null);

    // Feedback: botão muda para "✓ Copiado!" por 2 segundos via javax.swing.Timer
}
```

---

## 6. Estados da Tela

| Estado | Comportamento |
|---|---|
| Carregando | Label "Carregando..." central |
| Vazio | Mensagem de boas-vindas + instrução para ir ao Álbum |
| Normal | Layout completo |
| Erro | Mensagem de erro + botão "Tentar novamente" |

---

## 7. Checklist de Conclusão

- [ ] Progress card com percentual e barra
- [ ] Expansão por seção mostra progresso individual
- [ ] Recentes em scroll horizontal com StickerCards
- [ ] Lista de repetidas com badge de quantidade
- [ ] Botão WhatsApp copia mensagem formatada
- [ ] Feedback visual "Copiado!" por 2 segundos
- [ ] Estado vazio exibido corretamente
- [ ] SwingWorker — UI não trava durante carregamento
