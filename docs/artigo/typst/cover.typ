#import "utils.typ": *

#let cover_page(
  logo: "assets/logo-imt-black.svg",
  institution: "Instituto Mauá de Tecnologia",
  subject: (),
  title: "",
  subtitle: "",
  course: "[preencher curso]",
  division: "[preencher divisão]",
  authors: (),
  submission-date: none,
  language: "pt",
) = {
  set page(
    paper: "a4",
    margin: (left: 0mm, right: 0mm, top: 0mm, bottom: 0mm),
    numbering: none,
  )

  if logo != none {
    place(
      top + right,
      dx: -22mm,
      dy: 16mm,
      image(logo, width: 112pt),
    )
  }

  pad(
    left: 34mm,
    top: 36mm,
    right: 34mm,
    stack(
      spacing: 7mm,
      align(center, text(institution, size: 12pt, weight: "bold")),
      align(center, text("Curso: " + course, size: 11pt)),
      align(center, text("Disciplina: " + subject.name, size: 11pt)),
      align(center, text("Divisão: " + division, size: 11pt)),
      v(32mm),
      align(center, text(title, size: 18pt, weight: "bold")),
      if subtitle != "" {
        align(center, emph(text(subtitle, size: 12pt)))
      },
      v(20mm),
      align(center, stack(
        spacing: 3mm,
        ..authors.map(author => {
          let registration = if author.collegeId == "" { "" } else { " - " + author.collegeId }
          text(formatName(author.name) + registration, size: 11pt)
        }),
      )),
    ),
  )

  place(
    center + bottom,
    dy: -22mm,
    align(center, text("São Caetano do Sul\n" + formatDate(submission-date), size: 11pt)),
  )

  pagebreak()
}
