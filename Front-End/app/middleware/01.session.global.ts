// S'exécute AVANT auth.global.ts (ordre alphabétique des middlewares globaux).
// Si le jeton d'accès a expiré mais qu'un refresh token existe, on renouvelle la
// session ici : la suite (auth.global.ts, pages) voit alors un utilisateur connecté.
export default defineNuxtRouteMiddleware(async () => {
  const { $rafraichirSession } = useNuxtApp()
  const { isLoggedIn } = useAuth()
  if (!isLoggedIn.value && getRefreshToken().value) {
    await $rafraichirSession()
  }
})
