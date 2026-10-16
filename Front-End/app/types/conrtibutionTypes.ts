import type { ProjectListDto } from './projectTypes'
import type { UserListDto } from './userTypes'

export interface ContributionDetailDto {
  id: number
  duration: number
  dateContribution: Date
  user: UserListDto
  project: ProjectListDto
}

export interface ContributionListDto {
  id: number
  duration: number
  dateContribution: Date
  // Renvoyés par le Back-End : développeur et projet de la contribution
  idUser?: number
  userName?: string
  idProject?: number
  projectName?: string
}

export interface ContributionNewDto {
  duration: number
  dateContribution: Date
  idUser: number
  idProject: number
}
