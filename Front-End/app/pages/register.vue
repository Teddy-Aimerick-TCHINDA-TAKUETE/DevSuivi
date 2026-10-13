<template>
  <div>
    <AppPage
      title="Créer un compte"
      subtitle="Rejoignez l’équipe en quelques secondes."
    >
      <article class="ap-narrow">
        <form @submit.prevent="submit">
          <div class="ap-form-grid">
            <label>
              Prénom
              <input
                v-model="form.firstName"
                autocomplete="given-name"
                required
              />
            </label>
            <label>
              Nom
              <input
                v-model="form.lastName"
                autocomplete="family-name"
                required
              />
            </label>
          </div>
          <label>
            Email
            <input
              v-model="form.email"
              type="email"
              autocomplete="email"
              required
            />
          </label>
          <div class="ap-form-grid">
            <label>
              Mot de passe
              <input
                v-model="form.password"
                type="password"
                autocomplete="new-password"
                minlength="6"
                required
              />
            </label>
            <label>
              Confirmation
              <input
                v-model="form.confirm"
                type="password"
                autocomplete="new-password"
                required
              />
            </label>
          </div>
          <p v-if="message" class="ap-error" style="padding: 0.6rem 0.9rem">
            {{ message }}
          </p>
          <button type="submit" :aria-busy="loading" :disabled="loading">
            Créer mon compte
          </button>
        </form>
        <footer class="ap-muted">
          Déjà inscrit ? <NuxtLink to="/loging">Connectez-vous</NuxtLink>
        </footer>
      </article>
    </AppPage>
  </div>
</template>

<script setup lang="ts">
  import { errorMessage } from '~/utils/format'

  definePageMeta({
    layout: 'login-layout',
  })

  const { saveUser } = useApiUser()
  const form = reactive({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    confirm: '',
  })
  const loading = ref(false)
  const message = ref('')

  async function submit() {
    message.value = ''
    if (form.password !== form.confirm) {
      message.value = 'Les mots de passe ne correspondent pas.'
      return
    }
    loading.value = true
    const { error } = await saveUser({
      firstName: form.firstName,
      lastName: form.lastName,
      email: form.email,
      password: form.password,
      roles: ['USER'],
    })
    loading.value = false
    if (error.value) {
      message.value = errorMessage(error.value)
      return
    }
    await navigateTo('/loging')
  }
</script>

<style scoped></style>
