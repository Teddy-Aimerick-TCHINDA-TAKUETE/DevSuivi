<template>
  <div>
    <AppPage
      :title="server?.name ?? 'Serveur'"
      :subtitle="server?.description"
      back="/serveur/all"
    >
      <template #actions>
        <NuxtLink :to="`/serveur/save?id=${id}`" role="button" class="ap-btn"
          >Modifier</NuxtLink
        >
        <button class="ap-btn danger" @click="remove">Supprimer</button>
      </template>

      <article v-if="message" class="ap-error">{{ message }}</article>

      <DataState :pending="status === 'pending'" :error="error">
        <div class="ap-grid">
          <article class="ap-stat">
            <strong>{{ projects?.length ?? 0 }}</strong
            ><span>Projets hébergés</span>
          </article>
        </div>

        <div class="ap-section-title">
          <h3>Projets hébergés</h3>
          <NuxtLink :to="`/serveur/projects?id=${id}`">Voir à part →</NuxtLink>
        </div>
        <ProjectCards v-if="projects?.length" :items="projects" />
        <p v-else class="ap-empty ap-muted">Aucun projet sur ce serveur.</p>
      </DataState>
    </AppPage>
  </div>
</template>

<script setup lang="ts">
  import { errorMessage } from '~/utils/format'

  const id = Number(useRoute().params.id)
  const { getServerById, getProjetsByServerId, deleteServer } = useApiServer()
  const { data: server, status, error } = await getServerById(id)
  const { data: projects } = await getProjetsByServerId(id)
  const message = ref('')

  async function remove() {
    if (!confirm(`Supprimer le serveur « ${server.value?.name} » ?`)) return
    const { error: err } = await deleteServer(id)
    if (err.value) {
      message.value = errorMessage(err.value)
      return
    }
    await navigateTo('/serveur/all')
  }
</script>

<style scoped></style>
