import type { ContributionListDto } from '~/types/conrtibutionTypes'
import type { ProjectDetailDto, ProjectListDto } from '~/types/projectTypes'
import type { UserDetailDto, UserListDto, UserNewDto } from '~/types/userTypes'

export function useApiUser() {
  // $api : ajoute le jeton et le renouvelle si besoin (voir plugins/api.ts)
  const { $api } = useNuxtApp()

  // --- Lectures : useFetch (données d'une page, rendues côté serveur) ---------
  async function getAllUsers() {
    return await useFetch<UserListDto[]>('/user', { $fetch: $api })
  }
  async function getUserById(id: number) {
    return await useFetch<UserDetailDto>(() => `/user/${id}`, { $fetch: $api })
  }
  async function getUserProfil() {
    return await useFetch<UserListDto>('/user/me', { $fetch: $api })
  }
  async function getProjectByUserId(id: number) {
    return await useFetch<ProjectListDto[]>(() => `/user/${id}/projects`, {
      $fetch: $api,
    })
  }
  async function getContributionByUserId(id: number) {
    return await useFetch<ContributionListDto[]>(
      () => `/user/${id}/contributions`,
      { $fetch: $api },
    )
  }

  // --- Écritures : $fetch (une requête, résultat { data, error }) -------------
  function saveUser(userNewDto: UserNewDto) {
    return appeler<UserDetailDto>($api, '/user', {
      method: 'POST',
      body: userNewDto,
    })
  }
  function updateUser(id: number, userNewDto: UserNewDto) {
    return appeler<UserDetailDto>($api, `/user/${id}`, {
      method: 'PUT',
      body: userNewDto,
    })
  }
  function updateMe(userNewDto: UserNewDto) {
    return appeler<UserDetailDto>($api, '/user/me', {
      method: 'PUT',
      body: userNewDto,
    })
  }
  function deleteUser(id: number) {
    return appeler($api, `/user/${id}`, { method: 'DELETE' })
  }

  return {
    getAllUsers,
    getUserById,
    getUserProfil,
    getProjectByUserId,
    getContributionByUserId,
    saveUser,
    updateUser,
    updateMe,
    deleteUser,
  }
}
