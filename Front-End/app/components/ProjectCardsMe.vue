<template>
  <div class="ap-grid">
    <component
      :is="p.id ? NuxtLink : 'div'"
      v-for="(p, i) in items"
      :key="p.id ?? i"
      :to="p.id ? `/user/me/projets/${p.id}` : undefined"
      class="ap-card"
    >
      <div class="ap-card-top">
        <span class="ap-pill" :data-tone="statusTone(p.status)">{{
          p.status
        }}</span>
        <small class="ap-muted">Quota {{ p.quota }}</small>
      </div>
      <h4>{{ p.name }}</h4>
      <p>{{ p.description }}</p>
    </component>
  </div>
</template>

<script setup lang="ts">
  import { statusTone } from '~/utils/format'

  defineProps<{
    items: {
      id?: number
      name: string
      description: string
      status: string
      quota: number
    }[]
  }>()
  const NuxtLink = resolveComponent('NuxtLink')
</script>

<style scoped></style>
