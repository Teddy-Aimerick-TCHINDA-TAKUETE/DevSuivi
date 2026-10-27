import type {
  ContributionDetailDto,
  ContributionListDto,
  ContributionNewDto,
} from '~/types/conrtibutionTypes'

export function useApiContribution() {
  const { $api } = useNuxtApp()

  // --- Lectures : useFetch ----------------------------------------------------
  async function getAllContributions() {
    return await useFetch<ContributionListDto[]>('/contribution', {
      $fetch: $api,
    })
  }
  async function getContributionById(id: number) {
    return await useFetch<ContributionDetailDto>(
      () => `/contribution/${id}`,
      { $fetch: $api },
    )
  }

  // --- Écritures : $fetch -----------------------------------------------------
  function saveContribution(contributionNewDto: ContributionNewDto) {
    return appeler<ContributionDetailDto>($api, '/contribution', {
      method: 'POST',
      body: contributionNewDto,
    })
  }
  function updateContribution(
    id: number,
    contributionNewDto: ContributionNewDto,
  ) {
    return appeler<ContributionDetailDto>($api, `/contribution/${id}`, {
      method: 'PUT',
      body: contributionNewDto,
    })
  }
  function deleteContribution(id: number) {
    return appeler($api, `/contribution/${id}`, { method: 'DELETE' })
  }

  return {
    getAllContributions,
    getContributionById,
    saveContribution,
    updateContribution,
    deleteContribution,
  }
}
