= Interface Gráfica

A interface gráfica foi desenvolvida manualmente com Swing, sem geradores automáticos de código. Isso atende ao requisito do projeto e garante que a construção dos componentes, eventos, layouts e estados visuais seja compreendida pelo grupo. O FlatLaf foi usado como base visual moderna para Swing @flatlaf_2026, e a configuração global da aplicação é aplicada antes da criação da janela principal.

O arquivo `Theme.java` centraliza o sistema visual do aplicativo. Nele ficam cores, fontes, tamanhos, espaçamentos, raios de borda e dimensões compartilhadas. A versão atual utiliza uma paleta clara, com fundo principal branco, superfícies secundárias em cinza claro e verde `#22C55E` como cor de destaque. Essa centralização evita valores espalhados pela aplicação e permite alterar a identidade visual em um ponto único.

As fontes Geist são carregadas a partir do classpath da aplicação. Com isso, a aparência da interface não depende das fontes instaladas no computador do usuário. A aplicação tende a manter a mesma leitura visual em diferentes máquinas, preservando a identidade gráfica do projeto.

Os componentes foram pensados de forma atômica. `RoundedButton` e `RoundedPanel` não possuem regra de negócio e podem ser reutilizados em qualquer tela. `ProgressBarCustom`, `Badge`, `StatCard`, `PageHeader`, `FilterBar`, `RoundedTextField`, `FlagBadge` e `StickerCard` concentram padrões visuais recorrentes. As telas, por sua vez, compõem esses componentes e coordenam os estados principais.

Na tela inicial, o sistema apresenta o progresso geral da coleção, estatísticas de coletadas, faltantes e repetidas, estimativa de envelopes, gasto estimado, progresso por times e atividade recente. A Home foi organizada como um painel de acompanhamento, não apenas como uma lista de figurinhas. Na tela de álbum, o usuário visualiza as seções ordenadas, busca por código ou nome, filtra por status e altera a quantidade de cada figurinha por meio de um diálogo próprio. Essa separação aproxima a Home de uma visão gerencial e o Álbum de uma área de operação.

#set par(first-line-indent: 0pt)
#figure(
  image("../assets/app-visao-geral.png", width: 100%),
  caption: [Tela Visão Geral com progresso do álbum, métricas, times e atividade recente.]
)

#figure(
  image("../assets/app-album-filtros.png", width: 100%),
  caption: [Tela Álbum com filtros por status, busca e grade de figurinhas agrupadas por seção.]
)

#figure(
  image("../assets/app-album-dialogo-quantidade.png", width: 100%),
  caption: [Diálogo de quantidade aberto sobre a tela do Álbum, com controles de incremento e decremento.]
)
#set par(first-line-indent: 1.25cm)

A divisão entre `sections` e `components` foi essencial para controlar o crescimento da interface. Os componentes compartilhados resolvem problemas repetidos de layout e estilo, como cartões, botões, badges, campos e barras de progresso. Já as seções organizam partes maiores da tela, como cabeçalho, barra de filtros, grade do álbum, estatísticas e atividade. Assim, uma mudança visual em um botão ou card não exige editar cada tela, e uma mudança de composição de tela não altera a implementação interna dos componentes.

Elementos customizados, como bordas arredondadas, cartões, barras de progresso e opacidade de figurinhas não coletadas, foram desenhados com `paintComponent()` e `Graphics2D`. Dessa forma, a interface preserva uma aparência mais cuidada sem depender de ferramentas visuais de geração automática.
