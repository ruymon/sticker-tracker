# Desafios Técnicos

## WrapLayout: grid com quebra de linha

O Swing não possui um layout nativo equivalente ao flexbox do CSS. O `FlowLayout` padrão organiza componentes em linha, mas não resolve todos os cenários de redimensionamento dentro de um `JScrollPane`. Durante o desenvolvimento, os cards de figurinha chegaram a transbordar horizontalmente em vez de formar uma grade.

A solução foi usar `WrapLayout`, uma extensão de `FlowLayout` baseada no trabalho de Rob Camick, publicado em domínio público e muito citado na comunidade Java. O layout recalcula o tamanho preferido considerando a largura disponível, permitindo que os cards quebrem linha. Também foi necessário fazer o painel rolável acompanhar a largura do viewport, pois sem isso o layout acreditava ter espaço horizontal ilimitado.

## Cache de imagens com HashMap

Um álbum com mais de mil figurinhas pode gerar muito carregamento repetido de imagens. Se cada card recarregasse sua imagem a cada renderização ou rolagem, a interface ficaria lenta e consumiria recursos sem necessidade.

Para evitar isso, `StickerCard` mantém um cache compartilhado de imagens usando `Map<String, ImageIcon>`. A chave é a URL da imagem e o valor é o ícone já carregado e redimensionado. Quando uma imagem já está no cache, o retorno é imediato. Quando ainda não está, ela é carregada de forma assíncrona.

## SwingWorker e responsividade

O Swing renderiza a interface na Event Dispatch Thread. Se uma consulta ao banco ou carregamento de imagem for executado diretamente nessa thread, a janela pode travar até a operação terminar.

Para evitar esse problema, as telas Home e Album carregam dados com `SwingWorker`. A consulta ao banco roda em segundo plano e, quando termina, a interface é atualizada com segurança no método `done()`. Essa decisão tornou a aplicação mais responsiva e apresentável, principalmente ao abrir o álbum completo.

## Separação entre migrations e seed

Outro desafio foi organizar a criação do banco. Misturar estrutura e dados em um único script deixaria o processo menos claro. Por isso, migrations e seeds foram separados.

As migrations criam tabelas e constraints. Os seeds inserem a coleção, as seções e as figurinhas iniciais. Essa organização facilita explicar o banco, recriar o ambiente e entender quais arquivos alteram estrutura e quais apenas adicionam dados.
