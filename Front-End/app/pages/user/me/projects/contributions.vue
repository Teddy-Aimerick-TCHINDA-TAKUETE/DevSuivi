<template>
  <div>
    <AppPage
      title="Contributions du projet"
      :subtitle="
        current
          ? `Temps passé sur « ${current.name} »`
          : 'Choisissez un projet pour voir ses contributions.'
      "
      back="/user/me/projects"
    >
      <template #actions>
        <NuxtLink
          v-if="id"
          :to="`/user/me/contributions/save?project=${id}`"
          role="button"
          class="ap-btn"
          >+ Contribution</NuxtLink
        >
      </template>

      <EntityPicker label="Projet" :options="options" :current="id" />

      <p v-if="!id" class="ap-empty ap-muted">
        Sélectionnez un projet ci-dessus.
      </p>
      <DataState
        v-else
        :pending="pending"
        :error="error"
        :empty="!contributions.length"
        empty-text="Aucune contribution sur ce projet."
      >
        <div class="ap-grid">
          <article class="ap-stat" :class="{ 'ap-over': overQuota }">
            <strong>{{ totalDuration }}</strong
            ><span>Durée cumulée du projet</span>
            <OverQuotaPill
              :used="totalDuration"
              :quota="current?.quota ?? Infinity"
            />
          </article>
        </div>
        <DateFilter
          v-model:from="from"
          v-model:to="to"
          :count="filtered.length"
          :total="sumDuration(filtered)"
        />
        <p v-if="!filtered.length" class="ap-empty ap-muted">
          Aucune contribution sur cette période.
        </p>
        <ContributionTableMe v-else :items="filtered" />
      </DataState>
    </AppPage>
  </div>
</template>

<script setup lang="ts">
  import { isOverQuota, sumDuration } from '~/utils/projectRules'

  definePageMeta({ key: (route) => route.fullPath })

  const id = Number(useRoute().query.id) || 0
  const { getAllProjects, getContributiosByProjectId } = useApiProject()
  // const { data: projects } = getAllProjects()
  const result = id ? await getContributiosByProjectId(id) : null

  const { getProjectByUserId, getUserProfil } = useApiUser()
  const { data: user } = await getUserProfil()
  const userId = user.value?.id || 0
  const { data: projects } = await getProjectByUserId(userId)

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
    (projects.value ?? []).map((p) => ({ id: p.id, label: p.name })),
  )
  const current = computed(() => projects.value?.find((p) => p.id === id))
  const totalDuration = computed(() => sumDuration(contributions.value))
  const overQuota = computed(
    () =>
      !!current.value && isOverQuota(totalDuration.value, current.value.quota),
  )
</script>

<style scoped></style>
