export function useAuth() {
  const token = getAccessToken()

  const claims = computed<Record<string, any> | null>(() => {
    if (!token.value) return null
    try {
      const part = token.value.split('.')[1]
      if (!part) return null
      const c = JSON.parse(atob(part.replace(/-/g, '+').replace(/_/g, '/')))
      if (c.exp && c.exp * 1000 < Date.now()) return null // token expiré
      return c
    } catch {
      return null
    }
  })

  const isLoggedIn = computed(() => !!claims.value)
  const roles = computed<string[]>(() => claims.value?.roles ?? [])
  const hasRole = (...wanted: string[]) =>
    roles.value.some((r) => wanted.includes(r))

  return { isLoggedIn, roles, hasRole }
}
