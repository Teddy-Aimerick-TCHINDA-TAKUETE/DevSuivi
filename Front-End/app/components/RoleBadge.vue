<template>
  <span v-if="label" class="ap-role-badge">{{ label }}</span>
</template>

<script setup lang="ts">
  import { THEME_LABELS, themeRole } from '~/utils/roleTheme'

  // Rôle dominant de l'utilisateur connecté (ADMIN prime sur MANAGER)
  const { roles, isLoggedIn } = useAuth()
  const label = computed(() =>
    isLoggedIn.value ? THEME_LABELS[themeRole(roles.value)] : '',
  )
</script>
