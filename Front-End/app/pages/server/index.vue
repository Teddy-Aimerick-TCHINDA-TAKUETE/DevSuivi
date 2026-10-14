<template>
  <div>
    <AppPage
      title="Serveurs"
      subtitle="Les machines qui hébergent vos projets."
    >
      <template #actions>
        <NuxtLink to="/server/save" role="button" class="ap-btn"
          >+ Nouveau serveur</NuxtLink
        >
        <NuxtLink to="/server/all" role="button" class="ap-btn ap-ghost"
          >Tout voir</NuxtLink
        >
      </template>

      <DataState
        :pending="status === 'pending'"
        :error="error"
        :empty="!servers?.length"
        empty-text="Aucun serveur enregistré."
      >
        <div class="ap-grid">
          <article class="ap-stat">
            <strong>{{ servers?.length }}</strong
            ><span>Serveurs</span>
          </article>
        </div>
        <div class="ap-grid">
          <NuxtLink
            v-for="s in servers"
            :key="s.id"
            :to="`/server/${s.id}`"
            class="ap-card"
          >
            <div class="ap-card-top"><span class="ap-icon">🖥️</span></div>
            <h4>{{ s.name }}</h4>
            <p>{{ s.description }}</p>
          </NuxtLink>
        </div>
      </DataState>
    </AppPage>
  </div>
</template>

<script setup lang="ts">
  const { data: servers, status, error } = await useApiServer().getAllServers()
</script>

<style scoped></style>
