import { useAuth } from '~/composables/useAuth'
import type { UserListDto } from '~/types/userTypes'

const inSection = (path: string, base: string) =>
  path === base || path.startsWith(`${base}/`)

async function isOwnUserPage(to: { path: string; query: Record<string, any> }) {
  const config = useRuntimeConfig()
  const apiBase = config.public.apiBase
  const accessToken = getAccessToken()
  const user = await $fetch<UserListDto>(`${apiBase}/user/me`, {
    headers: { Authorization: `Bearer ${accessToken.value}` },
  })
  const myId = user.id
  if (!myId) return false
  if (to.path === `/user/me`) return true
  if (['/user/me/projects', '/user/me/contributions'].includes(to.path)) {
    return true
  }
  // if (['/user/projects', '/user/contributions'].includes(to.path)) {
  //   return Number(to.query.id) === myId
  // }
  return false
}

// Un non-admin voit la liste des projets, et seulement ceux auxquels il est affecté
async function isOwnProjectPage(to: {
  path: string
  query: Record<string, any>
}) {
  if (
    [
      '/user/me/projects',
      '/user/me/projects/all',
      '/user/me/projects/contributions',
      '/user/me/projects/users',
    ].includes(to.path)
  )
    return true

  let projectId = 0
  const detail = to.path.match(/^\/user\/me\/projects\/(\d+)$/)
  if (detail) projectId = Number(detail[1])
  // else if (['/project/users', '/project/contributions'].includes(to.path)) {
  //   projectId = Number(to.query.id)
  // }
  if (!projectId) return false

  // Composables AVANT le premier await (sinon le contexte Nuxt est perdu côté serveur)
  const apiBase = useRuntimeConfig().public.apiBase
  const accessToken = getAccessToken()
  const user = await $fetch<UserListDto>(`${apiBase}/user/me`, {
    headers: { Authorization: `Bearer ${accessToken.value}` },
  })

  const myId = user.id
  if (!myId) return false

  try {
    const projects = await $fetch<{ id: number }[]>(
      `${apiBase}/user/${myId}/projects`,
      {
        headers: { Authorization: `Bearer ${accessToken.value}` },
      },
    )
    return projects.some((p) => p.id === projectId)
  } catch {
    return false
  }
}

async function isOwnCotributionPage(to: {
  path: string
  query: Record<string, any>
}) {
  if (
    [
      '/user/me/contributions',
      '/user/me/contributions/all',
      '/user/me/contributions/save',
    ].includes(to.path)
  )
    return true

  let contributionId = 0
  const detail = to.path.match(/^\/user\/me\/contributions\/(\d+)$/)
  if (detail) contributionId = Number(detail[1])
  if (!contributionId) return false

  const apiBase = useRuntimeConfig().public.apiBase
  const accessToken = getAccessToken()
  const user = await $fetch<UserListDto>(`${apiBase}/user/me`, {
    headers: { Authorization: `Bearer ${accessToken.value}` },
  })

  const myId = user.id
  if (!myId) return false

  try {
    const contributions = await $fetch<{ id: number }[]>(
      `${apiBase}/user/${myId}/contributions`,
      {
        headers: { Authorization: `Bearer ${accessToken.value}` },
      },
    )
    return contributions.some((p) => p.id === contributionId)
  } catch {
    return false
  }
}

export default defineNuxtRouteMiddleware(async (to) => {
  // Page inexistante : on laisse Nuxt afficher la page 404
  if (to.matched.length === 0) return

  const { isLoggedIn, hasRole } = useAuth()
  const publicRoutes = ['/login', '/register']

  if (!isLoggedIn.value && !publicRoutes.includes(to.path)) {
    return navigateTo({ path: '/login', query: { redirect: to.fullPath } })
  }

  const forbidden = () =>
    createError({ statusCode: 403, statusMessage: 'Accès refusé' })

  if (
    inSection(to.path, '/user') &&
    !hasRole('ADMIN') &&
    !(await isOwnUserPage(to)) &&
    !(await isOwnProjectPage(to)) &&
    !(await isOwnCotributionPage(to))
  )
    throw forbidden()
  if (inSection(to.path, '/server') && !hasRole('ADMIN')) throw forbidden()
  if (inSection(to.path, '/assign') && !hasRole('ADMIN', 'MANAGER'))
    throw forbidden()
  if (inSection(to.path, '/project') && !hasRole('ADMIN', 'MANAGER'))
    throw forbidden()
  if (inSection(to.path, '/contribution') && !hasRole('ADMIN', 'MANAGER'))
    throw forbidden()
})
