// Cookies de session. Les lectures créent un `useCookie` à chaque appel :
// c'est voulu, ainsi on relit toujours la valeur la plus récente (SSR comme navigateur).

// SameSite=Lax : le cookie n'est pas envoyé lors de requêtes venant d'un autre site.
// Secure : seulement en https (sinon le navigateur refuserait le cookie en http).
function options(maxAge?: number) {
  const https = import.meta.client
    ? location.protocol === 'https:'
    : useRequestURL().protocol === 'https:'
  return {
    sameSite: 'lax' as const,
    secure: https,
    ...(maxAge ? { maxAge } : {}),
  }
}

export const getAccessToken = () => {
  return useCookie<string | null | undefined>('accessToken')
}

export const getRefreshToken = () => {
  return useCookie<string | null | undefined>('refreshToken')
}

// Jeton d'accès : cookie de session (court : il est renouvelé via le refresh token)
export const setAccessToken = (value: string) => {
  const token = useCookie<string | null | undefined>('accessToken', options())
  token.value = value
}

// Refresh token : durée de vie = celle donnée par le serveur (en secondes),
// ainsi l'utilisateur reste connecté même après avoir fermé le navigateur.
export const setRefreshToken = (value: string, maxAgeSeconds?: number) => {
  const token = useCookie<string | null | undefined>(
    'refreshToken',
    options(maxAgeSeconds),
  )
  token.value = value
}

// Déconnexion locale : on oublie les deux jetons
export const clearTokens = () => {
  getAccessToken().value = null
  getRefreshToken().value = null
}

// Secondes restantes avant une date d'expiration renvoyée par l'API
export const secondsUntil = (date: string | Date) =>
  Math.max(0, Math.floor((new Date(date).getTime() - Date.now()) / 1000))
