# Banco de Dados

O banco de dados foi modelado para representar o álbum de forma clara e extensível. A tabela `collections` existe mesmo com apenas um álbum em uso porque o custo dessa generalização é baixo e evita que a estrutura fique presa à Copa de 2026. Caso outro álbum seja cadastrado no futuro, o modelo já possui um ponto natural para essa expansão.

O campo `code` da figurinha não foi usado como chave primária. Códigos como `BRA1` podem reaparecer em outros álbuns, por exemplo em uma Copa futura. Por isso, cada registro usa um identificador UUID como chave primária, enquanto o código da figurinha permanece como uma informação de apresentação e busca dentro da coleção.

A tabela `sections` abstrai grupos do álbum. Ela permite tratar seleções, páginas especiais, figurinhas Panini e outras divisões de forma uniforme. Assim, a interface não precisa saber se uma seção representa um time ou um grupo especial; ela apenas renderiza seções ordenadas com suas respectivas figurinhas.

A tabela `user_stickers` representa o estado da coleção do usuário. A ausência de registro indica que a figurinha não foi coletada. Um registro com `quantity >= 1` indica posse, e valores maiores que um indicam figurinhas repetidas para troca. Essa escolha evita campos booleanos redundantes como `collected` ou `repeated`, pois essas informações podem ser derivadas diretamente da quantidade.

Os arquivos SQL foram separados em `db/migrations` e `db/seed`. As migrations definem a estrutura do banco, como tabelas, chaves e constraints. Os seeds inserem dados iniciais, como coleção, seções e figurinhas. Essa separação deixa claro o que é estrutura e o que é dado inicial, além de permitir recriar ou popular o banco de maneira previsível.

A numeração sequencial dos arquivos, como `01`, `02` e `03`, também foi adotada por simplicidade. A ordem de execução fica explícita, sem depender de interpretação por data, timestamp ou ferramenta externa.
