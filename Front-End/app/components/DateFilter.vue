<template>
  <div class="date-filter">
    <label v-if="users && (users.length > 1 || user)">
      Développeur
      <select v-model="user">
        <option :value="0">Tous</option>
        <option v-for="u in users" :key="u.id" :value="u.id">
          {{ u.name }}
        </option>
      </select>
    </label>
    <label v-if="projects && (projects.length > 1 || project)">
      Projet
      <select v-model="project">
        <option :value="0">Tous</option>
        <option v-for="p in projects" :key="p.id" :value="p.id">
          {{ p.name }}
        </option>
      </select>
    </label>
    <label>
      Du
      <input v-model="from" type="date" :max="to || undefined" />
    </label>
    <label>
      Au
      <input v-model="to" type="date" :min="from || undefined" />
    </label>
    <button
      v-if="from || to || user || project"
      type="button"
      class="ap-btn secondary outline"
      @click="reset"
    >
      Réinitialiser
    </button>
    <small class="ap-muted date-filter-summary">
      {{ count }} contribution{{ count > 1 ? 's' : '' }} · durée cumulée
      <strong>{{ formatDuration(total) }}</strong>
    </small>
  </div>
</template>

<script setup lang="ts">
  // Utilisation : <DateFilter v-model:from="from" v-model:to="to" :count :total />
  // Filtres facultatifs : :users="[{ id, name }]" v-model:user="user"
  //                       :projects="[{ id, name }]" v-model:project="project"
  const from = defineModel<string>('from', { default: '' })
  const to = defineModel<string>('to', { default: '' })
  const user = defineModel<number>('user', { default: 0 })
  const project = defineModel<number>('project', { default: 0 })
  defineProps<{
    count: number
    total: number
    users?: { id: number; name: string }[]
    projects?: { id: number; name: string }[]
  }>()

  function reset() {
    from.value = ''
    to.value = ''
    user.value = 0
    project.value = 0
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
  .date-filter input,
  .date-filter select {
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
