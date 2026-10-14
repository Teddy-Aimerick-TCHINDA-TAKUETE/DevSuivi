<template>
  <ClientOnly>
    <Teleport to="body">
      <dialog
        v-if="state"
        open
        role="alertdialog"
        aria-modal="true"
        aria-labelledby="confirm-title"
        aria-describedby="confirm-message"
        @click.self="answerConfirm(false)"
        @keydown.esc.prevent="answerConfirm(false)"
        @keydown.tab="trapFocus"
      >
        <article class="confirm-box" :data-danger="state.danger">
          <header>
            <span class="confirm-icon" aria-hidden="true">
              {{ state.danger ? '!' : '?' }}
            </span>
            <h3 id="confirm-title">{{ state.title ?? 'Confirmation' }}</h3>
          </header>
          <p id="confirm-message">{{ state.message }}</p>
          <footer>
            <button
              ref="cancelBtn"
              type="button"
              class="secondary outline"
              @click="answerConfirm(false)"
            >
              {{ state.cancelLabel ?? 'Annuler' }}
            </button>
            <button
              ref="okBtn"
              type="button"
              class="confirm-ok"
              @click="answerConfirm(true)"
            >
              {{ state.confirmLabel ?? 'Confirmer' }}
            </button>
          </footer>
        </article>
      </dialog>
    </Teleport>
  </ClientOnly>
</template>

<script setup lang="ts">
  import { answerConfirm, confirmState } from '~/utils/confirm'

  const state = confirmState
  const cancelBtn = ref<HTMLButtonElement | null>(null)
  const okBtn = ref<HTMLButtonElement | null>(null)
  let previouslyFocused: HTMLElement | null = null

  // Ouverture : on bloque le scroll de la page et on met le focus sur « Annuler »
  // (choix le plus sûr si l'utilisateur appuie trop vite sur Entrée).
  // Fermeture : on rend le focus à l'élément qui avait ouvert la boîte.
  watch(state, async (value) => {
    const root = document.documentElement
    if (value) {
      previouslyFocused = document.activeElement as HTMLElement | null
      root.classList.add('modal-is-open')
      await nextTick()
      cancelBtn.value?.focus()
    } else {
      root.classList.remove('modal-is-open')
      previouslyFocused?.focus?.()
      previouslyFocused = null
    }
  })

  onBeforeUnmount(() => {
    document.documentElement.classList.remove('modal-is-open')
    answerConfirm(false)
  })

  // Tab / Maj+Tab restent dans la boîte
  function trapFocus(e: KeyboardEvent) {
    const first = cancelBtn.value
    const last = okBtn.value
    if (!first || !last) return
    if (e.shiftKey && document.activeElement === first) {
      e.preventDefault()
      last.focus()
    } else if (!e.shiftKey && document.activeElement === last) {
      e.preventDefault()
      first.focus()
    }
  }
</script>

<style scoped>
  .confirm-box {
    width: min(26rem, 100%);
    margin: 0;
  }
  .confirm-box header {
    display: flex;
    border-bottom: none;
    background: transparent;
    padding: 0;
    align-items: center;
    gap: 0.75rem;
    margin: 0 0 0.9rem;
  }
  .confirm-box h3 {
    margin: 0;
    font-size: 1.15rem;
  }
  .confirm-icon {
    flex: none;
    display: grid;
    place-items: center;
    width: 2.2rem;
    height: 2.2rem;
    border-radius: 50%;
    font-weight: 700;
    background: rgba(99, 102, 241, 0.15);
    color: #4338ca;
  }
  .confirm-box[data-danger='true'] .confirm-icon {
    background: rgba(239, 68, 68, 0.15);
    color: #b91c1c;
  }
  .confirm-box p {
    margin-bottom: 1.4rem;
    color: var(--pico-muted-color);
  }
  .confirm-box footer {
    display: flex;
    justify-content: flex-end;
    gap: 0.6rem;
    margin: 0;
    padding: 0;
    border-top: none;
    background: transparent;
  }
  .confirm-box footer button {
    width: auto;
    margin: 0;
  }
  .confirm-box[data-danger='true'] .confirm-ok {
    --pico-background-color: #dc2626;
    --pico-border-color: #dc2626;
    --pico-color: #fff;
  }
  .confirm-box[data-danger='true'] .confirm-ok:is(:hover, :active, :focus) {
    --pico-background-color: #b91c1c;
    --pico-border-color: #b91c1c;
  }
  @media (max-width: 480px) {
    .confirm-box footer {
      flex-direction: column-reverse;
    }
    .confirm-box footer button {
      width: 100%;
    }
  }
</style>
