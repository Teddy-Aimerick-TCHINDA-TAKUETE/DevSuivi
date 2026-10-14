<template>
  <div class="date-filter">
    <label>
      Du
      <input v-model="from" type="date" :max="to || undefined" />
    </label>
    <label>
      Au
      <input v-model="to" type="date" :min="from || undefined" />
    </label>
    <button
      v-if="from || to"
      type="button"
      class="ap-btn secondary outline"
      @click="reset"
    >
      Réinitialiser
    </button>
    <small class="ap-muted date-filter-summary">
      {{ count }} contribution{{ count > 1 ? 's' : '' }} · durée cumulée
      <strong>{{ total }}</strong>
    </small>
  </div>
</template>

<script setup lang="ts">
  // Utilisation : <DateFilter v-model:from="from" v-model:to="to" :count :total />
  const from = defineModel<string>('from', { default: '' })
  const to = defineModel<string>('to', { default: '' })
  defineProps<{ count: number; total: number }>()

  function reset() {
    from.value = ''
    to.value = ''
  }
</script>

<style scoped>
  .date-filter {
    display: flex;
    flex-wrap: wrap;
    align-items: flex-end;
    gap: 0.75rem 1rem;
    margin-bottom: 1rem;
  }
  .date-filter label {
    margin: 0;
  }
  .date-filter input {
    margin: 0;
    width: auto;
  }
  .date-filter button {
    margin: 0;
  }
  .date-filter-summary {
    margin-left: auto;
    align-self: center;
  }
</style>
