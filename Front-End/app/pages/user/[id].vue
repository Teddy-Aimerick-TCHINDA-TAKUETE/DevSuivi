<template>
  <div>
    <AppPage
      :title="user ? fullName(user) : 'Utilisateur'"
      :subtitle="user?.email"
      back="/user/all"
    >
      <template #actions>
        <NuxtLink :to="`/user/save?id=${id}`" role="button" class="ap-btn"
          >Modifier</NuxtLink
        >
        <button class="ap-btn danger" @click="remove">Supprimer</button>
      </template>

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
            <NuxtLink :to="`/user/projects?id=${id}`">Voir tout →</NuxtLink>
          </div>
          <ProjectCards v-if="user.projects?.length" :items="user.projects" />
          <p v-else class="ap-empty ap-muted">
            Aucun projet pour cet utilisateur.
          </p>

          <div class="ap-section-title">
            <h3>Contributions</h3>
            <NuxtLink :to="`/user/contributions?id=${id}`"
              >Voir tout →</NuxtLink
            >
          </div>
          <ContributionTable
            v-if="user.contributions?.length"
            :items="user.contributions"
          />
          <p v-else class="ap-empty ap-muted">
            Aucune contribution enregistrée.
          </p>

          <div v-if="user.roles?.includes('DEVELOPER')" class="ap-links">
            <NuxtLink
              :to="`/assign/save?user=${id}`"
              role="button"
              class="ap-btn secondary outline"
              >+ Affecter à un projet</NuxtLink
            >
            <NuxtLink
              :to="`/contribution/save?user=${id}`"
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

  const id = Number(useRoute().params.id)
  const { getUserById, deleteUser } = useApiUser()
  const { data: user, status, error } = await getUserById(id)
  const message = ref('')

  async function remove() {
    if (
      !(await askConfirm({
        title: 'Supprimer l’utilisateur',
        message: `« ${fullName(user.value)} » sera supprimé définitivement, ainsi que ses affectations et ses contributions.`,
        confirmLabel: 'Supprimer',
      }))
    )
      return
    const { error: err } = await deleteUser(id)
    if (err.value) {
      message.value = errorMessage(err.value)
      return
    }
    await navigateTo('/user/all')
  }
</script>

<style scoped></style>
