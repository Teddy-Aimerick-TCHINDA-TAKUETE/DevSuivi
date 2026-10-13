import type {
  ContributionDetailDto,
  ContributionListDto,
  ContributionNewDto,
} from '~/types/conrtibutionTypes'

export function useApiContribution() {
  const config = useRuntimeConfig()
  const apiBase = config.public.apiBase
  const accessToken = getAccessToken()

  async function getAllContributions() {
    return await useFetch<ContributionListDto[]>(
      () => `${apiBase}/contribution`,
      {
        headers: computed(() => ({
          Authorization: `Bearer ${accessToken.value}`,
        })),
      },
    )
  }
  async function getContributionById(id: number) {
    return await useFetch<ContributionDetailDto>(
      () => `${apiBase}/contribution/${id}`,
      {
        headers: computed(() => ({
          Authorization: `Bearer ${accessToken.value}`,
        })),
      },
    )
  }
  async function saveContribution(contributionNewDto: ContributionNewDto) {
    return await useFetch<ContributionDetailDto>(
      () => `${apiBase}/contribution`,
      {
        headers: computed(() => ({
          Authorization: `Bearer ${accessToken.value}`,
        })),
        method: 'POST',
        body: contributionNewDto,
      },
    )
  }
  async function updateContribution(
    id: number,
    contributionNewDto: ContributionNewDto,
  ) {
    return await useFetch<ContributionDetailDto>(
      () => `${apiBase}/contribution/${id}`,
      {
        headers: computed(() => ({
          Authorization: `Bearer ${accessToken.value}`,
        })),
        method: 'PUT',
        body: contributionNewDto,
      },
    )
  }
  async function deleteContribution(id: number) {
    return await useFetch(() => `${apiBase}/contribution/${id}`, {
      headers: computed(() => ({
        Authorization: `Bearer ${accessToken.value}`,
      })),
      method: 'DELETE',
    })
  }
  return {
    getAllContributions,
    getContributionById,
    saveContribution,
    updateContribution,
    deleteContribution,
  }
}
