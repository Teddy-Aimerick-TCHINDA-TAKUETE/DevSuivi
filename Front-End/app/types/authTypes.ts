export interface AuthRequestDto {
  email: string
  password: string
}

export interface AuthResponseDto {
  accessToken: string
  refreshToken: string
  accessExpireAt: Date
  refreshExpireAt: Date
}

export interface RefreshTokenDto {
  refreshToken: string
}
