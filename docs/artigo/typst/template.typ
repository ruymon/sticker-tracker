#import "cover.typ": cover_page

#let project(
  logo: "assets/logo-imt-black.svg",
  institution: "Instituto Mauá de Tecnologia",
  subject: (),
  title: "",
  subtitle: "",
  course: "[preencher curso]",
  authors: (),
  submission-date: none,
  language: "pt",
  body,
) = {
  set document(
    author: authors.map(author => author.name),
    title: title,
    date: submission-date,
  )

  set text(font: "Libertinus Serif", lang: language, size: 12pt)
  set page(
    paper: "a4",
    margin: (left: 3cm, top: 3cm, right: 2cm, bottom: 2cm),
    numbering: "1",
    number-align: right,
  )
  set par(
    leading: 1.5em,
    spacing: 0.65em,
    first-line-indent: 1.25cm,
    justify: true,
  )
  set heading(numbering: "1.1")

  show heading.where(level: 1): set block(above: 18pt, below: 12pt)
  show heading.where(level: 1): set text(size: 12pt, weight: "bold")
  show heading.where(level: 2): set block(above: 14pt, below: 8pt)
  show heading.where(level: 2): set text(size: 12pt, weight: "bold")
  show heading.where(level: 3): set block(above: 10pt, below: 6pt)
  show heading.where(level: 3): set text(size: 12pt, weight: "bold")
  show raw: set text(font: "Libertinus Mono", size: 10pt)
  show link: set text(fill: black)
  show figure.caption: set text(size: 10pt)

  cover_page(
    logo: logo,
    institution: institution,
    subject: subject,
    title: title,
    subtitle: subtitle,
    course: course,
    division: division,
    authors: authors,
    submission-date: submission-date,
    language: language,
  )

  set page(numbering: "1")
  counter(page).update(1)

  body
}
