# Interface Gráfica

A interface gráfica foi desenvolvida manualmente com Swing, sem geradores automáticos de código. Isso atende ao requisito do projeto e garante que o grupo compreenda a construção dos componentes, eventos, layouts e estados visuais.

O arquivo `Theme.java` centraliza o sistema visual do aplicativo. Nele ficam cores, fontes, tamanhos, espaçamentos, raios de borda e dimensões compartilhadas. Essa centralização evita valores espalhados pela aplicação e permite alterar a identidade visual em um ponto único.

As fontes Geist são carregadas a partir do classpath da aplicação. Com isso, a aparência da interface não depende das fontes instaladas no computador do usuário. A aplicação tende a manter a mesma leitura visual em diferentes máquinas.

Os componentes foram pensados de forma atômica. `RoundedButton` e `RoundedPanel` não possuem regra de negócio e podem ser reutilizados em qualquer tela. `StickerCard` combina apresentação visual com dados de uma figurinha. As telas, por sua vez, apenas compõem componentes e coordenam os estados principais.

O FlatLaf foi usado como base de tema moderno para Swing. Elementos customizados, como bordas arredondadas, cards, barras de progresso e estado visual de figurinhas não coletadas, foram desenhados com `paintComponent()` e `Graphics2D`. Dessa forma, a interface preserva aparência moderna sem depender de ferramentas visuais de geração automática.
