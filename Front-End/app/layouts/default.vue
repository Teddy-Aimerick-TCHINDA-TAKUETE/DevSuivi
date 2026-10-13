<template>
  <div>
    <!-- Header commun -->
    <header class="ap-nav-wrap">
      <nav class="container">
        <ul>
          <li><NuxtLink to="/" class="ap-brand">◆ DevSuivi</NuxtLink></li>
        </ul>
        <ul>
          <template v-if="isLoggedIn">
            <li v-for="l in links" :key="l.to">
              <NuxtLink :to="l.to" active-class="ap-active">{{
                l.label
              }}</NuxtLink>
            </li>
            <li>
              <NuxtLink to="/profil" active-class="ap-active">Profil</NuxtLink>
            </li>
          </template>
          <li v-else>
            <NuxtLink to="/login" role="button" class="ap-btn ap-sm"
              >Connexion</NuxtLink
            >
          </li>
        </ul>
      </nav>
    </header>

    <!-- Contenu de la page -->
    <main class="container ap-main">
      <slot />
    </main>

    <!-- Footer commun -->
    <footer class="ap-footer">
      DevSuivi · suvi de developpeur, gestion de projets, serveurs et
      contributions
    </footer>
  </div>
</template>

<script setup lang="ts">
  import { useAuth } from '~/composables/useAuth'

  const { isLoggedIn, hasRole } = useAuth()

  const links = computed(() =>
    [
      { to: '/user/me/projets', label: 'Mes projets', show: true },
      { to: '/user/me/contributions', label: 'Mes contributions', show: true },
      { to: '/projet', label: 'Projets', show: hasRole('ADMIN', 'MANAGER') },
      { to: '/serveur', label: 'Serveurs', show: hasRole('ADMIN') },
      { to: '/user', label: 'Utilisateurs', show: hasRole('ADMIN') },
      {
        to: '/contribution',
        label: 'Contributions',
        show: hasRole('ADMIN', 'MANAGER'),
      },
      {
        to: '/assign',
        label: 'Affectations',
        show: hasRole('ADMIN', 'MANAGER'),
      },
    ].filter((l) => l.show),
  )
</script>

<style>
  /* ---------- Thème (surcharge des variables Pico) ---------- */
  html:root:not([data-theme='dark']) {
    --pico-primary: #4f46e5;
    --pico-primary-background: #4f46e5;
    --pico-primary-underline: rgba(79, 70, 229, 0.5);
    --pico-primary-hover: #4338ca;
    --pico-primary-hover-background: #4338ca;
    --pico-primary-hover-underline: #4338ca;
    --pico-primary-focus: rgba(79, 70, 229, 0.25);
    --pico-primary-inverse: #fff;
    --pico-border-radius: 0.6rem;
    --ap-soft: #eef2ff;
  }
  @media (prefers-color-scheme: dark) {
    html:root:not([data-theme='light']) {
      --pico-primary: #a5b4fc;
      --pico-primary-background: #6366f1;
      --pico-primary-underline: rgba(165, 180, 252, 0.5);
      --pico-primary-hover: #c7d2fe;
      --pico-primary-hover-background: #4f46e5;
      --pico-primary-hover-underline: #c7d2fe;
      --pico-primary-focus: rgba(99, 102, 241, 0.35);
      --pico-primary-inverse: #fff;
      --pico-border-radius: 0.6rem;
      --ap-soft: rgba(99, 102, 241, 0.16);
    }
  }

  /* ---------- Navigation ---------- */
  .ap-nav-wrap {
    position: sticky;
    top: 0;
    z-index: 10;
    backdrop-filter: blur(10px);
    background: color-mix(
      in srgb,
      var(--pico-background-color) 85%,
      transparent
    );
    border-bottom: 1px solid var(--pico-muted-border-color);
  }
  .ap-nav-wrap nav {
    flex-wrap: wrap;
    margin-bottom: 0;
  }
  .ap-brand {
    font-weight: 800;
    letter-spacing: -0.02em;
    font-size: 1.15rem;
    text-decoration: none;
  }
  .ap-nav-wrap a.ap-active {
    color: var(--pico-primary);
    font-weight: 700;
    box-shadow: inset 0 -2px 0 var(--pico-primary);
  }
  .ap-main {
    padding-block: 1.5rem 3rem;
  }
  .ap-footer {
    text-align: center;
    padding: 1.5rem 0;
    color: var(--pico-muted-color);
    font-size: 0.85rem;
    border-top: 1px solid var(--pico-muted-border-color);
  }

  .ap-btn {
    width: auto;
    margin: 0;
    display: inline-block;
  }
  .ap-sm {
    padding: 0.3rem 0.7rem;
    font-size: 0.85rem;
  }
</style>
