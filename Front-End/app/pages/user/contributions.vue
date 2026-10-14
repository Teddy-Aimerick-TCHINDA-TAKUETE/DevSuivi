<template>
  <div>
    <AppPage
      title="Contributions d’un utilisateur"
      :subtitle="
        current
          ? `Temps passé par ${fullName(current)}`
          : 'Choisissez un utilisateur pour voir ses contributions.'
      "
      back="/user"
    >
      <template #actions>
        <NuxtLink
          v-if="id"
          :to="`/contribution/save?user=${id}`"
          role="button"
          class="ap-btn"
          >+ Contribution</NuxtLink
        >
      </template>

      <EntityPicker label="Utilisateur" :options="options" :current="id" />

      <p v-if="!id" class="ap-empty ap-muted">
        Sélectionnez un utilisateur ci-dessus.
      </p>
      <DataState
        v-else
        :pending="pending"
        :error="error"
        :empty="!contributions.length"
        empty-text="Aucune contribution pour cet utilisateur."
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
        <ContributionTable v-else :items="filtered" />
      </DataState>
    </AppPage>
  </div>
</template>

<script setup lang="ts">
  import { fullName } from '~/utils/format'

  import { sumDuration } from '~/utils/projectRules'

  definePageMeta({ key: (route) => route.fullPath })

  const id = Number(useRoute().query.id) || 0
  const { getAllUsers, getContributionByUserId } = useApiUser()
  const { data: users } = await getAllUsers()
  const result = id ? await getContributionByUserId(id) : null

  const contributions = computed(() => result?.data.value ?? [])
  // Filtre par date (bornes incluses)
  const from = ref('')
  const to = ref('')
  const filtered = computed(() =>
    filterByDate(contributions.value, from.value, to.value),
  )
  const pending = computed(() => result?.status.value === 'pending')
  const error = computed(() => result?.error.value)
  const options = computed(() =>
    (users.value ?? []).map((u) => ({ id: u.id, label: fullName(u) })),
  )
  const current = computed(() => users.value?.find((u) => u.id === id))
</script>

<style scoped></style>
