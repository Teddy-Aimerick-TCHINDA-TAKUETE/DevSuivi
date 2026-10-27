<template>
  <div>
    <AppPage title="Connexion" subtitle="Content de vous revoir 👋">
      <article class="ap-narrow">
        <form @submit.prevent="submit">
          <label>
            Email
            <input
              v-model="form.email"
              type="email"
              placeholder="vous@exemple.com"
              autocomplete="email"
              required
            />
          </label>
          <label>
            Mot de passe
            <input
              v-model="form.password"
              type="password"
              placeholder="••••••••"
              autocomplete="current-password"
              required
            />
          </label>
          <p v-if="message" class="ap-error" style="padding: 0.6rem 0.9rem">
            {{ message }}
          </p>
          <button
            type="submit"
            :aria-busy="loading"
            :disabled="loading || !canSubmit"
          >
            Se connecter
          </button>
        </form>
        <footer class="ap-muted">
          Pas encore de compte ?
          <NuxtLink to="/register">Inscrivez-vous</NuxtLink>
        </footer>
      </article>
    </AppPage>
  </div>
</template>

<script setup lang="ts">
  import type { AuthRequestDto } from '~/types/authTypes'
  import { errorMessage } from '~/utils/format'

  definePageMeta({
    layout: 'login-layout',
  })

  const { loging } = useApiAuth()
  const form = reactive<AuthRequestDto>({ email: '', password: '' })
  const loading = ref(false)
  const message = ref('')
  const canSubmit = computed(
    () => form.email.trim() !== '' && form.password !== '',
  )
  const route = useRoute()

  async function submit() {
    loading.value = true
    message.value = ''
    const { data: authResponseDto, error } = await loging({ ...form })
    loading.value = false

    if (error || !authResponseDto) {
      setAccessToken('')
      setRefreshToken('')
      const status = error?.statusCode
      message.value =
        status === 401 || status === 403
          ? 'Email ou mot de passe incorrect.'
          : errorMessage(error)
      return
    }

    setAccessToken(authResponseDto.accessToken)
    setRefreshToken(
      authResponseDto.refreshToken,
      secondsUntil(authResponseDto.refreshExpireAt),
    )

    const redirect = String(route.query.redirect ?? '')
    await navigateTo(
      redirect.startsWith('/') && !redirect.startsWith('//')
        ? redirect
        : '/profil',
    )
  }
</script>

<style scoped></style>
