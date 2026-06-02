# Artigo em Typst

Este diretório contém a versão Typst do artigo acadêmico do projeto de controle de figurinhas.

## Compilar

```bash
typst compile main.typ tracker-figurinhas-artigo.pdf
```

O template usa formato A4, margens ABNT, texto justificado, recuo de primeira linha, capa acadêmica e bibliografia com o estilo `associacao-brasileira-de-normas-tecnicas`.

## Estrutura

- `main.typ`: entrada principal do artigo.
- `template.typ`, `cover.typ`, `utils.typ`: base adaptada do repositório `pump-station-analysis`.
- `sections/`: seções do artigo, separadas para facilitar escrita conjunta.
- `bibliography.bib`: referências citadas no texto.
- `assets/`: logotipo usado na capa.

O campo de divisão ainda está como placeholder para revisão com o grupo.
