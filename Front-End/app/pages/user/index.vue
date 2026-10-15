<template>
  <div>
    <AppPage
      title="Utilisateurs"
      subtitle="Les membres de l’équipe et leurs rôles."
    >
      <template #actions>
        <NuxtLink to="/user/projects" role="button" class="ap-btn"
          >Projets par user</NuxtLink
        >
        <NuxtLink to="/user/contributions" role="button" class="ap-btn"
          >Contributions par user</NuxtLink
        >
        <NuxtLink
          v-if="hasRole('ADMIN')"
          to="/user/save"
          role="button"
          class="ap-btn"
          >+ Nouvel utilisateur</NuxtLink
        >
        <NuxtLink to="/user/all" role="button" class="ap-btn ap-ghost"
          >Tout voir</NuxtLink
        >
      </template>

      <DataState
        :pending="status === 'pending'"
        :error="error"
        :empty="!users?.length"
        empty-text="Aucun utilisateur pour le moment."
      >
        <div class="ap-grid">
          <article v-if="hasRole('ADMIN')" class="ap-stat">
            <strong>{{ users?.length }}</strong
            ><span>Utilisateurs</span>
          </article>
          <article v-if="hasRole('ADMIN')" class="ap-stat">
            <strong>{{ admins }}</strong
            ><span>Administrateurs</span>
          </article>
          <article v-if="hasRole('ADMIN')" class="ap-stat">
            <strong>{{ managers }}</strong
            ><span>Responsables</span>
          </article>
          <article class="ap-stat">
            <strong>{{ developers }}</strong
            ><span>Developpeurs</span>
          </article>
        </div>

        <div class="ap-section-title">
          <h3>Derniers inscrits</h3>
          <NuxtLink to="/user/all">Voir la liste complète →</NuxtLink>
        </div>
        <UserCards :items="recent" />
      </DataState>
    </AppPage>
  </div>
</template>

<script setup lang="ts">
  const { data: users, status, error } = await useApiUser().getAllUsers()
  const { hasRole } = useAuth()
  const recent = computed(() => {
    if (!hasRole('ADMIN')) {
      users.value = users.value?.filter((u) => u.roles.includes('DEVELOPER'))
    }
    return [...(users.value ?? [])].sort((a, b) => b.id - a.id).slice(0, 6)
  })
  const admins = computed(
    () => (users.value ?? []).filter((u) => u.roles?.includes('ADMIN')).length,
  )
  const managers = computed(
    () =>
      (users.value ?? []).filter((u) => u.roles?.includes('MANAGER')).length,
  )
  const developers = computed(
    () =>
      (users.value ?? []).filter((u) => u.roles?.includes('DEVELOPER')).length,
  )
</script>

<style scoped></style>
