<template>
  <div>
    <AppPage
      title="Contributions du projet"
      :subtitle="
        current
          ? `Temps passé sur « ${current.name} »`
          : 'Choisissez un projet pour voir ses contributions.'
      "
      back="/project"
    >
      <template #actions>
        <NuxtLink
          v-if="id"
          :to="`/contribution/save?project=${id}`"
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
            <strong>{{ formatDuration(totalDuration) }}</strong
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
          v-model:user="user"
          :users="developers"
          :count="filtered.length"
          :total="sumDuration(filtered)"
        />
        <p v-if="!filtered.length" class="ap-empty ap-muted">
          Aucune contribution sur cette période.
        </p>
        <ContributionTable v-else :items="filtered" show-user />
      </DataState>
    </AppPage>
  </div>
</template>

<script setup lang="ts">
  import { isOverQuota, sumDuration } from '~/utils/projectRules'

  definePageMeta({ key: (route) => route.fullPath })

  const id = Number(useRoute().query.id) || 0
  const { getAllProjects, getContributiosByProjectId } = useApiProject()
  const { data: projects } = await getAllProjects()
  const result = id ? await getContributiosByProjectId(id) : null

  const contributions = computed(() => result?.data.value ?? [])
  // Filtres : développeur (0 = tous) et date (bornes incluses)
  const from = ref('')
  const to = ref('')
  const user = ref(0)
  const developers = computed(() => distinctUsers(contributions.value))
  const filtered = computed(() =>
    filterContributions(contributions.value, {
      user: user.value,
      from: from.value,
      to: to.value,
    }),
  )
  const pending = computed(() => result?.status.value === 'pending')
  const error = computed(() => result?.error.value)
  const options = computed(() =>
    (projects.value ?? []).map((p) => ({ id: p.id, label: p.name })),
  )
  const current = computed(() => projects.value?.find((p) => p.id === id))
  // Durée cumulée de toutes les contributions du projet (le filtre par date ne la change pas)
  const totalDuration = computed(() => sumDuration(contributions.value))
  const overQuota = computed(
    () =>
      !!current.value && isOverQuota(totalDuration.value, current.value.quota),
  )
</script>

<style scoped></style>
