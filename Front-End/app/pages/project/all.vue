<template>
  <div>
    <AppPage
      title="Tous les projets"
      subtitle="Recherchez, modifiez ou supprimez vos projets."
      back="/project"
    >
      <template #actions>
        <NuxtLink to="/project/save" role="button" class="ap-btn"
          >+ Nouveau projet</NuxtLink
        >
      </template>

      <input
        v-model="search"
        type="search"
        placeholder="Rechercher un projet, un statut, un serveur…"
      />
      <article v-if="message" class="ap-error">{{ message }}</article>

      <DataState
        :pending="status === 'pending'"
        :error="error"
        :empty="!filtered.length"
        empty-text="Aucun projet trouvé."
      >
        <div class="ap-table">
          <table class="striped">
            <thead>
              <tr>
                <th>Nom</th>
                <th>Statut</th>
                <th>Quota (h)</th>
                <th>Serveur</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="p in filtered" :key="p.id">
                <td>
                  <NuxtLink :to="`/project/${p.id}`"
                    ><strong>{{ p.name }}</strong></NuxtLink
                  >
                </td>
                <td>
                  <span class="ap-pill" :data-tone="statusTone(p.status)">{{
                    p.status
                  }}</span>
                  <OverQuotaPill :used="p.totalDuration" :quota="p.quota" />
                  <ProjectStatusSelect :project="p" @changed="refresh()" />
                </td>
                <td>{{ p.quota }} h</td>
                <td>
                  <NuxtLink v-if="p.server" :to="`/server/${p.server.id}`">{{
                    p.server.name
                  }}</NuxtLink>
                  <span v-else class="ap-muted">—</span>
                </td>
                <td class="ap-cell-actions">
                  <NuxtLink
                    :to="`/project/save?id=${p.id}`"
                    role="button"
                    class="ap-btn ap-sm outline"
                    >Modifier</NuxtLink
                  >
                  <button
                    class="ap-btn ap-sm danger"
                    @click="remove(p.id, p.name)"
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
  import { errorMessage, statusTone } from '~/utils/format'

  const { getAllProjects, deleteProject } = useApiProject()
  const { data: projects, status, error, refresh } = await getAllProjects()

  const search = ref('')
  const message = ref('')

  const filtered = computed(() => {
    const q = search.value.trim().toLowerCase()
    return (projects.value ?? []).filter(
      (p) =>
        !q ||
        `${p.name} ${p.status} ${p.server?.name ?? ''}`
          .toLowerCase()
          .includes(q),
    )
  })

  async function remove(id: number, name: string) {
    if (
      !(await askConfirm({
        title: 'Supprimer le projet',
        message: `« ${name} » sera supprimé définitivement, ainsi que ses affectations et ses contributions.`,
        confirmLabel: 'Supprimer',
      }))
    )
      return
    const { error: err } = await deleteProject(id)
    message.value = err.value ? errorMessage(err.value) : ''
    await refresh()
  }
</script>

<style scoped></style>
