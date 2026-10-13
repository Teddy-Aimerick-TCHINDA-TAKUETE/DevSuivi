import type { ContributionListDto } from './conrtibutionTypes'
import type { ProjectListDto } from './projectTypes'

export interface UserDetailDto {
  id: number
  firstName: string
  lastName: string
  email: string
  roles: string[]
  projects: ProjectListDto[]
  contributions: ContributionListDto[]
}

export interface UserListDto {
  id: number
  firstName: string
  lastName: string
  email: string
  roles: string[]
}

export interface UserNewDto {
  firstName: string
  lastName: string
  email: string
  password: string
  roles: string[]
}
