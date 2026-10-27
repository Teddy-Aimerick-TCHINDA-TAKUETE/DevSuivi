import type { ContributionListDto } from '~/types/conrtibutionTypes'
import type {
  ProjectDetailDto,
  ProjectListDto,
  ProjectNewDto,
} from '~/types/projectTypes'
import type { UserListDto } from '~/types/userTypes'

export function useApiProject() {
  const { $api } = useNuxtApp()

  // --- Lectures : useFetch ----------------------------------------------------
  async function getAllProjects() {
    return await useFetch<ProjectListDto[]>('/project', { $fetch: $api })
  }
  async function getProjectById(id: number) {
    return await useFetch<ProjectDetailDto>(() => `/project/${id}`, {
      $fetch: $api,
    })
  }
  async function getUsersByProjectId(id: number) {
    return await useFetch<UserListDto[]>(() => `/project/${id}/users`, {
      $fetch: $api,
    })
  }
  async function getContributiosByProjectId(id: number) {
    return await useFetch<ContributionListDto[]>(
      () => `/project/${id}/contributions`,
      { $fetch: $api },
    )
  }

  // --- Écritures : $fetch -----------------------------------------------------
  function saveProject(projectNewDto: ProjectNewDto) {
    return appeler<ProjectDetailDto>($api, '/project', {
      method: 'POST',
      body: projectNewDto,
    })
  }
  function updateProject(id: number, projectNewDto: ProjectNewDto) {
    return appeler<ProjectDetailDto>($api, `/project/${id}`, {
      method: 'PUT',
      body: projectNewDto,
    })
  }
  function deleteProject(id: number) {
    return appeler($api, `/project/${id}`, { method: 'DELETE' })
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
