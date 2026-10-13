<template>
  <div>
    <AppPage
      title="Vos Contributions"
      :subtitle="
        current
          ? `Temps passé par ${fullName(current)}`
          : 'Voici la liste de vos contributions.'
      "
    >
      <template #actions>
        <NuxtLink
          v-if="id"
          :to="`/contribution/save?user=${id}`"
          role="button"
          class="ap-btn"
          >+ Contribution</NuxtLink
        >
      </template>

      <DataState
        :pending="pending"
        :error="error"
        :empty="!contributions.length"
        empty-text="Aucune contribution pour cet utilisateur."
      >
        <ContributionTable :items="contributions" />
      </DataState>
    </AppPage>
  </div>
</template>

<script setup lang="ts">
  import { fullName } from '~/utils/format'

  definePageMeta({ key: (route) => route.fullPath })

  const { getContributionByUserId, getUserProfil } = useApiUser()
  const { data: user } = await getUserProfil()
  const id = user.value?.id || 0
  const result = id ? await getContributionByUserId(id) : null

  const contributions = computed(() => result?.data.value ?? [])
  const pending = computed(() => result?.status.value === 'pending')
  const error = computed(() => result?.error.value)
  const current = computed(() => user.value)
</script>

<style scoped></style>
