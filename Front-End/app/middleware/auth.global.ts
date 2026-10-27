import { useAuth } from '~/composables/useAuth'
import type { UserListDto } from '~/types/userTypes'

const inSection = (path: string, base: string) =>
  path === base || path.startsWith(`${base}/`)

async function userPage(to: { path: string; query: Record<string, any> }) {
  const { hasRole } = useAuth()
  if (hasRole('DEVELOPER')) return false
  if (hasRole('ADMIN')) return true
  if (['/user/save'].includes(to.path) && !hasRole('ADMIN')) return false
  return true
}

async function isOwnUserPage(to: { path: string; query: Record<string, any> }) {
  const { $api } = useNuxtApp()
  const user = await $api<UserListDto>('/user/me')
  const myId = user.id
  if (!myId) return false
  if (to.path === `/user/me`) return true
  if (to.path === `/user/me/save`) return true
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
  const { $api } = useNuxtApp()
  const user = await $api<UserListDto>('/user/me')

  const myId = user.id
  if (!myId) return false

  try {
    const projects = await $api<{ id: number }[]>(`/user/${myId}/projects`)
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

  const { $api } = useNuxtApp()
  const user = await $api<UserListDto>('/user/me')

  const myId = user.id
  if (!myId) return false

  try {
    const contributions = await $api<{ id: number }[]>(
      `/user/${myId}/contributions`,
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
    (inSection(to.path, '/user/me/projects') ||
      inSection(to.path, '/user/me/contributions')) &&
    !hasRole('DEVELOPER')
  )
    throw forbidden()
  if (
    inSection(to.path, '/user') &&
    !(await userPage(to)) &&
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
