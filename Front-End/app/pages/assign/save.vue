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
              <option v-for="u in users" :key="u.id" :value="u.id">
                {{ fullName(u) }} — {{ u.email }}
              </option>
            </select>
          </label>
          <label>
            Projet
            <select v-model.number="form.project" required>
              <option :value="0" disabled>Choisir un projet…</option>
              <option v-for="p in projects" :key="p.id" :value="p.id">
                {{ p.name }}
              </option>
            </select>
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
            Affecter
          </button>
        </form>
      </article>
    </AppPage>
  </div>
</template>

<script setup lang="ts">
  import { fullName, errorMessage } from '~/utils/format'

  definePageMeta({ key: (route) => route.fullPath })

  // Préremplissable via /assign/save?user=1&project=2
  const route = useRoute()
  const { saveAssign } = useApiAssign()
  const { data: users } = await useApiUser().getAllUsers()
  const { data: projects } = await useApiProject().getAllProjects()

  const form = reactive({
    user: Number(route.query.user) || 0,
    project: Number(route.query.project) || 0,
  })
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
