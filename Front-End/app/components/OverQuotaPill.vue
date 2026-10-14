<template>
  <span
    v-if="isOverQuota(used, quota)"
    class="ap-pill"
    data-tone="over"
    title="La durée cumulée dépasse le quota"
    >Dépassement</span
  >
</template>

<script setup lang="ts">
  import { isOverQuota } from '~/utils/projectRules'

  // Simple signalement : n'affecte pas l'état du projet
  defineProps<{ used?: number; quota: number }>()
</script>

<style scoped>
  .ap-pill {
    margin-left: 0.4rem;
  }
</style>
