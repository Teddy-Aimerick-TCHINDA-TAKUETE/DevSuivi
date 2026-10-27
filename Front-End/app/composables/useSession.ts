// Fin de session : on oublie les jetons, on prévient le serveur pour qu'il révoque
// le refresh token, puis on retourne à la page de connexion.
export function useSession() {
  // Composables appelés avant tout await (contexte Nuxt)
  const { logout: revoquer } = useApiAuth()

  async function logout() {
    const refreshToken = getRefreshToken().value
    clearTokens()
    if (refreshToken) await revoquer({ refreshToken }) // échec ignoré : appeler() ne lève pas
    await navigateTo('/login')
  }

  return { logout }
}
