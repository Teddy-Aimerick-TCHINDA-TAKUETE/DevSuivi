import type { AuthRequestDto, AuthResponseDto } from '~/types/authTypes'

export function useApiAuth() {
  const config = useRuntimeConfig()
  const apiBase = config.public.apiBase

  async function loging(authRequestDto: AuthRequestDto) {
    return await useFetch<AuthResponseDto>(() => `${apiBase}/auth/login`, {
      method: 'POST',
      body: authRequestDto,
    })
  }

  return {
    loging,
  }
}
