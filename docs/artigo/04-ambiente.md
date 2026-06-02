# Ambiente

O banco de dados do projeto roda em MySQL através de Docker. Essa decisão melhora a reprodutibilidade: qualquer integrante do grupo consegue subir o mesmo ambiente de banco sem instalar e configurar o MySQL manualmente no computador. O Docker Compose concentra a configuração do serviço, porta, volume e credenciais.

O aplicativo Java não foi containerizado. Como a interface foi feita em Swing, ela depende do sistema operacional para exibir janelas. Executar uma aplicação gráfica desktop dentro de um container exigiria configurações extras, como encaminhamento de display, sem trazer benefício real para o escopo do projeto.

O volume do Docker mantém os dados do MySQL entre reinicializações. Assim, o banco não é descartado toda vez que o container é desligado, preservando o comportamento esperado de uma aplicação com persistência não volátil.

O acesso ao banco foi feito com JDBC explícito, sem ORM. Essa escolha atende diretamente ao requisito da disciplina e também torna as consultas visíveis. O fluxo entre Java e SQL pode ser acompanhado sem camadas de abstração escondidas.

Para evitar que o código da interface ou do domínio conheça detalhes de SQL, foi adotado o padrão Repository. Cada repositório encapsula operações de leitura e escrita relacionadas a uma entidade, mantendo o restante da aplicação mais limpo e organizado.
