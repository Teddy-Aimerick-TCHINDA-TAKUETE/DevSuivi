<template>
  <div class="ap-table">
    <table class="striped">
      <thead>
        <tr>
          <th>Date</th>
          <th v-if="showUser">Développeur</th>
          <th v-if="showProject">Projet</th>
          <th>Durée</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="c in items" :key="c.id">
          <td>{{ formatDate(c.dateContribution) }}</td>
          <td v-if="showUser">{{ c.userName ?? '—' }}</td>
          <td v-if="showProject">{{ c.projectName ?? '—' }}</td>
          <td>
            <span class="ap-pill">{{ formatDuration(c.duration) }}</span>
          </td>
          <td class="ap-cell-actions">
            <NuxtLink :to="`/contribution/${c.id}`">Détails →</NuxtLink>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup lang="ts">
  import { formatDate } from '~/utils/format'

  defineProps<{
    items: {
      id: number
      duration: number
      dateContribution: string | Date
      userName?: string
      projectName?: string
    }[]
    showUser?: boolean
    showProject?: boolean
  }>()
</script>

<style scoped></style>
