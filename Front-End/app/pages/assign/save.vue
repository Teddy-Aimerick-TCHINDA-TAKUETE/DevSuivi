<template>
  <div>
    <AppPage
      title="Nouvelle affectation"
      subtitle="Rattachez un utilisateur à un projet."
      back="/assign/all"
    >
      <article class="ap-narrow">
        <form @submit.prevent="submit">
          <label>
            Utilisateur
            <select v-model.number="form.user" required>
              <option :value="0" disabled>Choisir un utilisateur…</option>
              <option v-for="u in developers" :key="u.id" :value="u.id">
                {{ fullName(u) }}
              </option>
            </select>
          </label>
          <label>
            Projet
            <select
              v-model.number="form.project"
              :disabled="!form.user || loadingProjects"
              :aria-busy="loadingProjects"
              required
            >
              <option :value="0" disabled>
                {{
                  form.user
                    ? 'Choisir un projet…'
                    : 'Sélectionnez d’abord un utilisateur'
                }}
              </option>
              <option v-for="p in availableProjects" :key="p.id" :value="p.id">
                {{ p.name }}
              </option>
            </select>
            <small
              v-if="form.user && !loadingProjects && !availableProjects.length"
              >Aucun projet disponible : cet utilisateur est déjà affecté à tous
              les projets prévus ou en cours.</small
            >
          </label>
          <p v-if="message" class="ap-error" style="padding: 0.6rem 0.9rem">
            {{ message }}
          </p>
          <button
            type="submit"
            class="ap-btn"
            :aria-busy="submitting"
            :disabled="submitting || !form.user || !form.project"
          >
            Créer l’affectation
          </button>
        </form>
      </article>
    </AppPage>
  </div>
</template>

<script setup lang="ts">
  import type { ProjectListDto } from '~/types/projectTypes'
  import { fullName, errorMessage } from '~/utils/format'
  import { canBeAssigned } from '~/utils/projectRules'

  definePageMeta({ key: (route) => route.fullPath })

  // Préremplissable via /assign/save?user=1&project=2
  const route = useRoute()
  const { saveAssign } = useApiAssign()
  const { getAllUsers, getProjectByUserId } = useApiUser()
  const { data: users } = await getAllUsers()
  // Seuls les développeurs peuvent être affectés à un projet ou saisir du temps
  const developers = computed(() =>
    (users.value ?? []).filter((u) => u.roles?.includes('DEVELOPER')),
  )
  const { data: projects } = await useApiProject().getAllProjects()

  const form = reactive({
    user: Number(route.query.user) || 0,
    project: Number(route.query.project) || 0,
  })
  // Projets où l'utilisateur choisi n'est pas encore affecté
  const availableProjects = ref<ProjectListDto[]>([])
  const loadingProjects = ref(false)

  watch(
    () => form.user,
    async (userId) => {
      availableProjects.value = []
      if (!userId) {
        form.project = 0
        return
      }
      loadingProjects.value = true
      try {
        const { data: assigned } = await getProjectByUserId(userId)
        const ids = new Set(assigned.value?.map((p) => p.id))
        availableProjects.value = (projects.value ?? []).filter(
          (p) => !ids.has(p.id) && canBeAssigned(p.status),
        )
      } catch {
        availableProjects.value = []
      }
      loadingProjects.value = false
      // ?project= n'est conservé que si l'utilisateur n'y est pas déjà affecté
      if (!availableProjects.value.some((p) => p.id === form.project))
        form.project = 0
    },
    { immediate: true },
  )

  const submitting = ref(false)
  const message = ref('')

  async function submit() {
    submitting.value = true
    message.value = ''
    const { error } = await saveAssign({
      user: form.user,
      project: form.project,
    })
    submitting.value = false
    if (error.value) {
      message.value = errorMessage(error.value)
      return
    }
    await navigateTo('/assign/all')
  }
</script>

<style scoped></style>
