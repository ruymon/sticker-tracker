# spec-00 — Convenções e Padrões de Código (BÍBLIA DO PROJETO)

> Este documento é a referência absoluta de estilo e qualidade do projeto Cola Aí.
> Todo código gerado deve seguir estas regras sem exceção. Consistência > preferência pessoal.

---

## 1. Nomenclatura de Métodos (Verb Families)

Cada operação tem uma família de verbos canônica. **Nunca misture verbos da mesma família.**

| Operação | Verbo canônico | Proibido |
|---|---|---|
| Leitura | `find*` | `get*`, `fetch*`, `read*`, `load*` |
| Criação/persistência | `create*` | `save*`, `build*`, `make*`, `insert*` |
| Deleção | `delete*` | `remove*`, `destroy*`, `purge*`, `drop*` |
| Validação/asserção | `assert*` | `ensure*`, `check*`, `validate*` |
| Atualização | `update*` | `edit*`, `modify*`, `change*` |

### Exemplos corretos
```java
// ✅ correto
stickerRepository.findAll();
stickerRepository.findById(id);
stickerRepository.findBySection(sectionId);
userStickerRepository.createUserSticker(sticker);
userStickerRepository.deleteUserSticker(id);
userStickerRepository.updateQuantity(id, quantity);
```

```java
// ❌ errado
stickerRepository.getAll();
stickerRepository.fetchById(id);
userStickerRepository.saveSticker(sticker);
userStickerRepository.removeSticker(id);
```

---

## 2. Convenções de Nomenclatura

### Java
| Elemento | Convenção | Exemplo |
|---|---|---|
| Classes | PascalCase | `StickerRepository`, `UserSticker` |
| Métodos | camelCase | `findBySection()`, `createUserSticker()` |
| Variáveis | camelCase | `stickerCard`, `userCollection` |
| Constantes | UPPER_SNAKE_CASE | `DEFAULT_COLLECTION_ID`, `MAX_QUANTITY` |
| Pacotes | lowercase | `sticker_tracker.data.repository` |

### Banco de dados
| Elemento | Convenção | Exemplo |
|---|---|---|
| Tabelas | snake_case, plural | `user_stickers`, `sections` |
| Colunas | snake_case | `section_id`, `image_url`, `created_at` |
| PKs | sempre `id` | `id CHAR(36)` |
| FKs | `{tabela_singular}_id` | `sticker_id`, `collection_id` |

### Nomes devem revelar intenção
```java
// ✅ correto — linguagem de domínio
Sticker sticker = stickerRepository.findById(stickerId);
List<Sticker> missingStickers = stickerRepository.findMissing();
int repeatedCount = userSticker.getQuantity() - 1;

// ❌ errado — genérico e sem intenção
Object data = repo.get(id);
List<Object> info = repo.findAll();
int value = sticker.getQty() - 1;
```

---

## 3. Qualidade de Código

- Cada método faz **uma coisa só**
- Se um método precisa de comentário para ser entendido, **refatore**
- Separe blocos de responsabilidade com **linhas em branco**

```java
// ✅ correto — linear, claro
public List<Sticker> findRepeated() {
    List<UserSticker> userStickers = userStickerRepository.findAll();

    return userStickers.stream()
        .filter(us -> us.getQuantity() > 1)
        .map(us -> stickerRepository.findById(us.getStickerId()))
        .flatMap(Optional::stream)
        .collect(Collectors.toList());
}

// ❌ errado — denso, ilegível
public List<Sticker> getRepeated() {
    return userStickerRepository.findAll().stream().filter(us->us.getQuantity()>1).map(us->stickerRepository.findById(us.getStickerId())).flatMap(Optional::stream).collect(Collectors.toList());
}
```

---

## 4. Imutabilidade e Estado

- **`final` é o padrão** para campos e variáveis locais
- Zero mutações escondidas; zero side effects dentro de expressões

```java
// ✅ correto
final String stickerId = userSticker.getStickerId();
final Optional<Sticker> sticker = stickerRepository.findById(stickerId);

// ❌ errado — reutilização de variável
String id = userSticker.getStickerId();
id = stickerRepository.findById(id).map(Sticker::getName).orElse("unknown");
```

---

## 5. Tipos e Null Safety

- **`null` nunca é retornado por método público** — use `Optional<T>`
- `Object` como tipo genérico é **proibido**
- Use `var` para inferência quando o tipo for óbvio (Java 10+)

```java
// ✅ correto
public Optional<Sticker> findById(String id) { ... }

var stickers = stickerRepository.findAll();

// ❌ errado
public Sticker findById(String id) {
    return null;
}
```

---

## 6. Streams vs Loops

Prefira streams declarativos para transformações de coleções.

```java
// ✅ preferido
List<String> repeatedCodes = userStickers.stream()
    .filter(us -> us.getQuantity() > 1)
    .map(UserSticker::getStickerId)
    .collect(Collectors.toList());

// ✅ aceitável — loop quando há side effects explícitos
for (Sticker sticker : stickers) {
    stickerRepository.createUserSticker(sticker);
}
```

---

## 7. Java Específico

- **Records** para objetos de valor imutáveis simples (Java 16+)
```java
record Progress(int collected, int total) {
    int missing() { return total - collected; }
}
```

- Getters/setters só onde necessário; prefira construtores com `final`
- Mantenha código debuggável: uma operação por linha em chains complexos

---

## 8. Estrutura de Pacotes

```
sticker_tracker/
├── domain/         — entidades puras, records, value objects
├── data/
│   └── repository/ — acesso ao banco, usa find*/create*/delete*/update*
├── infra/
│   └── db/         — DatabaseConnection (Singleton)
├── ui/
│   ├── screens/    — telas completas
│   └── components/ — componentes reutilizáveis
└── Main.java
```

---

## 9. O que nunca fazer

| Proibido | Alternativa |
|---|---|
| Retornar `null` em método público | `Optional<T>` |
| Usar `Object` como tipo | Generics ou classe de domínio |
| Misturar verbos (`get` + `find`) | Escolha um e mantenha |
| One-liners densos | Quebre em linhas legíveis |
| Nomes genéricos: `data`, `info`, `value`, `obj` | Nome de domínio |
