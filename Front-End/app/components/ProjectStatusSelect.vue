<template>
  <span class="status-select">
    <select
      :value="project.status"
      :disabled="saving"
      :aria-busy="saving"
      :aria-label="`Changer l’état de ${project.name}`"
      @change="change"
    >
      <option v-for="s in PROJECT_STATUSES" :key="s" :value="s">
        {{ s }}
      </option>
    </select>
    <small v-if="message" class="ap-error">{{ message }}</small>
  </span>
</template>

<script setup lang="ts">
  import type { ProjectNewDto } from '~/types/projectTypes'
  import { PROJECT_STATUSES, errorMessage } from '~/utils/format'

  // Change uniquement l'état d'un projet : on renvoie le projet tel quel
  // (PUT /project/{id}) avec le seul champ status modifié.
  const props = defineProps<{
    project: {
      id: number
      name: string
      description: string
      quota: number
      status: string
      server?: { id: number } | null
    }
  }>()
  const emit = defineEmits<{ changed: [status: string] }>()

  const { updateProject } = useApiProject()
  const saving = ref(false)
  const message = ref('')

  async function change(e: Event) {
    const select = e.target as HTMLSelectElement
    const status = select.value
    const previous = props.project.status
    saving.value = true
    message.value = ''
    const dto: ProjectNewDto = {
      name: props.project.name,
      description: props.project.description,
      quota: props.project.quota,
      status,
      idServer: props.project.server?.id ?? 0,
    }
    const res = await updateProject(props.project.id, dto)
    saving.value = false
    if (res.error) {
      message.value = errorMessage(res.error)
      select.value = previous
      return
    }
    emit('changed', status)
  }
</script>

<style scoped>
  .status-select select {
    margin: 0;
    padding-block: 0.25rem;
    width: auto;
    min-width: 10rem;
  }
</style>
