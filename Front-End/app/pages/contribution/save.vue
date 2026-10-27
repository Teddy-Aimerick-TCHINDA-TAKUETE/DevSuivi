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
              <option v-for="u in developers" :key="u.id" :value="u.id">
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
              <option v-for="p in selectableProjects" :key="p.id" :value="p.id">
                {{ p.name
                }}{{
                  canReceiveContributions(p.status) ? '' : ` (${p.status})`
                }}
              </option>
            </select>
            <small
              v-if="
                form.idUser && !loadingProjects && !selectableProjects.length
              "
              >{{
                projects.length
                  ? 'Aucun projet en cours pour cet utilisateur.'
                  : 'Cet utilisateur n’est affecté à aucun projet.'
              }}</small
            >
          </label>
          <div class="ap-form-grid">
            <label>
              Date
              <input v-model="form.date" type="date" required />
            </label>
            <label>
              Durée (en minutes)
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
  import { canReceiveContributions } from '~/utils/projectRules'

  definePageMeta({ key: (route) => route.fullPath })

  // /contribution/save → création (préremplissable via ?user= et ?project=)
  // /contribution/save?id=5 → modification
  const route = useRoute()
  const id = Number(route.query.id) || null
  const { getContributionById, saveContribution, updateContribution } =
    useApiContribution()
  const { getAllUsers, getProjectByUserId } = useApiUser()
  const { data: users } = await getAllUsers()
  // Seuls les développeurs peuvent être affectés à un projet ou saisir du temps
  const developers = computed(() =>
    (users.value ?? []).filter((u) => u.roles?.includes('DEVELOPER')),
  )
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
  // Projet d'origine d'une contribution modifiée : on le garde dans la liste
  // même s'il est clôturé depuis, pour pouvoir corriger la durée ou la date.
  const originalProjectId = ref(0)

  // Règle : seulement les projets en cours auxquels l'utilisateur est affecté
  const selectableProjects = computed(() =>
    projects.value.filter(
      (p) =>
        canReceiveContributions(p.status) || p.id === originalProjectId.value,
    ),
  )

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
        const { data } = await getProjectByUserId(userId)
        projects.value = data.value ?? []
      } catch {
        projects.value = []
      }
      loadingProjects.value = false
      // On ne garde un projet que s'il fait partie de ceux de l'utilisateur
      const byName = wantedProjectName
        ? projects.value.find((p) => p.name === wantedProjectName)?.id
        : undefined
      wantedProjectName = null
      if (byName) originalProjectId.value = byName
      const keep = byName ?? form.idProject
      form.idProject = selectableProjects.value.some((p) => p.id === keep)
        ? keep
        : 0
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
    if (res.error) {
      message.value = errorMessage(res.error)
      return
    }
    await navigateTo(
      res.data?.id
        ? `/contribution/${res.data.id}`
        : '/contribution/all',
    )
  }
</script>

<style scoped></style>
