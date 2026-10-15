<template>
  <div>
    <AppPage
      :title="project?.name ?? 'Projet'"
      :subtitle="project?.description"
      back="/user/me/projects/all"
    >
      <!-- <template #actions>
        <NuxtLink :to="`/project/save?id=${id}`" role="button" class="ap-btn"
          >Modifier</NuxtLink
        >
        <button class="ap-btn danger" @click="remove">Supprimer</button>
      </template> -->
      <template #actions>
        <NuxtLink
          :to="`/user/me/contributions/save?project=${id}`"
          role="button"
          class="ap-btn"
          >+ Ajouter une contribution</NuxtLink
        >
      </template>

      <article v-if="message" class="ap-error">{{ message }}</article>

      <DataState :pending="status === 'pending'" :error="error">
        <template v-if="project">
          <div class="ap-grid">
            <article class="ap-stat">
              <strong
                ><span
                  class="ap-pill"
                  :data-tone="statusTone(project.status)"
                  >{{ project.status }}</span
                ></strong
              >
              <span>Statut</span>
            </article>
            <article class="ap-stat">
              <strong>{{ project.quota }} h</strong><span>Quota</span>
            </article>
            <article class="ap-stat">
              <strong>{{ project.users?.length ?? 0 }}</strong
              ><span>Membres</span>
            </article>
            <article class="ap-stat" :class="{ 'ap-over': overQuota }">
              <strong>{{ formatDuration(usedTime) }}</strong
              ><span>Durée cumulée</span>
              <OverQuotaPill :used="usedTime" :quota="project.quota" />
            </article>
            <article class="ap-stat" :class="{ 'ap-over': overQuota }">
              <strong>{{ formatDuration(myUsedTime) }}</strong
              ><span>Ma durée cumulée</span>
              <OverQuotaPill :used="usedTime" :quota="project.quota" />
            </article>
          </div>

          <label>
            Durée cumulée / quota
            <progress
              :value="usedTime"
              :max="quotaInMinutes(project.quota) || 1"
              :style="
                overQuota ? { '--pico-progress-color': '#dc2626' } : undefined
              "
            />
          </label>

          <p v-if="project.server">
            Hébergé sur
            <!-- <NuxtLink :to="`/server/${project.server.id}`"
              ><strong>{{ project.server.name }}</strong></NuxtLink
            > -->
            <strong>{{ project.server.name }}</strong>
          </p>

          <div class="ap-section-title">
            <h3>Équipe</h3>
            <NuxtLink :to="`/user/me/projects/users?id=${id}`"
              >Voir tout →</NuxtLink
            >
          </div>
          <UserCardsMe v-if="project.users?.length" :items="project.users" />
          <p v-else class="ap-empty ap-muted">
            Personne n’est encore affecté à ce projet.
          </p>

          <div class="ap-section-title">
            <h3>Mes contributions</h3>
            <NuxtLink :to="`/user/me/projects/contributions?id=${id}`"
              >Voir tout →</NuxtLink
            >
          </div>
          <ContributionTableMe
            v-if="myContributions.length"
            :items="myContributions"
          />
          <p v-else class="ap-empty ap-muted">
            Aucune contribution enregistrée.
          </p>

          <div class="ap-links">
            <!-- <NuxtLink
              :to="`/assign/save?project=${id}`"
              role="button"
              class="ap-btn secondary outline"
              >+ Affecter un utilisateur</NuxtLink
            > -->
            <NuxtLink
              :to="`/user/me/contributions/save?project=${id}`"
              role="button"
              class="ap-btn secondary outline"
              >+ Ajouter une contribution</NuxtLink
            >
          </div>
        </template>
      </DataState>
    </AppPage>
  </div>
</template>

<script setup lang="ts">
  import { errorMessage, statusTone } from '~/utils/format'
  import {
    isOverQuota,
    quotaInMinutes,
    sumDuration,
  } from '~/utils/projectRules'

  const id = Number(useRoute().params.id)
  const { getProjectById, deleteProject } = useApiProject()
  const { data: project, status, error } = await getProjectById(id)
  const message = ref('')
  const { data: user } = await useApiUser().getUserProfil()
  const { data: mine } = await useApiUser().getContributionByUserId(
    user.value?.id ?? 0,
  )

  const myContributions = computed(() => {
    const ids = new Set((mine.value ?? []).map((c) => c.id))
    return (project.value?.contributions ?? []).filter((c) => ids.has(c.id))
  })

  // const myContributions = computed(() => {
  //   return project.value?.contributions ?? []
  // })

  const usedTime = computed(() => sumDuration(project.value?.contributions))
  const myUsedTime = computed(() => sumDuration(myContributions.value))
  // Dépassement : durée cumulée > quota (signalement, l'état ne change pas)
  const overQuota = computed(() =>
    isOverQuota(usedTime.value, project.value?.quota),
  )
</script>

<style scoped></style>
