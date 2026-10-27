<template>
  <div>
    <AppPage
      :title="'Modifier votre profil'"
      subtitle="Identité, accès et rôles."
      back="/profil"
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
            Nouveau mot de passe
            <input
              v-model="form.password"
              type="password"
              autocomplete="new-password"
              placeholder="Laisser vide pour ne pas le changer"
            />
          </label>
          <p v-if="emailChanged" class="ap-muted" role="status">
            ⚠️ En changeant votre email, vous serez déconnecté et devrez vous
            reconnecter avec le nouvel email.
          </p>
          <p v-if="message" class="ap-error" style="padding: 0.6rem 0.9rem">
            {{ message }}
          </p>
          <button
            type="submit"
            class="ap-btn"
            :aria-busy="submitting"
            :disabled="submitting || !canSubmit"
          >
            {{ 'Enregistrer les modifications' }}
          </button>
        </form>
      </article>
    </AppPage>
  </div>
</template>

<script setup lang="ts">
  import { ALL_ROLES, errorMessage } from '~/utils/format'

  definePageMeta({ key: (route) => route.fullPath })

  const { getUserProfil, updateMe } = useApiUser()
  const { logout } = useSession()
  const existing = await getUserProfil()
  const originalEmail = ref('')

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
        originalEmail.value = u.email
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

  const emailChanged = computed(
    () =>
      originalEmail.value !== '' &&
      form.email.trim().toLowerCase() !== originalEmail.value.toLowerCase(),
  )

  // Tous les champs obligatoires doivent être remplis (les rôles sont facultatifs)
  const canSubmit = computed(
    () =>
      form.firstName.trim() !== '' &&
      form.lastName.trim() !== '' &&
      /^\S+@\S+\.\S+$/.test(form.email.trim()),
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
    const res = await updateMe(dto)
    submitting.value = false
    if (res.error) {
      message.value = errorMessage(res.error)
      return
    }
    // email changé : le JWT n'est plus valable, on se reconnecte
    if (emailChanged.value) {
      return await logout()
    }
    await navigateTo('/profil')
  }
</script>

<style scoped></style>
