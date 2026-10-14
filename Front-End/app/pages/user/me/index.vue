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

          <div class="ap-section-title">
            <h3>Projets</h3>
            <NuxtLink :to="`/user/me/projets`">Voir tout →</NuxtLink>
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
      </DataState>
    </AppPage>
  </div>
</template>

<script setup lang="ts">
  import { fullName, initials, errorMessage, roleTone } from '~/utils/format'

  const { getUserProfil, getProjetByUserId, getContributionByUserId } =
    useApiUser()
  const { data: user, status, error } = await getUserProfil()
  const { data: projects } = await getProjetByUserId(user.value?.id ?? 0)
  const { data: contributions } = await getContributionByUserId(
    user.value?.id ?? 0,
  )
  const message = ref('')
</script>

<style scoped></style>
