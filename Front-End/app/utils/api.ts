import type { FetchError } from 'ofetch'

// Résultat d'une écriture (POST / PUT / DELETE) : jamais d'exception, on teste `error`.
export interface Resultat<T> {
  data: T | null
  error: FetchError | null
}

/**
 * Envoie une requête d'écriture avec $fetch (une seule requête, pas de cache,
 * pas d'état partagé) et range le résultat dans { data, error }.
 */
export async function appeler<T = unknown>(
  api: typeof $fetch,
  request: string,
  options: Parameters<typeof $fetch>[1] = {},
): Promise<Resultat<T>> {
  try {
    const data = await api<T>(request, options as any)
    return { data: (data ?? null) as T | null, error: null }
  } catch (e) {
    return { data: null, error: e as FetchError }
  }
}
