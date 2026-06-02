= Estrutura de Arquivos e Arquitetura

O projeto foi organizado em camadas, com responsabilidades bem separadas. A pasta `domain` contém as classes que representam conceitos do sistema, como coleção, seção, figurinha, progresso e figurinha do usuário. Essas classes não dependem de Swing, JDBC ou MySQL, o que mantém o domínio simples e portável.

A camada `data` contém os repositórios. Eles são responsáveis por transformar operações do sistema em consultas SQL. Métodos como `findByCollection`, `findRepeated`, `updateQuantity` e `findProgress` seguem uma nomenclatura previsível, facilitando a leitura do código e evitando mistura de verbos com o mesmo significado.

A pasta `infra` concentra detalhes de infraestrutura, como a conexão com o banco de dados. Essa separação impede que cada classe crie sua própria conexão ou conheça diretamente o driver JDBC. Já a pasta `config` guarda constantes de aplicação, navegação, coleção padrão e regras simples de cálculo, como quantidade de figurinhas por envelope e preço estimado do envelope. Essa separação evita que números de configuração fiquem espalhados pelas telas.

A camada `ui` contém a interface gráfica, dividida em componentes reutilizáveis e telas completas. As telas maiores seguem uma divisão interna entre composição visual e carregamento de dados. `HomeDataLoader` e `AlbumDataLoader` encapsulam as consultas executadas em segundo plano, enquanto `HomeScreen` e `AlbumScreen` coordenam a renderização e os estados de tela. Essa organização evita que a montagem visual fique misturada com a criação de repositórios e chamadas ao banco.

Também foi criada uma divisão por seções dentro das telas. A Home é composta por `HeaderSection`, `CollectionProgressSection`, `HomeStatsSection`, `TeamsProgressSection` e `ActivitySection`. A tela de álbum separa cabeçalho, barra de ferramentas e grade em `AlbumHeaderSection`, `AlbumToolbarSection` e `AlbumGridSection`. Essa escolha impede que cada tela vire uma classe gigante com toda a interface no mesmo arquivo. A tela principal continua responsável por orquestrar estado, filtros e carregamento, enquanto cada seção cuida de uma parte visual coesa.

No banco de dados foi usada a convenção `snake_case`, comum em SQL. No Java foram usadas classes em `PascalCase`, atributos e métodos em `camelCase` e constantes em `UPPER_SNAKE_CASE`. Essa decisão respeita o padrão de cada linguagem e reduz estranhamento para quem lê o código.

O projeto não utiliza Maven ou Gradle. As dependências externas ficam na pasta `lib`, incluindo o FlatLaf e o MySQL Connector/J. Essa escolha torna a compilação mais manual, mas também explícita: o classpath informa diretamente quais bibliotecas são necessárias para executar a aplicação.
