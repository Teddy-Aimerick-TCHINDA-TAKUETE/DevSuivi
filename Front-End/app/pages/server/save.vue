<template>
  <div>
    <AppPage
      :title="id ? 'Modifier le serveur' : 'Nouveau serveur'"
      subtitle="Nom et description du serveur."
      back="/server/all"
    >
      <article class="ap-narrow">
        <form @submit.prevent="submit">
          <label>
            Nom
            <input v-model="form.name" placeholder="srv-prod-01" required />
          </label>
          <label>
            Description
            <textarea
              v-model="form.description"
              rows="4"
              placeholder="Rôle, localisation, spécificités…"
            />
          </label>
          <p v-if="message" class="ap-error" style="padding: 0.6rem 0.9rem">
            {{ message }}
          </p>
          <button
            type="submit"
            class="ap-btn"
            :aria-busy="submitting"
            :disabled="submitting || !canSubmit"
          >
            {{ id ? 'Enregistrer les modifications' : 'Créer le serveur' }}
          </button>
        </form>
      </article>
    </AppPage>
  </div>
</template>

<script setup lang="ts">
  import { errorMessage } from '~/utils/format'

  definePageMeta({ key: (route) => route.fullPath })

  // /server/save → création · /server/save?id=2 → modification
  const id = Number(useRoute().query.id) || null
  const { getServerById, saveServer, updateServer } = useApiServer()
  const existing = id ? await getServerById(id) : null

  const form = reactive({ name: '', description: '' })
  if (existing) {
    watch(
      existing.data,
      (s) => {
        if (s) Object.assign(form, { name: s.name, description: s.description })
      },
      { immediate: true },
    )
  }

  // Tous les champs obligatoires doivent être remplis
  const canSubmit = computed(() => form.name.trim() !== '')

  const submitting = ref(false)
  const message = ref('')

  async function submit() {
    submitting.value = true
    message.value = ''
    const res = id
      ? await updateServer(id, { ...form })
      : await saveServer({ ...form })
    submitting.value = false
    if (res.error.value) {
      message.value = errorMessage(res.error.value)
      return
    }
    await navigateTo(
      res.data.value?.id ? `/server/${res.data.value.id}` : '/server/all',
    )
  }
</script>

<style scoped></style>
