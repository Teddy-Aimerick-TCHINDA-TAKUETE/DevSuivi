<template>
  <div>
    <!-- :title="`Contribution #${id}`" -->
    <AppPage
      :title="'Contribution'"
      :subtitle="
        contribution ? formatDate(contribution.dateContribution) : undefined
      "
      back="/contribution/all"
    >
      <template #actions>
        <NuxtLink
          :to="`/contribution/save?id=${id}`"
          role="button"
          class="ap-btn"
          >Modifier</NuxtLink
        >
        <button class="ap-btn danger" @click="remove">Supprimer</button>
      </template>

      <article v-if="message" class="ap-error">{{ message }}</article>

      <DataState :pending="status === 'pending'" :error="error">
        <template v-if="contribution">
          <div class="ap-grid">
            <article class="ap-stat">
              <strong>{{ formatDuration(contribution.duration) }}</strong
              ><span>Durée</span>
            </article>
            <article class="ap-stat">
              <strong style="font-size: 1.2rem">{{
                formatDate(contribution.dateContribution)
              }}</strong
              ><span>Date</span>
            </article>
          </div>

          <div class="ap-grid">
            <NuxtLink
              v-if="contribution.user"
              :to="`/user/${contribution.user.id}`"
              class="ap-card"
            >
              <small class="ap-muted">Contributeur</small>
              <div class="ap-person" style="margin-top: 0.5rem">
                <span class="ap-avatar">{{ initials(contribution.user) }}</span>
                <div>
                  <h4>{{ fullName(contribution.user) }}</h4>
                  <small>{{ contribution.user.email }}</small>
                </div>
              </div>
            </NuxtLink>

            <NuxtLink
              v-if="contribution.project"
              :to="`/project/${contribution.project.id}`"
              class="ap-card"
            >
              <small class="ap-muted">Projet</small>
              <h4 style="margin-top: 0.5rem">
                {{ contribution.project.name }}
              </h4>
              <p>{{ contribution.project.description }}</p>
              <span
                class="ap-pill"
                :data-tone="statusTone(contribution.project.status)"
                style="margin-top: 0.6rem"
              >
                {{ contribution.project.status }}
              </span>
            </NuxtLink>
          </div>
        </template>
      </DataState>
    </AppPage>
  </div>
</template>

<script setup lang="ts">
  import {
    fullName,
    initials,
    errorMessage,
    formatDate,
    statusTone,
  } from '~/utils/format'

  const id = Number(useRoute().params.id)
  const { getContributionById, deleteContribution } = useApiContribution()
  const { data: contribution, status, error } = await getContributionById(id)
  const message = ref('')

  async function remove() {
    if (
      !(await askConfirm({
        title: 'Supprimer la contribution',
        message: 'Cette contribution sera supprimée définitivement.',
        confirmLabel: 'Supprimer',
      }))
    )
      return
    const { error: err } = await deleteContribution(id)
    if (err.value) {
      message.value = errorMessage(err.value)
      return
    }
    await navigateTo('/contribution/all')
  }
</script>

<style scoped></style>
