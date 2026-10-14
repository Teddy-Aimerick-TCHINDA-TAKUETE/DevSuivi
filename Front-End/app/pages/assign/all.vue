<template>
  <div>
    <AppPage
      title="Toutes les affectations"
      subtitle="Retirez ou consultez les affectations existantes."
      back="/assign"
    >
      <template #actions>
        <NuxtLink to="/assign/save" role="button" class="ap-btn"
          >+ Nouvelle affectation</NuxtLink
        >
      </template>

      <input
        v-model="search"
        type="search"
        placeholder="Rechercher un utilisateur ou un projet…"
      />
      <article v-if="message" class="ap-error">{{ message }}</article>

      <DataState
        :pending="status === 'pending'"
        :error="error"
        :empty="!filtered.length"
        empty-text="Aucune affectation trouvée."
      >
        <div class="ap-table">
          <table class="striped">
            <thead>
              <tr>
                <th>Utilisateur</th>
                <th>Projet</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="a in filtered" :key="`${a.user.id}-${a.project.id}`">
                <td>
                  <NuxtLink :to="`/user/${a.user.id}`" class="ap-person">
                    <span class="ap-avatar">{{ initials(a.user) }}</span>
                    <strong>{{ fullName(a.user) }}</strong>
                  </NuxtLink>
                </td>
                <td>
                  <NuxtLink :to="`/projet/${a.project.id}`">{{
                    a.project.name
                  }}</NuxtLink>
                </td>
                <td class="ap-cell-actions">
                  <NuxtLink
                    :to="`/assign/id?user=${a.user.id}&project=${a.project.id}`"
                    role="button"
                    class="ap-btn ap-sm secondary outline"
                    >Détails</NuxtLink
                  >
                  <button class="ap-btn ap-sm danger" @click="remove(a)">
                    Retirer
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </DataState>
    </AppPage>
  </div>
</template>

<script setup lang="ts">
  import type { AssignListDto } from '~/types/assignTypes'
  import { fullName, errorMessage, initials } from '~/utils/format'

  const { getAllAssigns, deleteAssign } = useApiAssign()
  const { data: assigns, status, error, refresh } = await getAllAssigns()

  const search = ref('')
  const message = ref('')
  const filtered = computed(() => {
    const q = search.value.trim().toLowerCase()
    return (assigns.value ?? []).filter(
      (a) =>
        !q ||
        `${fullName(a.user)} ${a.user.email} ${a.project.name}`
          .toLowerCase()
          .includes(q),
    )
  })

  async function remove(a: AssignListDto) {
    if (
      !(await askConfirm({
        title: 'Retirer l’affectation',
        message: `${fullName(a.user)} ne sera plus affecté au projet « ${a.project.name} ».`,
        confirmLabel: 'Retirer',
      }))
    )
      return
    const { error: err } = await deleteAssign({
      user: a.user.id,
      project: a.project.id,
    })
    message.value = err.value ? errorMessage(err.value) : ''
    await refresh()
  }
</script>

<style scoped></style>
