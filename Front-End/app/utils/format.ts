// Petits helpers d'affichage partagés par les pages (auto-importés par Nuxt)

export const formatDate = (d: string | Date) => {
  // "2026-10-07" (LocalDate Java) : on évite new Date(str) qui interprète en UTC
  const m = typeof d === 'string' ? d.match(/^(\d{4})-(\d{2})-(\d{2})/) : null
  const date = m
    ? new Date(Number(m[1]), Number(m[2]) - 1, Number(m[3]))
    : new Date(d)
  return date.toLocaleDateString('fr-FR', {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
  })
}

export const fullName = (u?: { firstName: string; lastName: string } | null) =>
  u ? `${u.firstName} ${u.lastName}` : '—'

export const initials = (u?: { firstName: string; lastName: string } | null) =>
  u ? `${u.firstName?.[0] ?? ''}${u.lastName?.[0] ?? ''}`.toUpperCase() : '?'

// États possibles d'un projet (mêmes valeurs que côté Back-End)
export const PROJECT_STATUSES = [
  'PLANNED',
  'IN_PROGRESS',
  'FINISH',
  'CANCELED',
  'PAUSE',
] as const
export type ProjectStatus = (typeof PROJECT_STATUSES)[number]
export const DEFAULT_PROJECT_STATUS: ProjectStatus = 'PLANNED'

// Une couleur par état : voir les .ap-pill[data-tone=...] dans AppPage.vue
export const statusTone = (status = '') => {
  switch (status.toUpperCase()) {
    case 'PLANNED':
      return 'planned'
    case 'IN_PROGRESS':
      return 'progress'
    case 'FINISH':
      return 'finish'
    case 'CANCELED':
      return 'canceled'
    case 'PAUSE':
      return 'pause'
    default:
      return 'info'
  }
}

// Une couleur par rôle : voir .ap-pill[data-tone='role-...'] dans AppPage.vue
export const roleTone = (role = '') => {
  switch (role.toUpperCase()) {
    case 'ADMIN':
      return 'role-admin'
    case 'MANAGER':
      return 'role-manager'
    case 'DEVELOPER':
      return 'role-developer'
    default:
      return 'info'
  }
}

export const ALL_ROLES = ['ADMIN', 'MANAGER', 'DEVELOPER'] as const

export const parseRoles = (value: string) =>
  value
    .split(',')
    .map((r) => r.trim())
    .filter(Boolean)

export const errorMessage = (e: unknown) => {
  const err = e as any
  return (
    err?.data?.detail ??
    err?.data?.message ??
    err?.statusMessage ??
    err?.message ??
    'Une erreur est survenue.'
  )
}
