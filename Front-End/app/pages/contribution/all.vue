<template>
  <div>
    <AppPage
      title="Toutes les contributions"
      subtitle="Classées de la plus récente à la plus ancienne."
      back="/contribution"
    >
      <template #actions>
        <NuxtLink to="/contribution/save" role="button" class="ap-btn"
          >+ Nouvelle contribution</NuxtLink
        >
      </template>

      <article v-if="message" class="ap-error">{{ message }}</article>

      <DataState
        :pending="status === 'pending'"
        :error="error"
        :empty="!sorted.length"
        empty-text="Aucune contribution enregistrée."
      >
        <DateFilter
          v-model:from="from"
          v-model:to="to"
          v-model:user="user"
          v-model:project="project"
          :users="developers"
          :projects="projectChoices"
          :count="filtered.length"
          :total="sumDuration(filtered)"
        />
        <p v-if="!filtered.length" class="ap-empty ap-muted">
          Aucune contribution sur cette période.
        </p>
        <div v-else class="ap-table">
          <table class="striped">
            <thead>
              <tr>
                <!-- <th>#</th> -->
                <th>Date</th>
                <th>Développeur</th>
                <th>Projet</th>
                <th>Durée</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="c in filtered" :key="c.id">
                <!-- <td class="ap-muted">{{ c.id }}</td> -->
                <td>{{ formatDate(c.dateContribution) }}</td>
                <td>{{ c.userName ?? '—' }}</td>
                <td>{{ c.projectName ?? '—' }}</td>
                <td>
                  <span class="ap-pill">{{ formatDuration(c.duration) }}</span>
                </td>
                <td class="ap-cell-actions">
                  <NuxtLink
                    :to="`/contribution/${c.id}`"
                    role="button"
                    class="ap-btn ap-sm secondary outline"
                    >Détails</NuxtLink
                  >
                  <NuxtLink
                    :to="`/contribution/save?id=${c.id}`"
                    role="button"
                    class="ap-btn ap-sm outline"
                    >Modifier</NuxtLink
                  >
                  <button class="ap-btn ap-sm danger" @click="remove(c.id)">
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
  import { errorMessage, formatDate } from '~/utils/format'
  import { sumDuration } from '~/utils/projectRules'

  const { getAllContributions, deleteContribution } = useApiContribution()
  const {
    data: contributions,
    status,
    error,
    refresh,
  } = await getAllContributions()
  const message = ref('')

  const sorted = computed(() =>
    [...(contributions.value ?? [])].sort(
      (a, b) => +new Date(b.dateContribution) - +new Date(a.dateContribution),
    ),
  )

  // Filtres : développeur et projet (liés l'un à l'autre, 0 = tous) + période
  const from = ref('')
  const to = ref('')
  const user = ref(0)
  const project = ref(0)
  const developers = computed(() => userOptions(sorted.value, project.value))
  const projectChoices = computed(() =>
    projectOptions(sorted.value, user.value),
  )
  const filtered = computed(() =>
    filterContributions(sorted.value, {
      user: user.value,
      project: project.value,
      from: from.value,
      to: to.value,
    }),
  )

  async function remove(id: number) {
    if (
      !(await askConfirm({
        title: 'Supprimer la contribution',
        message: 'Cette contribution sera supprimée définitivement.',
        confirmLabel: 'Supprimer',
      }))
    )
      return
    const { error: err } = await deleteContribution(id)
    message.value = err.value ? errorMessage(err.value) : ''
    await refresh()
  }
</script>

<style scoped></style>
