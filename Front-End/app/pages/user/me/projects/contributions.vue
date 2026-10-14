<template>
  <div>
    <AppPage
      title="Contributions du projet"
      :subtitle="
        current
          ? `Temps passé sur « ${current.name} »`
          : 'Choisissez un projet pour voir ses contributions.'
      "
      back="/user/me/projects"
    >
      <template #actions>
        <NuxtLink
          v-if="id"
          :to="`/user/me/contributions/save?project=${id}`"
          role="button"
          class="ap-btn"
          >+ Contribution</NuxtLink
        >
      </template>

      <EntityPicker label="Projet" :options="options" :current="id" />

      <p v-if="!id" class="ap-empty ap-muted">
        Sélectionnez un projet ci-dessus.
      </p>
      <DataState
        v-else
        :pending="pending"
        :error="error"
        :empty="!contributions.length"
        empty-text="Aucune contribution sur ce projet."
      >
        <ContributionTableMe :items="contributions" />
      </DataState>
    </AppPage>
  </div>
</template>

<script setup lang="ts">
  definePageMeta({ key: (route) => route.fullPath })

  const id = Number(useRoute().query.id) || 0
  const { getAllProjects, getContributiosByProjectId } = useApiProject()
  // const { data: projects } = getAllProjects()
  const result = id ? await getContributiosByProjectId(id) : null

  const { getProjectByUserId, getUserProfil } = useApiUser()
  const { data: user } = await getUserProfil()
  const userId = user.value?.id || 0
  const { data: projects } = await getProjectByUserId(userId)

  const contributions = computed(() => result?.data.value ?? [])
  const pending = computed(() => result?.status.value === 'pending')
  const error = computed(() => result?.error.value)
  const options = computed(() =>
    (projects.value ?? []).map((p) => ({ id: p.id, label: p.name })),
  )
  const current = computed(() => projects.value?.find((p) => p.id === id))
</script>

<style scoped></style>
