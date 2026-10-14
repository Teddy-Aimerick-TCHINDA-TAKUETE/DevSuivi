<template>
  <div>
    <AppPage
      title="Projets d’un serveur"
      :subtitle="
        current
          ? `Hébergés sur « ${current.name} »`
          : 'Choisissez un serveur pour voir ses projets.'
      "
      back="/server"
    >
      <EntityPicker label="Serveur" :options="options" :current="id" />

      <p v-if="!id" class="ap-empty ap-muted">
        Sélectionnez un serveur ci-dessus.
      </p>
      <DataState
        v-else
        :pending="pending"
        :error="error"
        :empty="!projects.length"
        empty-text="Aucun projet sur ce serveur."
      >
        <ProjectCards :items="projects" />
      </DataState>
    </AppPage>
  </div>
</template>

<script setup lang="ts">
  definePageMeta({ key: (route) => route.fullPath })

  const id = Number(useRoute().query.id) || 0
  const { getAllServers, getProjectsByServerId } = useApiServer()
  const { data: servers } = await getAllServers()
  const result = id ? await getProjectsByServerId(id) : null

  const projects = computed(() => result?.data.value ?? [])
  const pending = computed(() => result?.status.value === 'pending')
  const error = computed(() => result?.error.value)
  const options = computed(() =>
    (servers.value ?? []).map((s) => ({ id: s.id, label: s.name })),
  )
  const current = computed(() => servers.value?.find((s) => s.id === id))
</script>

<style scoped></style>
