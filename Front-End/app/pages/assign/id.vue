<template>
  <div>
    <AppPage
      title="Détail de l’affectation"
      :subtitle="
        assign ? `${fullName(assign.user)} → ${assign.project.name}` : undefined
      "
      back="/assign/all"
    >
      <template #actions>
        <button v-if="assign" class="ap-btn danger" @click="remove">
          Retirer
        </button>
      </template>

      <article v-if="message" class="ap-error">{{ message }}</article>

      <DataState :pending="status === 'pending'" :error="error">
        <p v-if="!assign" class="ap-empty ap-muted">
          Affectation introuvable.
          <NuxtLink to="/assign/all">Voir toutes les affectations</NuxtLink>
        </p>
        <div v-else class="ap-grid">
          <NuxtLink :to="`/user/${assign.user.id}`" class="ap-card">
            <small class="ap-muted">Utilisateur</small>
            <div class="ap-person" style="margin-top: 0.5rem">
              <span class="ap-avatar">{{ initials(assign.user) }}</span>
              <div>
                <h4>{{ fullName(assign.user) }}</h4>
                <small>{{ assign.user.email }}</small>
              </div>
            </div>
          </NuxtLink>

          <NuxtLink :to="`/projet/${assign.project.id}`" class="ap-card">
            <small class="ap-muted">Projet</small>
            <h4 style="margin-top: 0.5rem">{{ assign.project.name }}</h4>
            <p>{{ assign.project.description }}</p>
            <span
              class="ap-pill"
              :data-tone="statusTone(assign.project.status)"
              style="margin-top: 0.6rem"
            >
              {{ assign.project.status }}
            </span>
          </NuxtLink>
        </div>
      </DataState>
    </AppPage>
  </div>
</template>

<script setup lang="ts">
  import { fullName, initials, statusTone, errorMessage } from '~/utils/format'

  definePageMeta({ key: (route) => route.fullPath })

  // /assign/id?user=1&project=2
  // getAssignById envoie un GET avec un body (refusé par fetch) : on retrouve
  // donc l'affectation dans la liste complète.
  const route = useRoute()
  const userId = Number(route.query.user) || 0
  const projectId = Number(route.query.project) || 0

  const { getAllAssigns, deleteAssign } = useApiAssign()
  const { data: assigns, status, error } = await getAllAssigns()
  const assign = computed(() =>
    assigns.value?.find(
      (a) => a.user.id === userId && a.project.id === projectId,
    ),
  )
  const message = ref('')

  async function remove() {
    if (
      !(await askConfirm({
        title: 'Retirer l’affectation',
        message: 'Cet utilisateur ne sera plus affecté à ce projet.',
        confirmLabel: 'Retirer',
      }))
    )
      return
    const { error: err } = await deleteAssign({
      user: userId,
      project: projectId,
    })
    if (err.value) {
      message.value = errorMessage(err.value)
      return
    }
    await navigateTo('/assign/all')
  }
</script>

<style scoped></style>
