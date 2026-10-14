// Boîte de confirmation maison (remplace window.confirm).
// Utilisation : if (!(await askConfirm({ title, message }))) return
// Le composant <ConfirmDialog /> (monté dans app.vue) affiche la boîte.

export interface ConfirmOptions {
  title?: string
  message: string
  confirmLabel?: string
  cancelLabel?: string
  /** Bouton de confirmation rouge (défaut : true) */
  danger?: boolean
}

// État partagé, uniquement modifié côté navigateur (clic sur un bouton)
export const confirmState = shallowRef<ConfirmOptions | null>(null)

let resolver: ((answer: boolean) => void) | null = null

export function askConfirm(options: ConfirmOptions): Promise<boolean> {
  resolver?.(false) // une seule boîte à la fois
  confirmState.value = { danger: true, ...options }
  return new Promise<boolean>((resolve) => {
    resolver = resolve
  })
}

export function answerConfirm(answer: boolean) {
  confirmState.value = null
  resolver?.(answer)
  resolver = null
}
