import type {
  AssignId,
  AssignIdNewDto,
  AssignListDto,
} from '~/types/assignTypes'

export function useApiAssign() {
  const { $api } = useNuxtApp()

  // --- Lectures : useFetch ----------------------------------------------------
  async function getAllAssigns() {
    return await useFetch<AssignListDto[]>('/assign', { $fetch: $api })
  }
  async function getAssignById(assignId: AssignId) {
    return await useFetch<AssignListDto>('/assign/id', {
      $fetch: $api,
      method: 'GET',
      body: assignId,
    })
  }

  // --- Écritures : $fetch -----------------------------------------------------
  function saveAssign(assignIdNewDto: AssignIdNewDto) {
    return appeler<AssignListDto>($api, '/assign', {
      method: 'POST',
      body: assignIdNewDto,
    })
  }
  function deleteAssign(assignId: AssignId) {
    return appeler($api, '/assign', { method: 'DELETE', body: assignId })
  }

  return {
    getAllAssigns,
    getAssignById,
    saveAssign,
    deleteAssign,
  }
}
