<template>
  <div>
    <p v-if="pending" aria-busy="true">Chargement…</p>
    <article v-else-if="error" class="ap-error">
      <strong>Oups.</strong> Impossible de récupérer les données. Vérifiez que
      l’API (Spring Boot) tourne bien et que l’adresse est correcte.
    </article>
    <p v-else-if="empty" class="ap-empty ap-muted">
      {{ emptyText ?? 'Rien à afficher pour le moment.' }}
    </p>
    <slot v-else />
  </div>
</template>

<script setup lang="ts">
  defineProps<{
    pending?: boolean
    error?: unknown
    empty?: boolean
    emptyText?: string
  }>()
</script>

<style scoped></style>
