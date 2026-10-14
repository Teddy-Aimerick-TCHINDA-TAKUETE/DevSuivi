<template>
  <div>
    <NuxtLayout>
      <NuxtPage />
    </NuxtLayout>
    <ConfirmDialog />
  </div>
</template>

<script setup lang="ts">
  import { themeRole } from '~/utils/roleTheme'

  // Le thème dépend du rôle dominant : on le pose sur <html data-role="...">
  // (rendu côté serveur, donc sans flash) et le CSS ci-dessous fait le reste.
  const { roles, isLoggedIn } = useAuth()
  const role = computed(() =>
    isLoggedIn.value ? themeRole(roles.value) : 'guest',
  )
  useHead({ htmlAttrs: { 'data-role': role } })
</script>

<style>
  /* ---------- Couleur d'accent selon le rôle ---------- */
  html {
    --ap-accent: #64748b; /* guest : thème par défaut */
  }
  html[data-role='admin'] {
    --ap-accent: #db2777;
  }
  html[data-role='manager'] {
    --ap-accent: #0d9488;
  }
  html[data-role='developer'] {
    --ap-accent: #4f46e5;
  }
  @media (prefers-color-scheme: dark) {
    html:not([data-theme='light']) {
      --ap-accent: #94a3b8;
    }
    html:not([data-theme='light'])[data-role='admin'] {
      --ap-accent: #f472b6;
    }
    html:not([data-theme='light'])[data-role='manager'] {
      --ap-accent: #2dd4bf;
    }
    html:not([data-theme='light'])[data-role='developer'] {
      --ap-accent: #818cf8;
    }
  }

  /* ---------- Éléments qui suivent l'accent (barre de navigation) ---------- */
  html .ap-nav-wrap {
    border-top: 4px solid var(--ap-accent);
    transition: border-color 0.2s;
  }
  html .ap-brand,
  html .ap-brand:hover {
    color: var(--ap-accent);
  }
  html .ap-nav-wrap a.ap-active {
    color: var(--ap-accent);
    box-shadow: inset 0 -2px 0 var(--ap-accent);
  }
  html .ap-footer {
    border-top-color: var(--ap-accent);
  }
  .ap-role-badge {
    display: inline-block;
    padding: 0.15rem 0.65rem;
    border-radius: 999px;
    font-size: 0.75rem;
    font-weight: 700;
    color: var(--ap-accent);
    background: color-mix(in srgb, var(--ap-accent) 15%, transparent);
    border: 1px solid color-mix(in srgb, var(--ap-accent) 45%, transparent);
  }
</style>
