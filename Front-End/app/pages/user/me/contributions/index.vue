<template>
  <div>
    <AppPage title="Contributions" subtitle="Le temps passé sur vos projets.">
      <template #actions>
        <NuxtLink to="/user/me/contributions/save" role="button" class="ap-btn"
          >+ Nouvelle contribution</NuxtLink
        >
        <NuxtLink
          to="/user/me/contributions/all"
          role="button"
          class="ap-btn ap-ghost"
          >Tout voir</NuxtLink
        >
      </template>

      <DataState
        :pending="status === 'pending'"
        :error="error"
        :empty="!contributions?.length"
        empty-text="Aucune contribution enregistrée."
      >
        <div class="ap-grid">
          <article class="ap-stat">
            <strong>{{ contributions?.length }}</strong
            ><span>Contributions</span>
          </article>
          <article class="ap-stat">
            <strong>{{ total }}</strong
            ><span>Durée totale</span>
          </article>
          <article class="ap-stat">
            <strong>{{ average }}</strong
            ><span>Durée moyenne</span>
          </article>
        </div>

        <div class="ap-section-title">
          <h3>Les plus récentes</h3>
          <NuxtLink to="/user/me/contributions/all"
            >Voir la liste complète →</NuxtLink
          >
        </div>
        <ContributionTableMe :items="recent" />
      </DataState>
    </AppPage>
  </div>
</template>

<script setup lang="ts">
  const { getContributionByUserId, getUserProfil } = useApiUser()
  const { data: user } = await getUserProfil()
  const id = user.value?.id || 0

  // const {
  //   data: contributions,
  //   status,
  //   error,
  // } = useApiContribution().getAllContributions()

  const {
    data: contributions,
    status,
    error,
  } = await getContributionByUserId(id)

  const recent = computed(() =>
    [...(contributions.value ?? [])]
      .sort(
        (a, b) => +new Date(b.dateContribution) - +new Date(a.dateContribution),
      )
      .slice(0, 6),
  )
  const total = computed(() =>
    (contributions.value ?? []).reduce((s, c) => s + c.duration, 0),
  )
  const average = computed(() =>
    contributions.value?.length
      ? Math.round((total.value / contributions.value.length) * 10) / 10
      : 0,
  )
</script>

<style scoped></style>
