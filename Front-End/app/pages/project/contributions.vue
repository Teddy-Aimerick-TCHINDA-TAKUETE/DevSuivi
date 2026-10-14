<template>
  <div>
    <AppPage
      title="Contributions du projet"
      :subtitle="
        current
          ? `Temps passé sur « ${current.name} »`
          : 'Choisissez un projet pour voir ses contributions.'
      "
      back="/project"
    >
      <template #actions>
        <NuxtLink
          v-if="id"
          :to="`/contribution/save?project=${id}`"
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
        <ContributionTable :items="contributions" />
      </DataState>
    </AppPage>
  </div>
</template>

<script setup lang="ts">
  definePageMeta({ key: (route) => route.fullPath })

  const id = Number(useRoute().query.id) || 0
  const { getAllProjects, getContributiosByProjectId } = useApiProject()
  const { data: projects } = await getAllProjects()
  const result = id ? await getContributiosByProjectId(id) : null

  const contributions = computed(() => result?.data.value ?? [])
  const pending = computed(() => result?.status.value === 'pending')
  const error = computed(() => result?.error.value)
  const options = computed(() =>
    (projects.value ?? []).map((p) => ({ id: p.id, label: p.name })),
  )
  const current = computed(() => projects.value?.find((p) => p.id === id))
</script>

<style scoped></style>
