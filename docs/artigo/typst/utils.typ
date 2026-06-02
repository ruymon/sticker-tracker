#let formatName(name) = {
  let words = name.split(" ")

  if words.len() < 2 {
    return name
  }

  let first-name = words.remove(0)
  let last-name = words.pop()
  let condensed-surnames = ()

  for surname in words {
    condensed-surnames.push(surname.first() + ".")
  }

  (first-name, condensed-surnames, last-name).flatten().join(" ")
}

#let formatDate(date) = {
  if date == none {
    return ""
  }

  let months = (
    "janeiro",
    "fevereiro",
    "março",
    "abril",
    "maio",
    "junho",
    "julho",
    "agosto",
    "setembro",
    "outubro",
    "novembro",
    "dezembro",
  )

  str(date.day()) + " de " + months.at(date.month() - 1) + " de " + str(date.year())
}
