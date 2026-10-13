export const getAccessToken = () => {
  return useCookie('accessToken')
}

export const getRefreshToken = () => {
  return useCookie('refreshToken')
}

export const setAccessToken = (value: string) => {
  const token = useCookie('accessToken')
  token.value = value
}

export const setRefreshToken = (value: string) => {
  const token = useCookie('refreshToken')
  token.value = value
}
