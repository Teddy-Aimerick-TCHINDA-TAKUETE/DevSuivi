<template>
  <div>
    <AppPage
      :title="id ? 'Modifier le projet' : 'Nouveau projet'"
      subtitle="Renseignez les informations principales du projet."
      back="/projet/all"
    >
      <article>
        <form @submit.prevent="submit">
          <label>
            Nom
            <input
              v-model="form.name"
              placeholder="Mon super projet"
              required
            />
          </label>
          <label>
            Description
            <textarea
              v-model="form.description"
              rows="4"
              placeholder="De quoi s’agit-il ?"
            />
          </label>
          <div class="ap-form-grid">
            <label>
              Quota
              <input
                v-model.number="form.quota"
                type="number"
                min="0"
                required
              />
            </label>
            <label>
              Statut
              <select v-model="form.status" required>
                <option v-for="s in PROJECT_STATUSES" :key="s" :value="s">
                  {{ s }}
                </option>
              </select>
            </label>
            <label>
              Serveur
              <select v-model.number="form.idServer" required>
                <option :value="0" disabled>Choisir un serveur…</option>
                <option v-for="s in servers" :key="s.id" :value="s.id">
                  {{ s.name }}
                </option>
              </select>
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
            {{ id ? 'Enregistrer les modifications' : 'Créer le projet' }}
          </button>
        </form>
      </article>
    </AppPage>
  </div>
</template>

<script setup lang="ts">
  import type { ProjectNewDto } from '~/types/projectTypes'
  import {
    DEFAULT_PROJECT_STATUS,
    PROJECT_STATUSES,
    errorMessage,
  } from '~/utils/format'

  definePageMeta({ key: (route) => route.fullPath })

  // /projet/save → création · /projet/save?id=3 → modification
  const id = Number(useRoute().query.id) || null
  const { getProjectById, saveProject, updateProject } = useApiProject()
  const { data: servers } = await useApiServer().getAllServers()
  const existing = id ? await getProjectById(id) : null

  const form = reactive({
    name: '',
    description: '',
    quota: 0,
    status: DEFAULT_PROJECT_STATUS as string,
    idServer: 0,
  })
  if (existing) {
    watch(
      existing.data,
      (p) => {
        if (!p) return
        Object.assign(form, {
          name: p.name,
          description: p.description,
          quota: p.quota,
          status: p.status,
          idServer: p.server?.id ?? 0,
        })
      },
      { immediate: true },
    )
  }

  // Tous les champs obligatoires doivent être remplis
  const canSubmit = computed(
    () =>
      form.name.trim() !== '' &&
      String(form.quota) !== '' &&
      Number(form.quota) >= 0 &&
      form.status !== '' &&
      form.idServer > 0,
  )

  const submitting = ref(false)
  const message = ref('')

  async function submit() {
    submitting.value = true
    message.value = ''
    const dto: ProjectNewDto = { ...form, quota: Number(form.quota) }
    const res = id ? await updateProject(id, dto) : await saveProject(dto)
    submitting.value = false
    if (res.error.value) {
      message.value = errorMessage(res.error.value)
      return
    }
    await navigateTo(
      res.data.value?.id ? `/projet/${res.data.value.id}` : '/projet/all',
    )
  }
</script>

<style scoped></style>
