<template>
  <div>
    <AppPage
      :title="id ? 'Modifier la contribution' : 'Nouvelle contribution'"
      subtitle="Qui a travaillé sur quoi, quand et combien de temps."
      back="/user/me/contributions/all"
    >
      <article class="ap-narrow">
        <form @submit.prevent="submit">
          <!-- <label>
            Utilisateur
            <select v-model.number="form.idUser" required>
              <option :value="0" disabled>Choisir un utilisateur…</option>
              <option v-for="u in users" :key="u.id" :value="u.id">
                {{ fullName(u) }}
              </option>
            </select>
          </label> -->
          <label>
            Projet
            <select v-model.number="form.idProject" required>
              <option :value="0" disabled>Choisir un projet…</option>
              <option v-for="p in selectableProjects" :key="p.id" :value="p.id">
                {{ p.name
                }}{{
                  canReceiveContributions(p.status) ? '' : ` (${p.status})`
                }}
              </option>
            </select>
            <small v-if="!selectableProjects.length"
              >Vous n’avez aucun projet en cours sur lequel déclarer une
              contribution.</small
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
  import { fullName, errorMessage } from '~/utils/format'
  import { canReceiveContributions } from '~/utils/projectRules'

  definePageMeta({ key: (route) => route.fullPath })

  // /contribution/save → création (préremplissable via ?user= et ?project=)
  // /contribution/save?id=5 → modification
  const { getContributionByUserId, getUserProfil, getProjectByUserId } =
    useApiUser()
  const { data: user } = await getUserProfil()
  const userId = user.value?.id || 0

  const route = useRoute()
  const id = Number(route.query.id) || null
  const { getContributionById, saveContribution, updateContribution } =
    useApiContribution()
  // const { data: users } = useApiUser().getAllUsers()
  // const { data: projects } = useApiProject().getAllProjects()
  const { data: projects } = await getProjectByUserId(userId)
  const existing = id ? await getContributionById(id) : null
  // Projet d'origine d'une contribution modifiée : on le garde dans la liste
  // même s'il est clôturé depuis, pour pouvoir corriger la durée ou la date.
  const originalProjectId = ref(0)

  // Règle : seulement mes projets en cours (on ne modifie plus la liste reçue)
  const selectableProjects = computed(() =>
    (projects.value ?? []).filter(
      (p) =>
        canReceiveContributions(p.status) || p.id === originalProjectId.value,
    ),
  )

  const form = reactive({
    duration: 1,
    date: new Date().toISOString().slice(0, 10),
    idUser: userId,
    idProject: Number(route.query.project) || 0,
  })

  if (existing) {
    watch(
      [existing.data, projects],
      ([c, list]) => {
        if (!c) return
        form.duration = c.duration
        form.date = String(c.dateContribution).slice(0, 10)
        form.idUser = userId
        // Le détail ne renvoie pas l'id du projet : on le retrouve grâce à son nom
        const original = list?.find((p) => p.name === c.project?.name)?.id
        if (original) originalProjectId.value = original
        form.idProject = original ?? form.idProject
      },
      { immediate: true },
    )
  }

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
        ? `/user/me/contributions/${res.data.value.id}`
        : '/user/me/contributions/all',
    )
  }
</script>

<style scoped></style>
