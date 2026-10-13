import type { ContributionListDto } from './conrtibutionTypes'
import type { ServerListDto } from './serverTypes'
import type { UserListDto } from './userTypes'

export interface ProjectDetailDto {
  id: number
  name: string
  description: string
  quota: number
  status: string
  server: ServerListDto
  users: UserListDto[]
  contributions: ContributionListDto[]
}

export interface ProjectListDto {
  id: number
  name: string
  description: string
  quota: number
  status: string
  server: ServerListDto
}

export interface ProjectNewDto {
  name: string
  description: string
  quota: number
  status: string
  idServer: number
}
