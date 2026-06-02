#import "template.typ": *

#show: project.with(
  title: "Sistema Desktop para Controle de Figurinhas da Copa do Mundo 2026",
  subtitle: "",
  institution: "Instituto Mauá de Tecnologia",
  subject: (
    code: "ECM251",
    name: "Linguagem de Programação I",
  ),
  course: "Engenharia da Computação",
  authors: (
    (name: "Ruy Diniz Monteiro", collegeId: "24.00846-0"),
    (name: "Amanda Bialer", collegeId: "24.01621-7"),
  ),
  submission-date: datetime(
    year: 2026,
    month: 6,
    day: 2,
  ),
)

#include "sections/resumo.typ"

#set par(first-line-indent: 0pt)
#outline(title: [Sumário], depth: 3, indent: auto)
#pagebreak()
#set par(first-line-indent: 1.25cm)

#include "sections/introducao.typ"
#pagebreak(weak: true)

#include "sections/banco-de-dados.typ"
#pagebreak(weak: true)

#include "sections/ambiente.typ"
#pagebreak(weak: true)

#include "sections/arquitetura.typ"
#pagebreak(weak: true)

#include "sections/interface.typ"
#pagebreak(weak: true)

#include "sections/desafios-tecnicos.typ"
#pagebreak(weak: true)

#include "sections/conclusao.typ"
#pagebreak(weak: true)

#bibliography("bibliography.bib", style: "associacao-brasileira-de-normas-tecnicas")
