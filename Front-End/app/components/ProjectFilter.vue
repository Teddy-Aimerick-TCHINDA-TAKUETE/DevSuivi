<template>
  <div v-if="items.length" class="project-filter">
    <label>
      Statut
      <select v-model="status">
        <option value="">Tous ({{ items.length }})</option>
        <option v-for="s in statuses" :key="s.value" :value="s.value">
          {{ s.value }} ({{ s.count }})
        </option>
      </select>
    </label>
    <label v-if="overCount > 0 || overOnly" class="project-filter-over">
      <input v-model="overOnly" type="checkbox" role="switch" />
      Uniquement les projets en dépassement ({{ overCount }})
    </label>
    <button
      v-if="status || overOnly"
      type="button"
      class="ap-btn secondary outline"
      @click="reset"
    >
      Réinitialiser
    </button>
  </div>
</template>

<script setup lang="ts">
  import { PROJECT_STATUSES } from '~/utils/format'
  import { countOver } from '~/utils/projectRules'

  // Utilisation : <ProjectFilter v-model:status="status" v-model:over-only="overOnly" :items="projects" />
  // L'option « dépassement » n'apparaît que s'il y a au moins un projet en dépassement.
  const status = defineModel<string>('status', { default: '' })
  const overOnly = defineModel<boolean>('overOnly', { default: false })
  const props = defineProps<{
    items: { status: string; quota: number; totalDuration?: number }[]
  }>()

  const overCount = computed(() => countOver(props.items))
  // Seuls les états présents dans la liste sont proposés (avec leur effectif)
  const statuses = computed(() =>
    PROJECT_STATUSES.map((value) => ({
      value,
      count: props.items.filter((p) => p.status === value).length,
    })).filter((s) => s.count > 0 || s.value === status.value),
  )

  function reset() {
    status.value = ''
    overOnly.value = false
  }
</script>

<style scoped>
  .project-filter {
    display: flex;
    flex-wrap: wrap;
    align-items: flex-end;
    gap: 0.75rem 1.25rem;
    margin-bottom: 1rem;
  }
  .project-filter label {
    margin: 0;
  }
  .project-filter select {
    margin: 0;
    width: auto;
  }
  .project-filter-over {
    align-self: center;
  }
  .project-filter button {
    margin: 0;
  }
</style>
