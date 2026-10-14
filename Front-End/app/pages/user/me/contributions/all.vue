<template>
  <div>
    <AppPage
      title="Toutes les contributions"
      subtitle="Classées de la plus récente à la plus ancienne."
      back="/user/me/contributions"
    >
      <template #actions>
        <NuxtLink to="/user/me/contributions/save" role="button" class="ap-btn"
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
                <th>Durée</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="c in filtered" :key="c.id">
                <!-- <td class="ap-muted">{{ c.id }}</td> -->
                <td>{{ formatDate(c.dateContribution) }}</td>
                <td>
                  <span class="ap-pill">{{ c.duration }}</span>
                </td>
                <td class="ap-cell-actions">
                  <NuxtLink
                    :to="`/user/me/contributions/${c.id}`"
                    role="button"
                    class="ap-btn ap-sm secondary outline"
                    >Détails</NuxtLink
                  >
                  <NuxtLink
                    :to="`/user/me/contributions/save?id=${c.id}`"
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

  const { getContributionByUserId, getUserProfil } = useApiUser()
  const { data: user } = await getUserProfil()
  const id = user.value?.id || 0

  const { getAllContributions, deleteContribution } = useApiContribution()
  const {
    data: contributions,
    status,
    error,
    refresh,
  } = await getContributionByUserId(id)
  // const { data: contributions, status, error, refresh } = getAllContributions()
  const message = ref('')

  const sorted = computed(() =>
    [...(contributions.value ?? [])].sort(
      (a, b) => +new Date(b.dateContribution) - +new Date(a.dateContribution),
    ),
  )

  // Filtre par date (bornes incluses)
  const from = ref('')
  const to = ref('')
  const filtered = computed(() =>
    filterByDate(sorted.value, from.value, to.value),
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
