/**
 * Rôle qui détermine le thème : ADMIN > MANAGER > DEVELOPER.
 * Sans utilisateur connecté (ou sans rôle connu) : 'guest' = thème par défaut.
 */
export type ThemeRole = 'admin' | 'manager' | 'developer' | 'guest'

export const themeRole = (roles: string[] = []): ThemeRole => {
  if (roles.includes('ADMIN')) return 'admin'
  if (roles.includes('MANAGER')) return 'manager'
  if (roles.includes('DEVELOPER')) return 'developer'
  return 'guest'
}

export const THEME_LABELS: Record<ThemeRole, string> = {
  admin: 'Admin',
  manager: 'Manager',
  developer: 'Développeur',
  guest: '',
}
