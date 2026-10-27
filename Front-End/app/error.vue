<template>
  <NuxtLayout name="minimal">
    <section class="er-card">
      <p class="er-code">{{ code }}</p>
      <h1>{{ title }}</h1>
      <p class="er-text">{{ text }}</p>
      <div class="er-actions">
        <button class="ap-btn" @click="goHome">Retour à l’accueil</button>
        <button v-if="!token" class="ap-btn secondary outline" @click="goLogin">
          Se connecter
        </button>
      </div>
    </section>
  </NuxtLayout>
</template>

<script setup lang="ts">
  import type { NuxtError } from '#app'

  const props = defineProps<{ error: NuxtError }>()

  const token = getAccessToken()
  const code = computed(() => props.error.statusCode ?? 500)
  // 404 / 401 / 403 : même message (page absente ou accès refusé)
  const notAccessible = computed(() => [401, 403, 404].includes(code.value))

  const title = computed(() =>
    notAccessible.value ? 'Page inaccessible' : 'Oups, une erreur est survenue',
  )
  const text = computed(() =>
    notAccessible.value
      ? 'Cette page n’existe pas ou n’existe plus, ou vous n’avez pas les autorisations nécessaires pour y accéder.'
      : 'Quelque chose s’est mal passé de notre côté. Réessayez dans un instant.',
  )

  useHead({ title: `${code.value} · ${title.value}` })

  const goHome = () => clearError({ redirect: '/' })
  const goLogin = () => clearError({ redirect: '/login' })
</script>

<style>
  .er-card {
    text-align: center;
    padding: 2.5rem 2rem;
    border: 1px solid var(--pico-muted-border-color);
    border-radius: 1.25rem;
    background: var(--pico-card-background-color);
    box-shadow: 0 20px 40px -24px rgba(79, 70, 229, 0.5);
  }
  .er-code {
    margin: 0;
    font-size: clamp(4rem, 14vw, 6.5rem);
    font-weight: 800;
    line-height: 1;
    letter-spacing: -0.04em;
    background: linear-gradient(135deg, #4f46e5, #7c3aed 55%, #db2777);
    -webkit-background-clip: text;
    background-clip: text;
    color: transparent;
  }
  .er-card h1 {
    margin: 0.75rem 0 0.5rem;
    font-size: 1.5rem;
  }
  .er-text {
    margin: 0 auto 1.5rem;
    max-width: 26rem;
    color: var(--pico-muted-color);
  }
  .er-actions {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    gap: 0.6rem;
  }
</style>
