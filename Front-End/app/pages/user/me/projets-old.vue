<template>
  <div>
    <AppPage
      title="Vos Projets"
      :subtitle="
        current
          ? `Projets de ${fullName(current)}`
          : 'Voici la liste de vos projets.'
      "
    >
      <DataState
        :pending="pending"
        :error="error"
        :empty="!projects.length"
        empty-text="Cet utilisateur n’a aucun projet."
      >
        <ProjectCards :items="projects" />
      </DataState>
    </AppPage>
  </div>
</template>

<script setup lang="ts">
  import { fullName } from '~/utils/format'

  definePageMeta({ key: (route) => route.fullPath })

  const { getProjetByUserId, getUserProfil } = useApiUser()
  const { data: user } = await getUserProfil()
  const id = user.value?.id || 0
  const result = id ? await getProjetByUserId(id) : null

  const projects = computed(() => result?.data.value ?? [])
  const pending = computed(() => result?.status.value === 'pending')
  const error = computed(() => result?.error.value)
  const current = computed(() => user.value)
</script>

<style scoped></style>
