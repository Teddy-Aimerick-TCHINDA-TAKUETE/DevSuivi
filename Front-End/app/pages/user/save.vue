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
              :required="!id"
              :placeholder="id ? 'Laisser vide pour ne pas le changer' : ''"
            />
            <small v-if="id"
              >Laisser vide pour conserver le mot de passe actuel.</small
            >
          </label>
          <fieldset>
            <legend>Rôles</legend>
            <label v-for="r in ALL_ROLES" :key="r">
              <input
                v-model="form.roles"
                type="checkbox"
                :value="r"
                :disabled="isRoleDisabled(r)"
              />
              {{ r }}
            </label>
            <small
              >Facultatif : sans rôle coché, l’utilisateur est
              <strong>DEVELOPER</strong>. DEVELOPER ne se cumule ni avec ADMIN
              ni avec MANAGER ; ADMIN et MANAGER peuvent se cumuler.</small
            >
          </fieldset>
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
  const { getUserById, saveUser, updateUser, getUserProfil } = useApiUser()
  const { logout } = useSession()
  const existing = id ? await getUserById(id) : null
  const { data: me } = await getUserProfil()
  const itIsMe = ref<boolean>(false)
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
        if (
          me.value?.email.toLocaleLowerCase() === u.email.toLocaleLowerCase()
        ) {
          itIsMe.value = true
          originalEmail.value = u.email
        }
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
      itIsMe.value &&
      originalEmail.value !== '' &&
      form.email.trim().toLowerCase() !== originalEmail.value.toLowerCase(),
  )

  // Tous les champs obligatoires doivent être remplis (les rôles sont facultatifs)
  const canSubmit = computed(
    () =>
      form.firstName.trim() !== '' &&
      form.lastName.trim() !== '' &&
      /^\S+@\S+\.\S+$/.test(form.email.trim()) &&
      (id ? true : form.password !== ''),
  )

  // DEVELOPER est exclusif : coché, il bloque ADMIN et MANAGER, et inversement.
  // Si un compte existant cumule déjà les rôles (ex. compte de test), rien n'est
  // bloqué : on peut décocher librement pour revenir à une combinaison valide.
  const isDeveloper = computed(() => form.roles.includes('DEVELOPER'))
  const isManagement = computed(
    () => form.roles.includes('ADMIN') || form.roles.includes('MANAGER'),
  )
  function isRoleDisabled(role: string) {
    if (isDeveloper.value && isManagement.value) return false
    return role === 'DEVELOPER' ? isManagement.value : isDeveloper.value
  }

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
    if (res.error) {
      message.value = errorMessage(res.error)
      return
    }
    if (emailChanged.value) {
      return await logout()
    }
    await navigateTo(
      res.data?.id ? `/user/${res.data.id}` : '/user/all',
    )
  }
</script>

<style scoped></style>
