// Filtre par date pour les listes de contributions.
// Les dates arrivent du Back-End au format "YYYY-MM-DD" : on compare ces chaînes
// (ordre alphabétique = ordre chronologique), sans passer par new Date()
// qui décalerait d'un jour selon le fuseau horaire.

const isoDay = (d: string | Date) =>
  typeof d === 'string' ? d.slice(0, 10) : d.toISOString().slice(0, 10)

/** Garde les éléments dont la date est comprise entre from et to (inclus). */
export function filterByDate<T extends { dateContribution: string | Date }>(
  items: T[],
  from = '',
  to = '',
): T[] {
  if (!from && !to) return items
  return items.filter((i) => {
    const day = isoDay(i.dateContribution)
    return (!from || day >= from) && (!to || day <= to)
  })
}
