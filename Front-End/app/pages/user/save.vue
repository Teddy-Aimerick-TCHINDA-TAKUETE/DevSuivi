<template>
  <div>
    <AppPage
      :title="id ? 'Modifier l’utilisateur' : 'Nouvel utilisateur'"
      subtitle="Identité, accès et rôles."
      back="/user/all"
    >
      <article class="ap-narrow">
        <form @submit.prevent="submit">
          <div class="ap-form-grid">
            <label>
              Prénom
              <input v-model="form.firstName" required />
            </label>
            <label>
              Nom
              <input v-model="form.lastName" required />
            </label>
          </div>
          <label>
            Email
            <input v-model="form.email" type="email" required />
          </label>
          <label>
            Mot de passe
            <input
              v-model="form.password"
              type="password"
              autocomplete="new-password"
              required
            />
            <small v-if="id"
              >Le mot de passe est requis par l’API, même pour une
              modification.</small
            >
          </label>
          <label>
            Rôles
            <input
              v-model="form.roles"
              placeholder="DEVELOPER, MANAGER, ADMIN"
            />
            <small>Séparez les rôles par des virgules.</small>
          </label>
          <p v-if="message" class="ap-error" style="padding: 0.6rem 0.9rem">
            {{ message }}
          </p>
          <button
            type="submit"
            class="ap-btn"
            :aria-busy="submitting"
            :disabled="submitting"
          >
            {{ id ? 'Enregistrer' : 'Créer l’utilisateur' }}
          </button>
        </form>
      </article>
    </AppPage>
  </div>
</template>

<script setup lang="ts">
  import { errorMessage, parseRoles } from '~/utils/format'

  definePageMeta({ key: (route) => route.fullPath })

  // /user/save → création · /user/save?id=4 → modification
  const id = Number(useRoute().query.id) || null
  const { getUserById, saveUser, updateUser } = useApiUser()
  const existing = id ? await getUserById(id) : null

  const form = reactive({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    roles: 'DEVELOPER',
  })
  if (existing) {
    watch(
      existing.data,
      (u) => {
        if (!u) return
        Object.assign(form, {
          firstName: u.firstName,
          lastName: u.lastName,
          email: u.email,
          roles: u.roles?.join(', ') ?? '',
        })
      },
      { immediate: true },
    )
  }

  const submitting = ref(false)
  const message = ref('')

  async function submit() {
    submitting.value = true
    message.value = ''
    const dto = { ...form, roles: parseRoles(form.roles) }
    const res = id ? await updateUser(id, dto) : await saveUser(dto)
    submitting.value = false
    if (res.error.value) {
      message.value = errorMessage(res.error.value)
      return
    }
    await navigateTo(
      res.data.value?.id ? `/user/${res.data.value.id}` : '/user/all',
    )
  }
</script>

<style scoped></style>
