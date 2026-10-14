import type { ProjectListDto } from '~/types/projectTypes'
import type { ServerListDto, ServerNewDto } from '~/types/serverTypes'

export function useApiServer() {
  const config = useRuntimeConfig()
  const apiBase = config.public.apiBase
  const accessToken = getAccessToken()

  async function getAllServers() {
    return await useFetch<ServerListDto[]>(() => `${apiBase}/server`, {
      headers: computed(() => ({
        Authorization: `Bearer ${accessToken.value}`,
      })),
    })
  }
  async function getServerById(id: number) {
    return await useFetch<ServerListDto>(() => `${apiBase}/server/${id}`, {
      headers: computed(() => ({
        Authorization: `Bearer ${accessToken.value}`,
      })),
    })
  }
  async function getProjectsByServerId(id: number) {
    return await useFetch<ProjectListDto[]>(
      () => `${apiBase}/server/${id}/projects`,
      {
        headers: computed(() => ({
          Authorization: `Bearer ${accessToken.value}`,
        })),
      },
    )
  }
  async function saveServer(serverNewDto: ServerNewDto) {
    return await useFetch<ServerListDto>(() => `${apiBase}/server`, {
      headers: computed(() => ({
        Authorization: `Bearer ${accessToken.value}`,
      })),
      method: 'POST',
      body: serverNewDto,
    })
  }
  async function updateServer(id: number, serverNewDto: ServerNewDto) {
    return await useFetch<ServerListDto>(() => `${apiBase}/server/${id}`, {
      headers: computed(() => ({
        Authorization: `Bearer ${accessToken.value}`,
      })),
      method: 'PUT',
      body: serverNewDto,
    })
  }
  async function deleteServer(id: number) {
    return await useFetch(() => `${apiBase}/server/${id}`, {
      headers: computed(() => ({
        Authorization: `Bearer ${accessToken.value}`,
      })),
      method: 'DELETE',
    })
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
