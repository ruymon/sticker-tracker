# spec-08 — Documentação Acadêmica

> Documento técnico para entrega na disciplina.
> Escrito em Markdown — conversão posterior para LaTeX/ABNT.
> Foco nas decisões de engenharia, independente do código-fonte.

---

## 1. Estrutura do Documento

```
docs/artigo/
├── 00-capa.md
├── 01-resumo.md
├── 02-introducao.md
├── 03-banco-de-dados.md
├── 04-ambiente.md
├── 05-estrutura-de-arquivos.md
├── 06-interface.md
├── 07-desafios-tecnicos.md   ← seção dedicada aos desafios
├── 08-conclusao.md
└── 09-referencias.md
```

---

## 2. Conteúdo de Cada Seção

### 00 — Capa
- Título: **Cola Aí — Tracker de Figurinhas da Copa do Mundo 2026**
- Subtítulo: *"A cola que o professor não pode reprovar."*
- Instituição: Instituto Mauá de Tecnologia
- Disciplina: Programação Orientada a Objetos em Java
- Autores, data, semestre

---

### 01 — Resumo
150–200 palavras cobrindo:
- O que é o app e o problema que resolve
- Tecnologias (Java, Swing, MySQL, JDBC, Docker)
- Principais decisões arquiteturais
- Resultado entregue

---

### 02 — Introdução
- Contexto: Copa do Mundo FIFA 2026 e a febre de figurinhas no Brasil
- Problema: sem ferramenta, o colecionador não sabe o que falta nem o que pode trocar
- Solução: app desktop com rastreamento de coleção e geração de lista de trocas
- Escopo: o que o app faz e o que foi conscientemente deixado de fora

---

### 03 — Banco de Dados

**Tom:** Decisões de modelagem como escolhas de engenharia.

- Por que `collections` existe com apenas um álbum em produção — não otimizar para o caso simples quando o custo de generalização é zero
- Por que `code` não é PK — o mesmo código "BRA1" pode existir em Copa 2026 e Copa 2030; UUID como PK e `code` único por `collection_id` resolve sem complexidade extra
- A tabela `sections` como abstração — permite que times, figurinhas especiais (FWC, Panini) e regionais sejam tratados uniformemente pelo sistema
- `quantity >= 1` em `user_stickers` — ausência de registro representa "não tem"; um registro representa "tem"; quantity >= 2 representa "repetida para troca". Elimina flags booleanas redundantes; constraint no banco garante integridade
- Separação `migrations/` vs `seed/` — migrations definem estrutura (DDL) e nunca mudam; seeds inserem dados e podem ser reexecutados. Misturar tornaria difícil recriar só a estrutura
- Numeração sequencial das migrations (01, 02...) — ordem explícita e previsível; sem dependência de parsing de timestamp

---

### 04 — Ambiente

**Tom:** Justificar escolhas de infraestrutura como decisões de engenharia.

- Docker para isolamento do banco — reprodutibilidade total; qualquer pessoa clona e sobe com um comando; sem configuração manual do MySQL
- Por que não containerizar o app Java — Swing requer display do sistema operacional; rodar UI em container exige X11 forwarding sem benefício para o projeto
- Volume Docker que persiste dados entre reinicializações — banco não é efêmero
- JDBC explícito sem ORM — requisito da disciplina e benefício real: queries visíveis, fluxo de dados rastreável, zero magia escondida
- Padrão Repository encapsula JDBC — mantém código de negócio limpo sem abrir mão do controle sobre SQL

---

### 05 — Estrutura de Arquivos

**Tom:** Separação de responsabilidades como princípio, não convenção.

- Layered Architecture vs MVC clássico — quatro camadas com responsabilidades distintas: o que o sistema conhece (domain), como acessa dados (data), como se conecta à infraestrutura (infra), como se apresenta (ui)
- `domain` sem dependências externas — classes puras em Java; testáveis e portáveis
- `migrations/` dentro de `db/` — evolução do schema junto da responsabilidade que o utiliza
- Convenções de nomenclatura como contrato — `find*`, `create*`, `delete*`, `update*` em todos os repositórios; qualquer desenvolvedor sabe onde procurar sem ler a implementação
- `snake_case` no banco, `camelCase` no Java, `PascalCase` para classes — cada contexto usa a convenção da sua linguagem

---

### 06 — Interface Gráfica

**Tom:** UI como sistema de design, não lista de telas.

- Design system centralizado em `Theme.java` — tokens de cor, tipografia e espaçamento; mudança visual em um arquivo reflete em toda a interface
- Fontes customizadas (Geist) carregadas do classpath — aparência consistente em qualquer máquina, sem depender de instalação local
- Componentes atômicos e reutilizáveis — `RoundedPanel` e `RoundedButton` são átomos sem contexto de negócio; `StickerCard` combina átomos com lógica de apresentação; telas são composições de componentes
- FlatLaf como base de tema — aparência moderna sobre Swing; zero geração automática de código
- `paintComponent()` + `Graphics2D` para elementos customizados — bordas arredondadas, barra de progresso, alpha em cards não coletados; tudo desenhado explicitamente

---

### 07 — Desafios Técnicos

**Tom:** Honesto sobre o que foi difícil e como foi resolvido.

#### WrapLayout — Grid que quebra linha

O Swing não possui um layout equivalente ao `flexbox` do CSS. O `FlowLayout` padrão não quebra linha corretamente ao redimensionar a janela — os cards de figurinha transbordavam para fora do container.

A solução foi implementar `WrapLayout`, uma extensão de `FlowLayout` que recalcula o tamanho preferido considerando a largura disponível do container pai. A implementação é baseada no trabalho de Rob Camick (domínio público, amplamente referenciada na comunidade Java). O código foi adaptado e integrado ao projeto com a devida atribuição na documentação e nos comentários do arquivo.

**Por que não existe nativamente:** o Swing foi projetado antes de layouts fluidos serem comuns em interfaces desktop. Soluções modernas como JavaFX têm `FlowPane` com comportamento equivalente.

---

#### Cache de Imagens com HashMap

Com 1034 figurinhas no grid, carregar cada imagem toda vez que o usuário faz scroll tornava a interface inutilizável — lag visível e consumo desnecessário de I/O.

A solução foi um `HashMap<String, ImageIcon>` compartilhado entre os cards do `AlbumScreen`. Na primeira vez que uma imagem é requisitada, ela é carregada de forma assíncrona via `SwingWorker` e armazenada no cache. Nas requisições seguintes, o retorno é imediato.

`HashMap` não foi ensinado explicitamente na disciplina, mas é uma estrutura de dados fundamental para qualquer linguagem. A decisão de usá-lo veio da necessidade prática: sem cache a tela era inutilizável; com cache o scroll ficou fluido. A lógica é simples — chave é a URL da imagem, valor é o `ImageIcon` já carregado.

---

#### SwingWorker — Threading na UI

O Swing tem uma regra fundamental: toda operação de I/O (banco de dados, rede, disco) que ocorre na EDT (Event Dispatch Thread — a thread que renderiza a UI) trava a interface durante a operação.

Na prática: sem `SwingWorker`, abrir o AlbumScreen com 1034 figurinhas travaria a janela por vários segundos. Com `SwingWorker`, a query ao banco roda em uma thread separada e a UI continua responsiva; quando os dados chegam, o método `done()` é chamado de volta na EDT para atualizar os componentes com segurança.

`SwingWorker` não foi ensinado na disciplina, mas sua necessidade é inevitável em qualquer app com banco de dados e interface gráfica. A alternativa — não usá-lo — produziria uma UI travada e inapresentável.

---

#### Separação Migrations / Seed

O padrão ensinado na disciplina não aborda evolução de schema de banco de dados. O projeto adotou a convenção de separar arquivos DDL (que definem estrutura) de arquivos DML com dados iniciais (seed).

A decisão de rodar os SQLs manualmente em vez de automatizá-los em Java foi intencional: mantém o JDBC do app focado em operações de negócio, torna o processo de setup transparente e auditável, e evita adicionar complexidade de runtime para um problema que ocorre uma única vez.

---

### 08 — Conclusão

- Síntese das decisões e o que elas proporcionaram
- O que o projeto demonstra além do requisito mínimo
- Limitações da versão atual (single-user, sem sync, sem login)
- Evoluções naturais: múltiplos usuários, troca via rede, suporte a outros álbuns

---

### 09 — Referências

Formato ABNT NBR 6023:
- Documentação Java SE 17 — Oracle
- FlatLaf — FormDev Software GmbH
- MySQL Connector/J 8.x — Oracle
- Docker Documentation — Docker Inc.
- Catálogo Panini WC 2026 — github.com/danieltartaro/sticker-swap
- WrapLayout — Rob Camick, tips4java.wordpress.com, 2008

---

## 3. Apresentação PowerPoint

### Estrutura dos Slides

| # | Título | Foco |
|---|---|---|
| 1 | Abertura | Logo Cola Aí + slogan + nomes |
| 2 | O Problema | A dor do colecionador em uma frase |
| 3 | A Solução | O que o app faz em 3 bullets |
| 4 | Demo — HomeScreen | Screenshot com dados reais |
| 5 | Demo — AlbumScreen | Screenshot com grid |
| 6 | Banco de Dados | Diagrama ER + decisões de modelagem |
| 7 | Ambiente | Docker + JDBC — por que essas escolhas |
| 8 | Estrutura de Arquivos | Diagrama de camadas |
| 9 | Interface | Design system + componentes atômicos |
| 10 | Desafios Técnicos | WrapLayout, HashMap, SwingWorker |
| 11 | Conclusão | Aprendizados + próximos passos |

### Guia visual
- Fundo `#0A0A0A` — consistente com o app
- Fonte Geist — mesma do app
- Destaque em verde `#22C55E`
- Máximo 4 bullets por slide
- Screenshots grandes, sem texto em cima

---

## 4. Checklist de Conclusão

- [ ] Todos os `.md` do artigo criados
- [ ] Seção 07 cobre WrapLayout, HashMap, SwingWorker e Migrations com honestidade técnica
- [ ] Cada seção técnica descreve decisões, não implementação
- [ ] Nenhuma seção depende de código para fazer sentido
- [ ] PowerPoint com 11 slides conforme estrutura
- [ ] Referências em formato ABNT NBR 6023
- [ ] WrapLayout de Rob Camick referenciado corretamente
