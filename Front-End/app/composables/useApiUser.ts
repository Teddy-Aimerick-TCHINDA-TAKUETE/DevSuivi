import type { ContributionListDto } from '~/types/conrtibutionTypes'
import type { ProjectDetailDto, ProjectListDto } from '~/types/projectTypes'
import type { UserDetailDto, UserListDto, UserNewDto } from '~/types/userTypes'
import { getAccessToken } from '~/utils/stockage-jetons'

export function useApiUser() {
  const config = useRuntimeConfig()
  const apiBase = config.public.apiBase
  const accessToken = getAccessToken()

  async function getAllUsers() {
    return await useFetch<UserListDto[]>(() => `${apiBase}/user`, {
      headers: computed(() => ({
        Authorization: `Bearer ${accessToken.value}`,
      })),
    })
  }
  async function getUserById(id: number) {
    return await useFetch<UserDetailDto>(() => `${apiBase}/user/${id}`, {
      headers: computed(() => ({
        Authorization: `Bearer ${accessToken.value}`,
      })),
    })
  }
  async function getUserProfil() {
    return await useFetch<UserListDto>(() => `${apiBase}/user/me`, {
      headers: computed(() => ({
        Authorization: `Bearer ${accessToken.value}`,
      })),
    })
  }
  async function getProjectByUserId(id: number) {
    return await useFetch<ProjectListDto[]>(
      () => `${apiBase}/user/${id}/projects`,
      {
        headers: computed(() => ({
          Authorization: `Bearer ${accessToken.value}`,
        })),
      },
    )
  }
  async function getContributionByUserId(id: number) {
    return await useFetch<ContributionListDto[]>(
      () => `${apiBase}/user/${id}/contributions`,
      {
        headers: computed(() => ({
          Authorization: `Bearer ${accessToken.value}`,
        })),
      },
    )
  }
  async function saveUser(userNewDto: UserNewDto) {
    return await useFetch<UserDetailDto>(() => `${apiBase}/user`, {
      headers: computed(() => ({
        Authorization: `Bearer ${accessToken.value}`,
      })),
      method: 'POST',
      body: userNewDto,
    })
  }
  async function updateUser(id: number, userNewDto: UserNewDto) {
    return await useFetch<UserDetailDto>(() => `${apiBase}/user/${id}`, {
      headers: computed(() => ({
        Authorization: `Bearer ${accessToken.value}`,
      })),
      method: 'PUT',
      body: userNewDto,
    })
  }
  async function updateMe(userNewDto: UserNewDto) {
    return await useFetch<UserDetailDto>(() => `${apiBase}/user/me`, {
      headers: computed(() => ({
        Authorization: `Bearer ${accessToken.value}`,
      })),
      method: 'PUT',
      body: userNewDto,
    })
  }
  async function deleteUser(id: number) {
    return await useFetch(() => `${apiBase}/user/${id}`, {
      headers: computed(() => ({
        Authorization: `Bearer ${accessToken.value}`,
      })),
      method: 'DELETE',
    })
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
