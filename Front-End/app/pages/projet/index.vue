<template>
  <div>
    <AppPage title="Projets" subtitle="Vue d’ensemble de vos projets.">
      <template #actions>
        <NuxtLink to="/projet/users" role="button" class="ap-btn"
          >Users par projet</NuxtLink
        >
        <NuxtLink to="/projet/contributions" role="button" class="ap-btn"
          >Contributions par projet</NuxtLink
        >
        <NuxtLink to="/projet/save" role="button" class="ap-btn"
          >+ Nouveau projet</NuxtLink
        >
        <NuxtLink to="/projet/all" role="button" class="ap-btn ap-ghost"
          >Tout voir</NuxtLink
        >
      </template>

      <DataState
        :pending="status === 'pending'"
        :error="error"
        :empty="!projects?.length"
        empty-text="Aucun projet pour le moment. Créez le premier !"
      >
        <div class="ap-grid">
          <article class="ap-stat">
            <strong>{{ projects?.length }}</strong
            ><span>Projets</span>
          </article>
          <article class="ap-stat">
            <strong>{{ totalQuota }}</strong
            ><span>Quota cumulé</span>
          </article>
          <article class="ap-stat">
            <strong>{{ serverCount }}</strong
            ><span>Serveurs utilisés</span>
          </article>
        </div>

        <div class="ap-section-title">
          <h3>Derniers projets</h3>
          <NuxtLink to="/projet/all">Voir la liste complète →</NuxtLink>
        </div>
        <ProjectCards :items="recent" />
      </DataState>
    </AppPage>
  </div>
</template>

<script setup lang="ts">
  const {
    data: projects,
    status,
    error,
  } = await useApiProject().getAllProjects()

  const recent = computed(() =>
    [...(projects.value ?? [])].sort((a, b) => b.id - a.id).slice(0, 6),
  )
  const totalQuota = computed(() =>
    (projects.value ?? []).reduce((sum, p) => sum + (p.quota ?? 0), 0),
  )
  const serverCount = computed(
    () => new Set((projects.value ?? []).map((p) => p.server?.id)).size,
  )
</script>

<style scoped></style>
