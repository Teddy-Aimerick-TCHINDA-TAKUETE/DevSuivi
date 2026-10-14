<template>
  <div>
    <AppPage
      title="Tableau de bord"
      subtitle="Projets, serveurs, équipes et contributions au même endroit."
    >
      <template v-if="hasRole('ADMIN', 'MANAGER')" #actions>
        <NuxtLink to="/projet/save" role="button" class="ap-btn"
          >+ Nouveau projet</NuxtLink
        >
        <NuxtLink to="/contribution/save" role="button" class="ap-btn ap-ghost"
          >+ Contribution</NuxtLink
        >
      </template>

      <div class="ap-grid">
        <NuxtLink v-for="s in sections" :key="s.to" :to="s.to" class="ap-card">
          <div class="ap-card-top">
            <span class="ap-icon">{{ s.icon }}</span>
            <span class="ap-pill">{{ s.count ?? '–' }}</span>
          </div>
          <h4>{{ s.label }}</h4>
          <p>{{ s.text }}</p>
        </NuxtLink>
      </div>

      <article v-if="!token">
        <h4>Vous n’êtes pas connecté</h4>
        <p class="ap-muted">
          Connectez-vous ou créez un compte pour commencer.
        </p>
        <div class="ap-actions">
          <NuxtLink to="/loging" role="button" class="ap-btn"
            >Se connecter</NuxtLink
          >
          <NuxtLink
            to="/register"
            role="button"
            class="ap-btn secondary outline"
            >Créer un compte</NuxtLink
          >
        </div>
      </article>
    </AppPage>
  </div>
</template>

<script setup lang="ts">
  import { useAuth } from '~/composables/useAuth'

  const { data: user } = await useApiUser().getUserProfil()
  const userId = user.value?.id || 0

  const { data: mesProjects } = await useApiUser().getProjetByUserId(userId)
  const { data: mesContributions } =
    await useApiUser().getContributionByUserId(userId)
  const { data: projects } = await useApiProject().getAllProjects()
  const { data: servers } = await useApiServer().getAllServers()
  const { data: users } = await useApiUser().getAllUsers()
  const { data: contributions } =
    await useApiContribution().getAllContributions()
  const { data: assigns } = await useApiAssign().getAllAssigns()
  const token = getAccessToken()
  const { hasRole } = useAuth()

  const sections = computed(() =>
    [
      {
        to: '/user/me/projets',
        icon: '📁',
        label: 'Mes projets',
        text: 'Suivre l’avancement, les quotas et les équipes.',
        count: mesProjects.value?.length,
        show: hasRole('DEVELOPER'),
      },
      {
        to: '/user/me/contributions',
        icon: '⏱️',
        label: 'Mes contributions',
        text: 'Le temps passé sur chaque projet.',
        count: mesContributions.value?.length,
        show: hasRole('DEVELOPER'),
      },
      {
        to: '/projet',
        icon: '📁',
        label: 'Projets',
        text: 'Suivre l’avancement, les quotas et les équipes.',
        count: projects.value?.length,
        show: hasRole('ADMIN', 'MANAGER'),
      },
      {
        to: '/serveur',
        icon: '🖥️',
        label: 'Serveurs',
        text: 'Les machines qui hébergent vos projets.',
        count: servers.value?.length,
        show: hasRole('ADMIN'),
      },
      {
        to: '/user',
        icon: '👥',
        label: 'Utilisateurs',
        text: 'Les membres et leurs rôles.',
        count: users.value?.length,
        show: hasRole('ADMIN'),
      },
      {
        to: '/contribution',
        icon: '⏱️',
        label: 'Contributions',
        text: 'Le temps passé sur chaque projet.',
        count: contributions.value?.length,
        show: hasRole('ADMIN', 'MANAGER'),
      },
      {
        to: '/assign',
        icon: '🔗',
        label: 'Affectations',
        text: 'Qui travaille sur quoi.',
        count: assigns.value?.length,
        show: hasRole('ADMIN', 'MANAGER'),
      },
    ].filter((l) => l.show),
  )
</script>

<style scoped></style>
