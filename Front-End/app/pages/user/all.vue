<template>
  <div>
    <AppPage
      title="Tous les utilisateurs"
      subtitle="Recherchez, modifiez ou supprimez des comptes."
      back="/user"
    >
      <template #actions>
        <NuxtLink to="/user/save" role="button" class="ap-btn"
          >+ Nouvel utilisateur</NuxtLink
        >
      </template>

      <input
        v-model="search"
        type="search"
        placeholder="Rechercher par nom, email ou rôle…"
      />
      <article v-if="message" class="ap-error">{{ message }}</article>

      <DataState
        :pending="status === 'pending'"
        :error="error"
        :empty="!filtered.length"
        empty-text="Aucun utilisateur trouvé."
      >
        <div class="ap-table">
          <table class="striped">
            <thead>
              <tr>
                <th>Utilisateur</th>
                <th>Email</th>
                <th>Rôles</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="u in filtered" :key="u.id">
                <td>
                  <NuxtLink :to="`/user/${u.id}`" class="ap-person">
                    <span class="ap-avatar">{{ initials(u) }}</span>
                    <strong>{{ fullName(u) }}</strong>
                  </NuxtLink>
                </td>
                <td class="ap-muted">{{ u.email }}</td>
                <td>
                  <span
                    v-for="r in u.roles"
                    :key="r"
                    class="ap-pill"
                    :data-tone="roleTone(r)"
                    >{{ r }}</span
                  >
                </td>
                <td class="ap-cell-actions">
                  <NuxtLink
                    :to="`/user/save?id=${u.id}`"
                    role="button"
                    class="ap-btn ap-sm outline"
                    >Modifier</NuxtLink
                  >
                  <button
                    class="ap-btn ap-sm danger"
                    @click="remove(u.id, fullName(u))"
                  >
                    Supprimer
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
  import { fullName, initials, errorMessage, roleTone } from '~/utils/format'

  const { getAllUsers, deleteUser } = useApiUser()
  const { data: users, status, error, refresh } = await getAllUsers()

  const search = ref('')
  const message = ref('')
  const filtered = computed(() => {
    const q = search.value.trim().toLowerCase()
    return (users.value ?? []).filter(
      (u) =>
        !q ||
        `${u.firstName} ${u.lastName} ${u.email} ${u.roles?.join(' ')}`
          .toLowerCase()
          .includes(q),
    )
  })

  async function remove(id: number, name: string) {
    if (!confirm(`Supprimer l’utilisateur « ${name} » ?`)) return
    const { error: err } = await deleteUser(id)
    message.value = err.value ? errorMessage(err.value) : ''
    await refresh()
  }
</script>

<style scoped></style>
