= Desafios Técnicos

O projeto poderia ter sido muito mais simples. Uma implementação mínima em Swing, com poucas classes e consultas diretas ao banco, provavelmente atenderia parte dos requisitos. No entanto, o grupo se empolgou durante o desenvolvimento e passou a tentar uma solução progressivamente mais ambiciosa: primeiro uma estrutura de pastas mais organizada, depois uma arquitetura em camadas, em seguida uma interface visualmente mais cuidada e, por fim, um painel com métricas e estados de uso.

Muito do que foi implementado não era conhecido previamente pelos integrantes no ecossistema Java. Ainda assim, a experiência com outras linguagens e frameworks ajudou a formular boas perguntas: como separar dados e interface, como criar componentes reaproveitáveis, como evitar que uma tela vire um arquivo enorme, como carregar dados sem travar a UI e como reproduzir no Swing alguns comportamentos já vistos em outras tecnologias. O desenvolvimento acabou sendo guiado por pesquisa, leitura de documentação, buscas pontuais e uso de agentes para encontrar equivalentes em Java.

== WrapLayout: grid com quebra de linha

O Swing não possui um layout nativo equivalente ao `flexbox` do CSS. O `FlowLayout` padrão organiza componentes em linha, mas não resolve todos os cenários de redimensionamento dentro de um `JScrollPane`. Durante o desenvolvimento, os cards de figurinha chegaram a transbordar horizontalmente em vez de formar uma grade.

A solução foi usar `WrapLayout`, uma extensão de `FlowLayout` baseada no trabalho de Rob Camick @camick_wrap_layout_2008. O layout recalcula o tamanho preferido considerando a largura disponível, permitindo que os cards quebrem linha. Também foi necessário fazer o painel rolável acompanhar a largura do viewport, pois sem isso o layout acreditava ter espaço horizontal ilimitado.

A motivação para procurar essa solução veio de experiências anteriores com interfaces que reaproveitam células ou reorganizam itens conforme o tamanho da tela, como listas e grades em outros frameworks. O grupo não sabia de início qual seria o equivalente em Java, mas sabia qual comportamento procurar. Esse foi um padrão recorrente do projeto: reconhecer o problema por experiência prévia e pesquisar a solução específica para Swing.

== SwingWorker e responsividade

O Swing renderiza a interface na Event Dispatch Thread. Se uma consulta ao banco for executada diretamente nessa thread, a janela pode travar até a operação terminar. Esse problema aparece com mais força no álbum completo, pois a tela precisa consultar coleção, seções, figurinhas e quantidades do usuário.

Para evitar travamentos, as telas Home e Álbum carregam dados por meio de `SwingWorker`. A consulta ao banco roda em segundo plano e, quando termina, a interface é atualizada com segurança no método `done()`. A mesma ideia aparece no salvamento da quantidade, fazendo com que a alteração no banco não bloqueie a interação principal.

== Atualização local e percepção de desempenho

Um ponto percebido durante os testes foi que carregar dados em segundo plano não resolve, sozinho, todos os problemas de sensação de lentidão. Inicialmente, ao alterar a quantidade de uma figurinha, o sistema salvava no banco, recarregava os dados do álbum e só depois reconstruía a grade. Mesmo com `SwingWorker`, a interface parecia atrasada, pois o usuário fechava o diálogo e ainda via o estado antigo por alguns instantes.

A solução foi atualizar primeiro o estado local da tela e redesenhar a grade imediatamente, enquanto o salvamento no MySQL acontece em segundo plano. Caso o salvamento falhe, o estado anterior é restaurado e a tela de erro é exibida. Essa estratégia melhora a percepção de resposta sem abandonar a persistência no banco. O desafio mostrou que desempenho em interface não depende apenas de evitar travamentos, mas também de quando a aplicação comunica visualmente que uma ação foi concluída.

== Quantidade como estado único

Outro desafio foi representar coletadas, faltantes e repetidas sem multiplicar flags no banco. A solução foi tratar a quantidade como estado único. Quando não existe registro em `user_stickers`, a figurinha é faltante. Quando existe registro com quantidade um, ela foi coletada. Quando a quantidade é maior que um, a diferença representa as repetidas disponíveis.

Essa decisão simplifica filtros, progresso e cálculo de métricas. Ao mesmo tempo, exige cuidado no decremento e no diálogo de edição: se o usuário reduz a quantidade para zero, o registro deve ser removido, e não atualizado para um valor inválido.

== Separação entre migrations e seed

Também foi necessário organizar a criação do banco. Misturar estrutura e dados em um único script deixaria o processo menos claro. Por isso, migrations e seeds foram separados. As migrations criam tabelas e restrições, enquanto os seeds inserem a coleção, as seções e as figurinhas iniciais. Essa organização facilita explicar o banco, recriar o ambiente e entender quais arquivos alteram estrutura e quais apenas adicionam dados.

== Interface em Swing sem gerador visual

Construir a interface manualmente foi outro ponto trabalhoso. O Swing permite bastante controle, mas exige lidar diretamente com layouts, tamanhos, bordas, eventos e pintura. Para evitar um código monolítico, o grupo separou elementos pequenos em componentes compartilhados e blocos maiores em seções de tela. Essa separação tornou possível continuar refinando a interface sem transformar `HomeScreen` e `AlbumScreen` em classes gigantes.
