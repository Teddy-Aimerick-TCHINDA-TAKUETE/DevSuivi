import type { ProjectListDto } from './projectTypes'
import type { UserListDto } from './userTypes'

export interface AssignId {
  user: number
  project: number
}

export interface AssignIdNewDto {
  user: number
  project: number
}

export interface AssignListDto {
  user: UserListDto
  project: ProjectListDto
}
