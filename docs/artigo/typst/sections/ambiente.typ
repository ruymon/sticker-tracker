= Ambiente

O banco de dados do projeto roda em MySQL por meio de Docker. Essa decisão melhora a reprodutibilidade: qualquer integrante do grupo consegue subir o mesmo ambiente de banco sem instalar e configurar o MySQL manualmente no computador. O Docker Compose concentra imagem, porta, volume e credenciais em um único arquivo, seguindo a proposta de isolamento descrita pela documentação do Docker @docker_docs_2026.

O aplicativo Java não foi containerizado. Como a interface foi feita em Swing, ela depende do sistema operacional para exibir janelas. Executar uma aplicação gráfica desktop dentro de um container exigiria configurações extras, como encaminhamento de display, sem trazer benefício real para o escopo do projeto.

O volume do Docker mantém os dados do MySQL entre reinicializações. Assim, o banco não é descartado toda vez que o container é desligado, preservando o comportamento esperado de uma aplicação com persistência não volátil. Quando o ambiente precisa ser recriado do zero, a separação entre migrations e seed permite apagar o volume, subir o serviço e reaplicar a estrutura e os dados iniciais em ordem.

O acesso ao banco foi feito com JDBC explícito, sem ORM. Essa escolha atende diretamente ao requisito da disciplina e também torna as consultas visíveis. O fluxo entre Java e SQL pode ser acompanhado sem camadas de abstração escondidas. A classe `DatabaseConnection` centraliza a conexão, usa o driver `com.mysql.cj.jdbc.Driver` e permite configurar host, porta, banco, usuário e senha por variáveis de ambiente, mantendo valores padrão para execução local.

Mesmo com valores padrão simples, como usuário e senha `root` para o ambiente local, o projeto também documenta as variáveis esperadas em um arquivo `.env.example`. Essa decisão ajuda outros integrantes a configurar o banco sem precisar procurar nomes de variáveis diretamente no código e evita que credenciais reais sejam versionadas por acidente.

Para evitar que o código de interface ou de domínio conheça detalhes de SQL, foi adotado o padrão Repository. Cada repositório encapsula operações de leitura e escrita relacionadas a uma entidade, mantendo o restante da aplicação mais limpo e organizado.
