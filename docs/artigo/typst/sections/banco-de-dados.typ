#let entity(name, fields) = rect(width: 100%, inset: 6pt, stroke: 0.6pt, radius: 2pt)[
  #stack(
    spacing: 2pt,
    text(name, weight: "bold", size: 9pt),
    line(length: 100%, stroke: 0.4pt),
    ..fields.map(field => text(field, size: 7.4pt)),
  )
]

#let relation(label) = align(center)[
  #text(size: 8.5pt, fill: luma(40))[#label]
]

= Banco de Dados

O banco de dados foi modelado para representar o álbum de forma clara e extensível. A decisão principal foi não tratar a Copa de 2026 como um caso especial amarrado ao código. Mesmo que a interface atual exiba apenas esse álbum, a estrutura foi pensada para permitir outros álbuns no futuro, inclusive outras Copas, sem refazer a modelagem central.

#set par(first-line-indent: 0pt)
#figure(
  grid(
    columns: (1fr),
    rows: auto,
    gutter: 4pt,
    entity("collections", (
      "id CHAR(36) PK",
      "name VARCHAR(255)",
      "created_at / updated_at",
    )),
    relation("1 álbum possui N seções"),
    entity("sections", (
      "id CHAR(36) PK",
      "collection_id CHAR(36) FK",
      "prefix, name, type",
      "flag_asset, display_order",
    )),
    relation("1 seção agrupa N figurinhas"),
    entity("stickers", (
      "id CHAR(36) PK",
      "collection_id CHAR(36) FK",
      "section_id CHAR(36) FK",
      "code, number, name",
      "image_url",
      "UNIQUE(code, collection_id)",
    )),
    relation("1 figurinha possui 0 ou 1 estado do usuário"),
    entity("user_stickers", (
      "id CHAR(36) PK",
      "sticker_id CHAR(36) FK UNIQUE",
      "quantity INT CHECK(quantity >= 1)",
      "created_at / updated_at",
    )),
  ),
  caption: [Modelagem relacional adotada para álbuns, seções, figurinhas e estado da coleção.]
)
#set par(first-line-indent: 1.25cm)

A tabela `collections` existe mesmo com apenas um álbum em uso, pois o custo dessa generalização é baixo e evita que toda a estrutura fique presa à Copa de 2026. Se outro álbum for cadastrado, como uma Copa futura, a nova coleção passa a reutilizar as mesmas tabelas de seções, figurinhas e progresso do usuário. Essa granularidade mantém o sistema simples no uso atual, mas não fecha portas para expansão.

O campo `code` da figurinha também não foi usado como chave primária. Códigos como `BRA1`, `FWC1` ou `MEX14` são identificadores visuais do álbum, não identificadores globais do sistema. Se dois álbuns existirem em paralelo, o código `BRA1` pode aparecer em mais de uma coleção. Por isso, cada figurinha possui um `id` próprio em formato UUID, enquanto `code` é único apenas em conjunto com `collection_id`. Essa escolha evita colisões futuras sem sacrificar a busca por código na interface.

A tabela `sections` abstrai as divisões do álbum. Ela permite tratar seleções, páginas especiais, figurinhas Panini, histórico da Copa e outros agrupamentos de maneira uniforme. O campo `type` diferencia seções do tipo `team`, `special` e `regional`, enquanto `display_order` controla a ordem de exibição. Assim, a interface não precisa saber se uma seção representa uma seleção ou uma página especial; ela apenas renderiza grupos ordenados com suas respectivas figurinhas.

A tabela `user_stickers` representa o estado da coleção do usuário. A ausência de registro indica que a figurinha não foi coletada. Um registro com `quantity = 1` indica posse, e valores maiores que um indicam figurinhas repetidas para troca. Essa modelagem evita campos booleanos redundantes, como `collected` ou `repeated`, porque essas informações podem ser derivadas diretamente da quantidade. A restrição `quantity >= 1` mantém a integridade no banco e reforça essa regra: quantidade zero não é um estado persistido, mas a ausência do registro.

Os arquivos SQL foram separados em `db/migrations` e `db/seed`. As migrations definem a estrutura do banco, como tabelas, chaves, índices e restrições. Os seeds inserem dados iniciais, como coleção, seções e figurinhas. Essa separação deixa claro o que é estrutura e o que é dado de carga inicial, além de permitir recriar o ambiente de forma previsível. A numeração sequencial dos arquivos, como `01`, `02` e `03`, também foi adotada por simplicidade: a ordem de execução fica explícita e não depende de interpretação por data, timestamp ou ferramenta externa.

O seed principal cobre o recorte usado pelo projeto, com 52 seções e 1034 figurinhas. A coleta e conferência dos dados passou por mais de uma fonte. Parte das informações foi levantada no LastSticker, que mantém página de checklist da coleção Panini FIFA World Cup 2026 @laststicker_panini_2026. Depois, a lista de seleções e dados de times também foi comparada com o arquivo aberto `worldcup.teams.json` do projeto OpenFootball @openfootball_worldcup_2026. O resultado foi tratado como dado inicial do sistema, não como regra de negócio fixa.

A construção do seed também exigiu tratamento de qualidade de dados. Alguns nomes de figurinhas apresentaram caracteres incorretos por problema de codificação, e os arquivos de bandeiras precisaram ser comparados com os códigos ISO usados nas seções. Em outros momentos, foi necessário decidir se uma informação deveria ficar no banco, como o código FIFA, ou como recurso visual da interface, como o nome do asset de bandeira. Essa etapa mostrou que seed não é apenas inserir dados no banco: também envolve conferência, normalização e decisões sobre a fronteira entre dado persistido e recurso da aplicação.
