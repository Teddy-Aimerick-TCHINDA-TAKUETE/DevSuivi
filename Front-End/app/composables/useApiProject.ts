import type { ContributionListDto } from '~/types/conrtibutionTypes'
import type {
  ProjectDetailDto,
  ProjectListDto,
  ProjectNewDto,
} from '~/types/projectTypes'
import type { UserListDto } from '~/types/userTypes'

export function useApiProject() {
  const config = useRuntimeConfig()
  const apiBase = config.public.apiBase
  const accessToken = getAccessToken()

  async function getAllProjects() {
    return await useFetch<ProjectListDto[]>(() => `${apiBase}/project`, {
      headers: computed(() => ({
        Authorization: `Bearer ${accessToken.value}`,
      })),
    })
  }
  async function getProjectById(id: number) {
    return await useFetch<ProjectDetailDto>(() => `${apiBase}/project/${id}`, {
      headers: computed(() => ({
        Authorization: `Bearer ${accessToken.value}`,
      })),
    })
  }
  async function getUsersByProjectId(id: number) {
    return await useFetch<UserListDto[]>(
      () => `${apiBase}/project/${id}/users`,
      {
        headers: computed(() => ({
          Authorization: `Bearer ${accessToken.value}`,
        })),
      },
    )
  }
  async function getContributiosByProjectId(id: number) {
    return await useFetch<ContributionListDto[]>(
      () => `${apiBase}/project/${id}/contributions`,
      {
        headers: computed(() => ({
          Authorization: `Bearer ${accessToken.value}`,
        })),
      },
    )
  }
  async function saveProject(projectNewDto: ProjectNewDto) {
    return await useFetch<ProjectDetailDto>(() => `${apiBase}/project`, {
      headers: computed(() => ({
        Authorization: `Bearer ${accessToken.value}`,
      })),
      method: 'POST',
      body: projectNewDto,
    })
  }
  async function updateProject(id: number, projectNewDto: ProjectNewDto) {
    return await useFetch<ProjectDetailDto>(() => `${apiBase}/project/${id}`, {
      headers: computed(() => ({
        Authorization: `Bearer ${accessToken.value}`,
      })),
      method: 'PUT',
      body: projectNewDto,
    })
  }
  async function deleteProject(id: number) {
    return await useFetch(() => `${apiBase}/project/${id}`, {
      headers: computed(() => ({
        Authorization: `Bearer ${accessToken.value}`,
      })),
      method: 'DELETE',
    })
  }
  return {
    getAllProjects,
    getProjectById,
    getUsersByProjectId,
    getContributiosByProjectId,
    saveProject,
    updateProject,
    deleteProject,
  }
}
