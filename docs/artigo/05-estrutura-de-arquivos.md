# Estrutura de Arquivos

O projeto foi organizado em camadas, com responsabilidades bem separadas. A pasta `domain` contém as classes que representam conceitos do sistema, como coleção, seção, figurinha, progresso e figurinha do usuário. Essas classes não dependem de Swing, JDBC ou MySQL.

A camada `data` contém os repositórios. Eles são responsáveis por transformar operações do sistema em consultas SQL. Métodos como `findByCollection`, `findRepeated`, `updateQuantity` e `findProgress` seguem uma nomenclatura previsível, facilitando a leitura do código.

A pasta `infra` concentra detalhes de infraestrutura, como a conexão com o banco de dados. Essa separação impede que cada classe crie sua própria conexão ou conheça diretamente variáveis de ambiente e driver JDBC.

A camada `ui` contém a interface gráfica. Ela foi dividida em componentes reutilizáveis e telas. Componentes como `RoundedButton`, `RoundedPanel`, `ProgressBarCustom`, `StickerCard`, `FilterBar` e `EmptyState` são usados por diferentes telas. As telas maiores seguem um padrão por pasta, com `HomeScreen` e `AlbumScreen` separados de seus dados, loaders e seções visuais.

No banco de dados foi usada a convenção `snake_case`, comum em SQL. No Java foram usadas classes em `PascalCase` e atributos/métodos em `camelCase`. Essa decisão respeita o padrão de cada linguagem e reduz estranhamento para quem lê o código.
