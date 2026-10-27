import type { AuthResponseDto } from '~/types/authTypes'

/**
 * Client HTTP de l'application ($api) + renouvellement de la session.
 *
 * - $api ajoute tout seul `Authorization: Bearer <accessToken>`.
 * - Sur une réponse 401, il demande un nouvel accessToken avec le refresh token,
 *   puis rejoue UNE fois la requête.
 * - Quand plusieurs requêtes échouent en même temps, une seule demande de
 *   renouvellement part (les autres attendent la même promesse) : indispensable
 *   car le serveur n'accepte chaque refresh token qu'une seule fois (rotation).
 *
 * Ce plugin est exécuté une fois par requête côté serveur (SSR) et une fois dans
 * le navigateur : les variables ci-dessous ne sont donc jamais partagées entre
 * deux utilisateurs.
 */
export default defineNuxtPlugin((nuxtApp) => {
  const baseURL = useRuntimeConfig().public.apiBase

  // `useCookie` doit être appelé dans le contexte Nuxt : runWithContext le rétablit
  // même après un await. Côté serveur il peut renvoyer une promesse : on attend toujours.
  const avecContexte = async <T>(fn: () => T): Promise<T> =>
    await nuxtApp.runWithContext(fn)
  const lireAccess = () => avecContexte(() => getAccessToken().value)
  const lireRefresh = () => avecContexte(() => getRefreshToken().value)

  const base = $fetch.create({
    baseURL,
    async onRequest({ options }) {
      const jeton = await lireAccess()
      if (!jeton) return
      const headers = new Headers(options.headers)
      headers.set('Authorization', `Bearer ${jeton}`)
      options.headers = headers
    },
  })

  // --- Renouvellement -------------------------------------------------------

  async function renouveler(): Promise<string | null> {
    const refresh = await lireRefresh()
    if (!refresh) return null
    try {
      const r = await $fetch<AuthResponseDto>('/auth/refresh', {
        baseURL,
        method: 'POST',
        body: { refreshToken: refresh },
      })
      await avecContexte(() => {
        setAccessToken(r.accessToken)
        setRefreshToken(r.refreshToken, secondsUntil(r.refreshExpireAt))
      })
      // Laisse Vue écrire les cookies avant de continuer (cookies relus juste après)
      await nextTick()
      return r.accessToken
    } catch (e) {
      const statut = (e as { statusCode?: number })?.statusCode
      // Réponse refusée par le serveur : la session est terminée.
      if (statut === 400 || statut === 401 || statut === 403) {
        // Autre onglet : il a peut-être déjà renouvelé la session (le refresh token
        // que nous avons envoyé est alors périmé mais le cookie contient le nouveau).
        // On laisse quelques instants à cet onglet pour écrire ses cookies.
        for (let i = 0; i < 4; i++) {
          const courant = await lireRefresh()
          if (courant && courant !== refresh) return (await lireAccess()) ?? null
          if (import.meta.client) await new Promise((r) => setTimeout(r, 150))
        }
        await avecContexte(clearTokens)
      }
      // Panne réseau : on garde les jetons, un nouvel essai aura lieu plus tard
      return null
    }
  }

  let enCours: Promise<string | null> | null = null
  function rafraichirSession() {
    if (!enCours) enCours = renouveler().finally(() => (enCours = null))
    return enCours
  }

  // --- $api -----------------------------------------------------------------

  const api = (async (request: any, options?: any) => {
    try {
      return await base(request, options)
    } catch (e) {
      const statut = (e as { statusCode?: number })?.statusCode
      if (statut !== 401 || !(await lireRefresh())) throw e
      const jeton = await rafraichirSession()
      if (!jeton) {
        if (import.meta.client && !(await lireRefresh())) {
          await avecContexte(() => navigateTo('/login'))
        }
        throw e
      }
      // onRequest relit le cookie : la requête repart avec le nouveau jeton
      return await base(request, options)
    }
  }) as typeof $fetch
  Object.assign(api, base) // raw, native, create

  return { provide: { api, rafraichirSession } }
})
