// Petits helpers d'affichage partagés par les pages (auto-importés par Nuxt)

export const formatDate = (d: string | Date) => {
  // "2026-10-07" (LocalDate Java) : on évite new Date(str) qui interprète en UTC
  const m = typeof d === 'string' ? d.match(/^(\d{4})-(\d{2})-(\d{2})/) : null
  const date = m ? new Date(Number(m[1]), Number(m[2]) - 1, Number(m[3])) : new Date(d)
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

export const statusTone = (status = '') => {
  const s = status.toLowerCase()
  if (/(term|done|fini|clos|archiv)/.test(s)) return 'ok'
  if (/(attente|pause|todo|brouillon|pending)/.test(s)) return 'warn'
  return 'info'
}

export const parseRoles = (value: string) =>
  value
    .split(',')
    .map((r) => r.trim())
    .filter(Boolean)

export const errorMessage = (e: unknown) => {
  const err = e as any
  return (
    err?.data?.message ??
    err?.statusMessage ??
    err?.message ??
    'Une erreur est survenue.'
  )
}
