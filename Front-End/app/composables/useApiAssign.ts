import type {
  AssignId,
  AssignIdNewDto,
  AssignListDto,
} from '~/types/assignTypes'

export function useApiAssign() {
  const config = useRuntimeConfig()
  const apiBase = config.public.apiBase
  const accessToken = getAccessToken()

  async function getAllAssigns() {
    return await useFetch<AssignListDto[]>(() => `${apiBase}/assign`, {
      headers: computed(() => ({
        Authorization: `Bearer ${accessToken.value}`,
      })),
    })
  }
  async function getAssignById(assignId: AssignId) {
    return await useFetch<AssignListDto>(() => `${apiBase}/assign/id`, {
      headers: computed(() => ({
        Authorization: `Bearer ${accessToken.value}`,
      })),
      method: 'GET',
      body: assignId,
    })
  }
  async function saveAssign(assignIdNewDto: AssignIdNewDto) {
    return await useFetch<AssignListDto>(() => `${apiBase}/assign`, {
      headers: computed(() => ({
        Authorization: `Bearer ${accessToken.value}`,
      })),
      method: 'POST',
      body: assignIdNewDto,
    })
  }
  async function deleteAssign(assignId: AssignId) {
    return await useFetch(() => `${apiBase}/assign`, {
      headers: computed(() => ({
        Authorization: `Bearer ${accessToken.value}`,
      })),
      method: 'DELETE',
      body: assignId,
    })
  }
  return {
    getAllAssigns,
    getAssignById,
    saveAssign,
    deleteAssign,
  }
}
