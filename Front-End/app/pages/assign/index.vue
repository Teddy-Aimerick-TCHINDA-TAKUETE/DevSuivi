<template>
  <div>
    <AppPage title="Affectations" subtitle="Qui travaille sur quel projet.">
      <template #actions>
        <NuxtLink to="/assign/save" role="button" class="ap-btn"
          >+ Nouvelle affectation</NuxtLink
        >
        <NuxtLink to="/assign/all" role="button" class="ap-btn ap-ghost"
          >Tout voir</NuxtLink
        >
      </template>

      <DataState
        :pending="status === 'pending'"
        :error="error"
        :empty="!assigns?.length"
        empty-text="Aucune affectation pour le moment."
      >
        <div class="ap-grid">
          <article class="ap-stat">
            <strong>{{ assigns?.length }}</strong
            ><span>Affectations</span>
          </article>
          <article class="ap-stat">
            <strong>{{ userCount }}</strong
            ><span>Utilisateurs affectés</span>
          </article>
          <article class="ap-stat">
            <strong>{{ projectCount }}</strong
            ><span>Projets staffés</span>
          </article>
        </div>

        <div class="ap-section-title">
          <h3>Dernières affectations</h3>
          <NuxtLink to="/assign/all">Voir la liste complète →</NuxtLink>
        </div>
        <div class="ap-grid">
          <NuxtLink
            v-for="a in recent"
            :key="`${a.user.id}-${a.project.id}`"
            :to="`/assign/id?user=${a.user.id}&project=${a.project.id}`"
            class="ap-card"
          >
            <div class="ap-person">
              <span class="ap-avatar">{{ initials(a.user) }}</span>
              <div>
                <h4>{{ fullName(a.user) }}</h4>
                <small>→ {{ a.project.name }}</small>
              </div>
            </div>
          </NuxtLink>
        </div>
      </DataState>
    </AppPage>
  </div>
</template>

<script setup lang="ts">
  import { fullName, initials } from '~/utils/format'

  const { data: assigns, status, error } = await useApiAssign().getAllAssigns()
  const recent = computed(() => (assigns.value ?? []).slice(-6).reverse())
  const userCount = computed(
    () => new Set((assigns.value ?? []).map((a) => a.user.id)).size,
  )
  const projectCount = computed(
    () => new Set((assigns.value ?? []).map((a) => a.project.id)).size,
  )
</script>

<style scoped></style>
