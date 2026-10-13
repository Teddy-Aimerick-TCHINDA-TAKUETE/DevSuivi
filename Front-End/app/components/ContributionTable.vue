<template>
  <div class="ap-table">
    <table class="striped">
      <thead>
        <tr>
          <th>Date</th>
          <th>Durée</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="c in items" :key="c.id">
          <td>{{ formatDate(c.dateContribution) }}</td>
          <td>
            <span class="ap-pill">{{ c.duration }}</span>
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
    items: { id: number; duration: number; dateContribution: string | Date }[]
  }>()
</script>

<style scoped></style>
