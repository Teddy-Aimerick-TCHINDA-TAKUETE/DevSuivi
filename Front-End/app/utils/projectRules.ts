// Règles métier sur les états d'un projet (mêmes valeurs que côté Back-End).
// Un seul endroit à modifier si la règle change.

/** Un développeur ne peut être affecté qu'à un projet prévu ou en cours. */
export const canBeAssigned = (status = '') =>
  status === 'PLANNED' || status === 'IN_PROGRESS'

/** Une contribution ne peut être déclarée que sur un projet en cours. */
export const canReceiveContributions = (status = '') => status === 'IN_PROGRESS'

/**
 * Unités : le quota d'un projet est en HEURES, la durée d'une contribution en
 * MINUTES. Toute comparaison passe par ces helpers.
 */
export const MINUTES_PER_HOUR = 60
export const quotaInMinutes = (quotaHours = 0) => quotaHours * MINUTES_PER_HOUR

/** Durée cumulée d'une liste de contributions, en minutes. */
export const sumDuration = (items: { duration: number }[] = []) =>
  items.reduce((sum, c) => sum + c.duration, 0)

/**
 * Dépassement : durée cumulée (minutes) strictement supérieure au quota (heures).
 * C'est un simple signalement, il ne change pas l'état du projet.
 */
export const isOverQuota = (usedMinutes = 0, quotaHours = 0) =>
  usedMinutes > quotaInMinutes(quotaHours)

// ---------- Filtres des listes de projets ----------

type FilterableProject = {
  status: string
  quota: number
  totalDuration?: number
}

/** Le projet dépasse-t-il son quota ? (signalement, l'état ne change pas) */
export const isProjectOver = (p: FilterableProject) =>
  isOverQuota(p.totalDuration, p.quota)

/** Nombre de projets en dépassement dans une liste. */
export const countOver = (items: FilterableProject[] = []) =>
  items.filter(isProjectOver).length

/** Filtre par état (vide = tous) et/ou projets en dépassement uniquement. */
export function filterProjects<T extends FilterableProject>(
  items: T[],
  f: { status?: string; overOnly?: boolean },
): T[] {
  return items.filter(
    (p) =>
      (!f.status || p.status === f.status) && (!f.overOnly || isProjectOver(p)),
  )
}
