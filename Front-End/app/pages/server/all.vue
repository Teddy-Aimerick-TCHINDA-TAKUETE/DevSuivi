<template>
  <div>
    <AppPage
      title="Tous les serveurs"
      subtitle="Gérez votre parc de serveurs."
      back="/server"
    >
      <template #actions>
        <NuxtLink to="/server/save" role="button" class="ap-btn"
          >+ Nouveau serveur</NuxtLink
        >
      </template>

      <input
        v-model="search"
        type="search"
        placeholder="Rechercher un serveur…"
      />
      <article v-if="message" class="ap-error">{{ message }}</article>

      <DataState
        :pending="status === 'pending'"
        :error="error"
        :empty="!filtered.length"
        empty-text="Aucun serveur trouvé."
      >
        <div class="ap-table">
          <table class="striped">
            <thead>
              <tr>
                <th>Nom</th>
                <th>Description</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="s in filtered" :key="s.id">
                <td>
                  <NuxtLink :to="`/server/${s.id}`"
                    ><strong>{{ s.name }}</strong></NuxtLink
                  >
                </td>
                <td class="ap-muted">{{ s.description }}</td>
                <td class="ap-cell-actions">
                  <NuxtLink
                    :to="`/server/projects?id=${s.id}`"
                    role="button"
                    class="ap-btn ap-sm secondary outline"
                    >Projets</NuxtLink
                  >
                  <NuxtLink
                    :to="`/server/save?id=${s.id}`"
                    role="button"
                    class="ap-btn ap-sm outline"
                    >Modifier</NuxtLink
                  >
                  <button
                    class="ap-btn ap-sm danger"
                    @click="remove(s.id, s.name)"
                  >
                    Supprimer
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </DataState>
    </AppPage>
  </div>
</template>

<script setup lang="ts">
  import { errorMessage } from '~/utils/format'

  const { getAllServers, deleteServer } = useApiServer()
  const { data: servers, status, error, refresh } = await getAllServers()

  const search = ref('')
  const message = ref('')
  const filtered = computed(() => {
    const q = search.value.trim().toLowerCase()
    return (servers.value ?? []).filter(
      (s) => !q || `${s.name} ${s.description}`.toLowerCase().includes(q),
    )
  })

  async function remove(id: number, name: string) {
    if (
      !(await askConfirm({
        title: 'Supprimer le serveur',
        message: `« ${name} » sera supprimé définitivement, ainsi que tous les projets qu’il héberge.`,
        confirmLabel: 'Supprimer',
      }))
    )
      return
    const { error: err } = await deleteServer(id)
    message.value = err ? errorMessage(err) : ''
    await refresh()
  }
</script>

<style scoped></style>
