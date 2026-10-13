<template>
  <div>
    <AppPage
      title="Membres du projet"
      :subtitle="
        current
          ? `Équipe de « ${current.name} »`
          : 'Choisissez un projet pour voir son équipe.'
      "
      back="/user/me/projets"
    >
      <EntityPicker label="Projet" :options="options" :current="id" />

      <p v-if="!id" class="ap-empty ap-muted">
        Sélectionnez un projet ci-dessus.
      </p>
      <DataState
        v-else
        :pending="pending"
        :error="error"
        :empty="!users.length"
        empty-text="Aucun membre sur ce projet."
      >
        <UserCardsMe :items="users" />
      </DataState>
    </AppPage>
  </div>
</template>

<script setup lang="ts">
  definePageMeta({ key: (route) => route.fullPath })

  const id = Number(useRoute().query.id) || 0
  const { getAllProjects, getUsersByProjectId } = useApiProject()
  // const { data: projects } = getAllProjects()
  const result = id ? await getUsersByProjectId(id) : null

  const { getProjetByUserId, getUserProfil } = useApiUser()
  const { data: user } = await getUserProfil()
  const userId = user.value?.id || 0
  const { data: projects } = await getProjetByUserId(userId)

  const users = computed(() => result?.data.value ?? [])
  const pending = computed(() => result?.status.value === 'pending')
  const error = computed(() => result?.error.value)
  const options = computed(() =>
    (projects.value ?? []).map((p) => ({ id: p.id, label: p.name })),
  )
  const current = computed(() => projects.value?.find((p) => p.id === id))
</script>

<style scoped></style>
