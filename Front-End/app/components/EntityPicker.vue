<template>
  <div>
    <label>
      {{ label }}
      <select :value="current" @change="pick">
        <option :value="0" disabled>Choisir…</option>
        <option v-for="o in options" :key="o.id" :value="o.id">
          {{ o.label }}
        </option>
      </select>
    </label>
  </div>
</template>

<script setup lang="ts">
  // Sélecteur qui met à jour ?id= dans l'URL (la page se recharge avec le nouvel id)
  defineProps<{
    label: string
    options: { id: number; label: string }[]
    current: number
  }>()

  function pick(e: Event) {
    navigateTo({ query: { id: (e.target as HTMLSelectElement).value } })
  }
</script>

<style scoped></style>
