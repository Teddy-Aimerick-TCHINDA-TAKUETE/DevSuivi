<template>
  <div>
    <AppPage
      :title="id ? 'Modifier l’utilisateur' : 'Nouvel utilisateur'"
      subtitle="Identité, accès et rôles."
      back="/user/all"
    >
      <article class="ap-narrow">
        <form @submit.prevent="submit">
          <div class="ap-form-grid">
            <label>
              Prénom
              <input v-model="form.firstName" required />
            </label>
            <label>
              Nom
              <input v-model="form.lastName" required />
            </label>
          </div>
          <label>
            Email
            <input v-model="form.email" type="email" required />
          </label>
          <label>
            Mot de passe
            <input
              v-model="form.password"
              type="password"
              autocomplete="new-password"
              required
            />
            <small v-if="id"
              >Le mot de passe est requis par l’API, même pour une
              modification.</small
            >
          </label>
          <fieldset>
            <legend>Rôles</legend>
            <label v-for="r in ALL_ROLES" :key="r">
              <input v-model="form.roles" type="checkbox" :value="r" />
              {{ r }}
            </label>
            <small
              >Facultatif : sans rôle coché, l’utilisateur est
              <strong>DEVELOPER</strong>.</small
            >
          </fieldset>
          <p v-if="message" class="ap-error" style="padding: 0.6rem 0.9rem">
            {{ message }}
          </p>
          <button
            type="submit"
            class="ap-btn"
            :aria-busy="submitting"
            :disabled="submitting || !canSubmit"
          >
            {{ id ? 'Enregistrer les modifications' : 'Créer l’utilisateur' }}
          </button>
        </form>
      </article>
    </AppPage>
  </div>
</template>

<script setup lang="ts">
  import { ALL_ROLES, errorMessage } from '~/utils/format'

  definePageMeta({ key: (route) => route.fullPath })

  // /user/save → création · /user/save?id=4 → modification
  const id = Number(useRoute().query.id) || null
  const { getUserById, saveUser, updateUser } = useApiUser()
  const existing = id ? await getUserById(id) : null

  const form = reactive({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    roles: [] as string[],
  })
  if (existing) {
    watch(
      existing.data,
      (u) => {
        if (!u) return
        Object.assign(form, {
          firstName: u.firstName,
          lastName: u.lastName,
          email: u.email,
          roles: [...(u.roles ?? [])],
        })
      },
      { immediate: true },
    )
  }

  // Tous les champs obligatoires doivent être remplis (les rôles sont facultatifs)
  const canSubmit = computed(
    () =>
      form.firstName.trim() !== '' &&
      form.lastName.trim() !== '' &&
      /^\S+@\S+\.\S+$/.test(form.email.trim()) &&
      form.password !== '',
  )

  const submitting = ref(false)
  const message = ref('')

  async function submit() {
    submitting.value = true
    message.value = ''
    // Aucun rôle coché → DEVELOPER par défaut
    const dto = {
      ...form,
      roles: form.roles.length ? [...form.roles] : ['DEVELOPER'],
    }
    const res = id ? await updateUser(id, dto) : await saveUser(dto)
    submitting.value = false
    if (res.error.value) {
      message.value = errorMessage(res.error.value)
      return
    }
    await navigateTo(
      res.data.value?.id ? `/user/${res.data.value.id}` : '/user/all',
    )
  }
</script>

<style scoped></style>
