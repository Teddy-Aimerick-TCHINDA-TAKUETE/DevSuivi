<template>
  <div class="ap-grid">
    <!-- <NuxtLink
      v-for="u in items"
      :key="u.id"
      :to="`/user/${u.id}`"
      class="ap-card"
    > -->
    <div v-for="u in items" :key="u.id" class="ap-card">
      <div class="ap-person">
        <span class="ap-avatar">{{ initials(u) }}</span>
        <div>
          <h4>{{ fullName(u) }}</h4>
          <small>{{ u.email }}</small>
        </div>
      </div>
      <p v-if="u.roles?.length" style="margin-top: 0.7rem">
        <span
          v-for="r in u.roles"
          :key="r"
          class="ap-pill"
          :data-tone="roleTone(r)"
          >{{ r }}</span
        >
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { fullName, initials, roleTone } from '~/utils/format'

  defineProps<{
    items: {
      id: number
      firstName: string
      lastName: string
      email: string
      roles: string[]
    }[]
  }>()
</script>

<style scoped></style>
