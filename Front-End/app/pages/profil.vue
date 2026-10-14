<template>
  <div>
    <AppPage title="Mon profil" subtitle="Informations de votre session.">
      <template #actions>
        <button v-if="me" class="ap-btn ap-ghost" @click="logout">
          Se déconnecter
        </button>
      </template>

      <article v-if="!me" class="ap-narrow">
        <h4>Vous n’êtes pas connecté</h4>
        <p class="ap-muted">Connectez-vous pour voir votre profil.</p>
        <NuxtLink to="/login" role="button" class="ap-btn"
          >Se connecter</NuxtLink
        >
      </article>

      <template v-else>
        <article>
          <div class="ap-person">
            <span class="ap-avatar ap-lg">{{ initials(me) }}</span>
            <div>
              <h3>{{ me ? fullName(me) : 'Utilisateur connecté' }}</h3>
              <small>{{
                me?.email ?? claims?.email ?? claims?.sub ?? ''
              }}</small>
            </div>
          </div>
          <p style="margin: 1rem 0 0">
            Rôle :
            <span
              v-for="r in me?.roles"
              :key="r"
              class="ap-pill"
              :data-tone="roleTone(r)"
              >{{ r }}</span
            >
            <span v-if="!me?.roles?.length" class="ap-muted">inconnu</span>
          </p>
        </article>

        <div v-if="me" class="ap-links">
          <NuxtLink :to="`/user/me`" role="button" class="ap-btn"
            >Ma fiche</NuxtLink
          >
          <NuxtLink
            :to="`/user/me/projects`"
            role="button"
            class="ap-btn secondary outline"
            >Mes projets</NuxtLink
          >
          <NuxtLink
            :to="`/user/me/contributions`"
            role="button"
            class="ap-btn secondary outline"
            >Mes contributions</NuxtLink
          >
        </div>
      </template>
    </AppPage>
  </div>
</template>

<script setup lang="ts">
  import { initials, fullName, roleTone } from '~/utils/format'

  const token = getAccessToken()
  const refreshToken = getRefreshToken()
  const { data: user } = await useApiUser().getUserProfil()

  // Lecture "best effort" du JWT (id / email) pour retrouver l'utilisateur connecté
  const claims = computed<Record<string, any> | null>(() => {
    try {
      const part = token.value?.split('.')[1]
      if (!part) return null
      return JSON.parse(atob(part.replace(/-/g, '+').replace(/_/g, '/')))
    } catch {
      return null
    }
  })

  const me = computed(() => {
    return user.value
  })

  function logout() {
    token.value = null
    refreshToken.value = null
    navigateTo('/login')
  }
</script>

<style scoped></style>
