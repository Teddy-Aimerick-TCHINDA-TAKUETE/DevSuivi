import type { AuthRequestDto, AuthResponseDto } from '~/types/authTypes'
import type { UserDetailDto, UserNewDto } from '~/types/userTypes'

export function useApiAuth() {
  const config = useRuntimeConfig()
  const apiBase = config.public.apiBase

  async function loging(authRequestDto: AuthRequestDto) {
    return await useFetch<AuthResponseDto>(() => `${apiBase}/auth/login`, {
      method: 'POST',
      body: authRequestDto,
    })
  }
  async function register(userNewDto: UserNewDto) {
    return await useFetch<UserDetailDto>(() => `${apiBase}/auth/register`, {
      method: 'POST',
      body: userNewDto,
    })
  }
  async function refresh(authRequestDto: AuthRequestDto) {
    return await useFetch<AuthResponseDto>(() => `${apiBase}/auth/refresh`, {
      method: 'POST',
      body: authRequestDto,
    })
  }

  return {
    loging,
    register,
    refresh,
  }
}
