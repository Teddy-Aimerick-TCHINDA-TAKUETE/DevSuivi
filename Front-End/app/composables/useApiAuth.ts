import type {
  AuthRequestDto,
  AuthResponseDto,
  RefreshTokenDto,
} from '~/types/authTypes'
import type { UserDetailDto, UserNewDto } from '~/types/userTypes'

// Routes publiques (/auth/**) : pas de jeton à envoyer, donc $fetch directement
// (surtout pas $api, qui tenterait un renouvellement sur un 401 « mauvais mot de passe »).
export function useApiAuth() {
  const baseURL = useRuntimeConfig().public.apiBase

  function loging(authRequestDto: AuthRequestDto) {
    return appeler<AuthResponseDto>($fetch, '/auth/login', {
      baseURL,
      method: 'POST',
      body: authRequestDto,
    })
  }
  function register(userNewDto: UserNewDto) {
    return appeler<UserDetailDto>($fetch, '/auth/register', {
      baseURL,
      method: 'POST',
      body: userNewDto,
    })
  }
  function refresh(refreshTokenDto: RefreshTokenDto) {
    return appeler<AuthResponseDto>($fetch, '/auth/refresh', {
      baseURL,
      method: 'POST',
      body: refreshTokenDto,
    })
  }
  // Révoque le refresh token côté serveur
  function logout(refreshTokenDto: RefreshTokenDto) {
    return appeler($fetch, '/auth/logout', {
      baseURL,
      method: 'POST',
      body: refreshTokenDto,
    })
  }

  return {
    loging,
    register,
    refresh,
    logout,
  }
}
