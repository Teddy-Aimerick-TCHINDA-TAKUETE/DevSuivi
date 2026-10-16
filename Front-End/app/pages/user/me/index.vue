<template>
  <div>
    <AppPage
      :title="user ? fullName(user) : 'Utilisateur'"
      :subtitle="user?.email"
    >
      <article v-if="message" class="ap-error">{{ message }}</article>

      <DataState :pending="status === 'pending'" :error="error">
        <template v-if="user">
          <article>
            <div class="ap-person">
              <span class="ap-avatar ap-lg">{{ initials(user) }}</span>
              <div>
                <h3>{{ fullName(user) }}</h3>
                <small>{{ user.email }}</small>
                <p style="margin: 0.4rem 0 0">
                  <span
                    v-for="r in user.roles"
                    :key="r"
                    class="ap-pill"
                    :data-tone="roleTone(r)"
                    >{{ r }}</span
                  >
                </p>
              </div>
            </div>
          </article>

          <p v-if="!isDeveloper" class="ap-empty ap-muted">
            Votre compte n’a pas le rôle DEVELOPER : vous n’avez ni projet ni
            contribution.
          </p>

          <template v-if="isDeveloper">
            <div class="ap-grid">
              <article class="ap-stat">
                <strong>{{ formatDuration(totalDuration) }}</strong
                ><span>Durée cumulée de mes contributions</span>
              </article>
            </div>

            <div class="ap-section-title">
              <h3>Durée cumulée par projet</h3>
            </div>
            <ProjectDurationTable
              v-if="contributions?.length"
              :items="contributions"
              base="/user/me/projects"
            />
            <p v-else class="ap-empty ap-muted">
              Aucune contribution enregistrée.
            </p>

            <div class="ap-section-title">
              <h3>Projets</h3>
              <NuxtLink :to="`/user/me/projects`">Voir tout →</NuxtLink>
            </div>
            <ProjectCardsMe v-if="projects?.length" :items="projects" />
            <p v-else class="ap-empty ap-muted">
              Aucun projet pour cet utilisateur.
            </p>

            <div class="ap-section-title">
              <h3>Contributions</h3>
              <NuxtLink :to="`/user/me/contributions`">Voir tout →</NuxtLink>
            </div>
            <ContributionTableMe
              v-if="contributions?.length"
              :items="contributions"
              show-project
            />
            <p v-else class="ap-empty ap-muted">
              Aucune contribution enregistrée.
            </p>

            <div class="ap-links">
              <NuxtLink
                :to="`/user/me/contributions/save?user=${user.id}`"
                role="button"
                class="ap-btn secondary outline"
                >+ Ajouter une contribution</NuxtLink
              >
            </div>
          </template>
        </template>
      </DataState>
    </AppPage>
  </div>
</template>

<script setup lang="ts">
  import { fullName, initials, errorMessage, roleTone } from '~/utils/format'
  import { sumDuration } from '~/utils/projectRules'

  const { getUserProfil, getProjectByUserId, getContributionByUserId } =
    useApiUser()
  const { data: user, status, error } = await getUserProfil()
  const { data: projects } = await getProjectByUserId(user.value?.id ?? 0)
  const { data: contributions } = await getContributionByUserId(
    user.value?.id ?? 0,
  )
  const message = ref('')

  // Seuls les développeurs ont des projets et des contributions
  const isDeveloper = computed(() => !!user.value?.roles?.includes('DEVELOPER'))
  const totalDuration = computed(() => sumDuration(contributions.value ?? []))
</script>

<style scoped></style>
