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

/** Garde les contributions d'un développeur (user = 0 → toutes). */
export function filterByUser<T extends { idUser?: number }>(
  items: T[],
  user = 0,
): T[] {
  return user ? items.filter((i) => i.idUser === user) : items
}

/** Garde les contributions d'un projet (project = 0 → toutes). */
export function filterByProject<T extends { idProject?: number }>(
  items: T[],
  project = 0,
): T[] {
  return project ? items.filter((i) => i.idProject === project) : items
}

/** Projets présents dans une liste de contributions (pour le filtre). */
export function distinctProjects(
  items: { idProject?: number; projectName?: string }[],
): { id: number; name: string }[] {
  const map = new Map<number, string>()
  for (const i of items) {
    if (i.idProject != null) {
      map.set(i.idProject, i.projectName ?? `Projet #${i.idProject}`)
    }
  }
  return [...map]
    .map(([id, name]) => ({ id, name }))
    .sort((a, b) => a.name.localeCompare(b.name))
}

/**
 * Filtres liés : une fois un développeur choisi, seuls SES projets sont proposés ;
 * une fois un projet choisi, seuls les développeurs de CE projet sont proposés.
 * (Les listes sont déduites des contributions : on ne propose que des choix
 * qui donnent au moins un résultat.)
 */
export const userOptions = <
  T extends { idUser?: number; userName?: string; idProject?: number },
>(
  items: T[],
  project = 0,
) => distinctUsers(filterByProject(items, project))

export const projectOptions = <
  T extends { idProject?: number; projectName?: string; idUser?: number },
>(
  items: T[],
  user = 0,
) => distinctProjects(filterByUser(items, user))

/** Applique développeur, projet et période (bornes incluses). */
export function filterContributions<
  T extends {
    idUser?: number
    idProject?: number
    dateContribution: string | Date
  },
>(
  items: T[],
  f: { user?: number; project?: number; from?: string; to?: string },
): T[] {
  return filterByDate(
    filterByProject(filterByUser(items, f.user), f.project),
    f.from,
    f.to,
  )
}

/** Développeurs présents dans une liste de contributions (pour le filtre). */
export function distinctUsers(
  items: { idUser?: number; userName?: string }[],
): { id: number; name: string }[] {
  const map = new Map<number, string>()
  for (const i of items) {
    if (i.idUser != null) map.set(i.idUser, i.userName ?? `#${i.idUser}`)
  }
  return [...map]
    .map(([id, name]) => ({ id, name }))
    .sort((a, b) => a.name.localeCompare(b.name))
}

/** Nombre de contributions et durée cumulée (minutes) par projet. */
export function sumByProject(
  items: { duration: number; idProject?: number; projectName?: string }[],
): { id: number; name: string; count: number; total: number }[] {
  const map = new Map<
    number,
    { id: number; name: string; count: number; total: number }
  >()
  for (const i of items) {
    if (i.idProject == null) continue
    const row = map.get(i.idProject) ?? {
      id: i.idProject,
      name: i.projectName ?? `Projet #${i.idProject}`,
      count: 0,
      total: 0,
    }
    row.count++
    row.total += i.duration
    map.set(i.idProject, row)
  }
  return [...map.values()].sort((a, b) => b.total - a.total)
}
