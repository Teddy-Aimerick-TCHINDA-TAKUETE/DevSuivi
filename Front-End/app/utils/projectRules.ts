// Règles métier sur les états d'un projet (mêmes valeurs que côté Back-End).
// Un seul endroit à modifier si la règle change.

/** Un développeur ne peut être affecté qu'à un projet prévu ou en cours. */
export const canBeAssigned = (status = '') =>
  status === 'PLANNED' || status === 'IN_PROGRESS'

/** Une contribution ne peut être déclarée que sur un projet en cours. */
export const canReceiveContributions = (status = '') => status === 'IN_PROGRESS'

/** Durée cumulée d'une liste de contributions (même unité que le quota). */
export const sumDuration = (items: { duration: number }[] = []) =>
  items.reduce((sum, c) => sum + c.duration, 0)

/**
 * Dépassement : durée cumulée strictement supérieure au quota.
 * C'est un simple signalement, il ne change pas l'état du projet.
 */
export const isOverQuota = (used = 0, quota = 0) => used > quota
