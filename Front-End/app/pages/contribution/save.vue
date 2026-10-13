<template>
  <div>
    <AppPage
      :title="id ? 'Modifier la contribution' : 'Nouvelle contribution'"
      subtitle="Qui a travaillé sur quoi, quand et combien de temps."
      back="/contribution/all"
    >
      <article class="ap-narrow">
        <form @submit.prevent="submit">
          <label>
            Utilisateur
            <select v-model.number="form.idUser" required>
              <option :value="0" disabled>Choisir un utilisateur…</option>
              <option v-for="u in users" :key="u.id" :value="u.id">
                {{ fullName(u) }}
              </option>
            </select>
          </label>
          <label>
            Projet
            <select
              v-model.number="form.idProject"
              :disabled="!form.idUser || loadingProjects"
              :aria-busy="loadingProjects"
              required
            >
              <option :value="0" disabled>
                {{
                  form.idUser
                    ? 'Choisir un projet…'
                    : 'Sélectionnez d’abord un utilisateur'
                }}
              </option>
              <option v-for="p in projects" :key="p.id" :value="p.id">
                {{ p.name }}
              </option>
            </select>
            <small v-if="form.idUser && !loadingProjects && !projects.length"
              >Cet utilisateur n’est affecté à aucun projet.</small
            >
          </label>
          <div class="ap-form-grid">
            <label>
              Date
              <input v-model="form.date" type="date" required />
            </label>
            <label>
              Durée
              <input
                v-model.number="form.duration"
                type="number"
                min="0"
                step="any"
                required
              />
            </label>
          </div>
          <p v-if="message" class="ap-error" style="padding: 0.6rem 0.9rem">
            {{ message }}
          </p>
          <button
            type="submit"
            class="ap-btn"
            :aria-busy="submitting"
            :disabled="submitting || !canSubmit"
          >
            {{ id ? 'Enregistrer les modifications' : 'Créer la contribution' }}
          </button>
        </form>
      </article>
    </AppPage>
  </div>
</template>

<script setup lang="ts">
  import type { ProjectListDto } from '~/types/projectTypes'
  import { fullName, errorMessage } from '~/utils/format'

  definePageMeta({ key: (route) => route.fullPath })

  // /contribution/save → création (préremplissable via ?user= et ?project=)
  // /contribution/save?id=5 → modification
  const route = useRoute()
  const id = Number(route.query.id) || null
  const { getContributionById, saveContribution, updateContribution } =
    useApiContribution()
  const { getAllUsers, fetchProjectsOfUser } = useApiUser()
  const { data: users } = await getAllUsers()
  const existing = id ? await getContributionById(id) : null

  const form = reactive({
    duration: 1,
    date: new Date().toISOString().slice(0, 10),
    idUser: Number(route.query.user) || 0,
    idProject: Number(route.query.project) || 0,
  })

  // Projets de l'utilisateur sélectionné (vide tant qu'aucun utilisateur n'est choisi)
  const projects = ref<ProjectListDto[]>([])
  const loadingProjects = ref(false)

  // Nom du projet de la contribution en cours de modification (le détail
  // ne renvoie pas l'id du projet : on le retrouve grâce à son nom)
  let wantedProjectName: string | null = null

  if (existing) {
    watch(
      existing.data,
      (c) => {
        if (!c) return
        form.duration = c.duration
        form.date = String(c.dateContribution).slice(0, 10)
        wantedProjectName = c.project?.name ?? null
        form.idUser = c.user?.id ?? 0
      },
      { immediate: true },
    )
  }

  watch(
    () => form.idUser,
    async (userId) => {
      projects.value = []
      if (!userId) {
        form.idProject = 0
        return
      }
      loadingProjects.value = true
      try {
        projects.value = await fetchProjectsOfUser(userId)
      } catch {
        projects.value = []
      }
      loadingProjects.value = false
      // On ne garde un projet que s'il fait partie de ceux de l'utilisateur
      const byName = wantedProjectName
        ? projects.value.find((p) => p.name === wantedProjectName)?.id
        : undefined
      wantedProjectName = null
      const keep = byName ?? form.idProject
      form.idProject = projects.value.some((p) => p.id === keep) ? keep : 0
    },
    { immediate: true },
  )

  // Tous les champs obligatoires doivent être remplis
  const canSubmit = computed(
    () =>
      form.idUser > 0 &&
      form.idProject > 0 &&
      form.date !== '' &&
      String(form.duration) !== '' &&
      Number(form.duration) >= 0,
  )

  const submitting = ref(false)
  const message = ref('')

  async function submit() {
    submitting.value = true
    message.value = ''
    const dto = {
      duration: Number(form.duration),
      // On envoie la chaîne "YYYY-MM-DD" telle quelle (LocalDate côté Spring).
      // Le type du DTO déclare un Date, d'où le cast.
      dateContribution: form.date as unknown as Date,
      idUser: form.idUser,
      idProject: form.idProject,
    }
    const res = id
      ? await updateContribution(id, dto)
      : await saveContribution(dto)
    submitting.value = false
    if (res.error.value) {
      message.value = errorMessage(res.error.value)
      return
    }
    await navigateTo(
      res.data.value?.id
        ? `/contribution/${res.data.value.id}`
        : '/contribution/all',
    )
  }
</script>

<style scoped></style>
