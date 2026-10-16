<template>
  <div class="ap-table">
    <table class="striped">
      <thead>
        <tr>
          <th>Projet</th>
          <th>Contributions</th>
          <th>Durée cumulée</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="r in rows" :key="r.id">
          <td>
            <NuxtLink :to="`${base}/${r.id}`">{{ r.name }}</NuxtLink>
          </td>
          <td>{{ r.count }}</td>
          <td>
            <span class="ap-pill">{{ formatDuration(r.total) }}</span>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup lang="ts">
  // Durée cumulée des contributions d'un développeur, projet par projet.
  // base : préfixe de la fiche projet ('/project' ou '/user/me/projects')
  const props = defineProps<{
    items: { duration: number; idProject?: number; projectName?: string }[]
    base: string
  }>()
  const rows = computed(() => sumByProject(props.items))
</script>

<style scoped></style>
