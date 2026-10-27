import type { ProjectListDto } from '~/types/projectTypes'
import type { ServerListDto, ServerNewDto } from '~/types/serverTypes'

export function useApiServer() {
  const { $api } = useNuxtApp()

  // --- Lectures : useFetch ----------------------------------------------------
  async function getAllServers() {
    return await useFetch<ServerListDto[]>('/server', { $fetch: $api })
  }
  async function getServerById(id: number) {
    return await useFetch<ServerListDto>(() => `/server/${id}`, {
      $fetch: $api,
    })
  }
  async function getProjectsByServerId(id: number) {
    return await useFetch<ProjectListDto[]>(() => `/server/${id}/projects`, {
      $fetch: $api,
    })
  }

  // --- Écritures : $fetch -----------------------------------------------------
  function saveServer(serverNewDto: ServerNewDto) {
    return appeler<ServerListDto>($api, '/server', {
      method: 'POST',
      body: serverNewDto,
    })
  }
  function updateServer(id: number, serverNewDto: ServerNewDto) {
    return appeler<ServerListDto>($api, `/server/${id}`, {
      method: 'PUT',
      body: serverNewDto,
    })
  }
  function deleteServer(id: number) {
    return appeler($api, `/server/${id}`, { method: 'DELETE' })
  }

  return {
    getAllServers,
    getServerById,
    getProjectsByServerId,
    saveServer,
    updateServer,
    deleteServer,
  }
}
