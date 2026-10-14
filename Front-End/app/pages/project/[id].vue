<template>
  <div>
    <AppPage
      :title="project?.name ?? 'Projet'"
      :subtitle="project?.description"
      back="/project/all"
    >
      <template #actions>
        <NuxtLink :to="`/project/save?id=${id}`" role="button" class="ap-btn"
          >Modifier</NuxtLink
        >
        <button class="ap-btn danger" @click="remove">Supprimer</button>
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
              <strong>{{ project.quota }}</strong
              ><span>Quota</span>
            </article>
            <article class="ap-stat">
              <strong>{{ project.users?.length ?? 0 }}</strong
              ><span>Membres</span>
            </article>
            <article class="ap-stat" :class="{ 'ap-over': overQuota }">
              <strong>{{ usedTime }}</strong
              ><span>Durée cumulée</span>
              <OverQuotaPill :used="usedTime" :quota="project.quota" />
            </article>
          </div>

          <label>
            Changer l’état du projet
            <ProjectStatusSelect
              :project="project"
              @changed="project.status = $event"
            />
          </label>

          <label>
            Durée cumulée / quota
            <progress
              :value="usedTime"
              :max="project.quota || 1"
              :style="
                overQuota ? { '--pico-progress-color': '#dc2626' } : undefined
              "
            />
          </label>

          <p v-if="project.server">
            Hébergé sur
            <NuxtLink :to="`/server/${project.server.id}`"
              ><strong>{{ project.server.name }}</strong></NuxtLink
            >
          </p>

          <div class="ap-section-title">
            <h3>Équipe</h3>
            <NuxtLink :to="`/project/users?id=${id}`">Voir tout →</NuxtLink>
          </div>
          <UserCards v-if="project.users?.length" :items="project.users" />
          <p v-else class="ap-empty ap-muted">
            Personne n’est encore affecté à ce projet.
          </p>

          <div class="ap-section-title">
            <h3>Contributions</h3>
            <NuxtLink :to="`/project/contributions?id=${id}`"
              >Voir tout →</NuxtLink
            >
          </div>
          <ContributionTable
            v-if="project.contributions?.length"
            :items="project.contributions"
          />
          <p v-else class="ap-empty ap-muted">
            Aucune contribution enregistrée.
          </p>

          <div class="ap-links">
            <NuxtLink
              :to="`/assign/save?project=${id}`"
              role="button"
              class="ap-btn secondary outline"
              >+ Affecter un utilisateur</NuxtLink
            >
            <NuxtLink
              :to="`/contribution/save?project=${id}`"
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
  import { isOverQuota, sumDuration } from '~/utils/projectRules'

  const id = Number(useRoute().params.id)
  const { getProjectById, deleteProject } = useApiProject()
  const { data: project, status, error } = await getProjectById(id)
  const message = ref('')

  const usedTime = computed(() => sumDuration(project.value?.contributions))
  // Dépassement : durée cumulée > quota (signalement, l'état ne change pas)
  const overQuota = computed(() =>
    isOverQuota(usedTime.value, project.value?.quota),
  )

  async function remove() {
    if (
      !(await askConfirm({
        title: 'Supprimer le projet',
        message: `« ${project.value?.name} » sera supprimé définitivement, ainsi que ses affectations et ses contributions.`,
        confirmLabel: 'Supprimer',
      }))
    )
      return
    const { error: err } = await deleteProject(id)
    if (err.value) {
      message.value = errorMessage(err.value)
      return
    }
    await navigateTo('/project/all')
  }
</script>

<style scoped></style>
