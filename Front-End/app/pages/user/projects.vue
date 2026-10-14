<template>
  <div>
    <AppPage
      title="Projets d’un utilisateur"
      :subtitle="
        current
          ? `Projets de ${fullName(current)}`
          : 'Choisissez un utilisateur pour voir ses projets.'
      "
      back="/user"
    >
      <EntityPicker label="Utilisateur" :options="options" :current="id" />

      <p v-if="!id" class="ap-empty ap-muted">
        Sélectionnez un utilisateur ci-dessus.
      </p>
      <DataState
        v-else
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

  const id = Number(useRoute().query.id) || 0
  const { getAllUsers, getProjectByUserId } = useApiUser()
  const { data: users } = await getAllUsers()
  const result = id ? await getProjectByUserId(id) : null

  const projects = computed(() => result?.data.value ?? [])
  const pending = computed(() => result?.status.value === 'pending')
  const error = computed(() => result?.error.value)
  const options = computed(() =>
    (users.value ?? []).map((u) => ({ id: u.id, label: fullName(u) })),
  )
  const current = computed(() => users.value?.find((u) => u.id === id))
</script>

<style scoped></style>
