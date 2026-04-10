// Common API Response
export interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

// Pagination
export interface PageQuery {
  pageNum: number
  pageSize: number
}

export interface PageResult<T> {
  list: T[]
  total: number
  pageNum: number
  pageSize: number
}

// Base Entity
export interface BaseEntity {
  id: number
  createdAt: string
  updatedAt: string
  createdBy: string
  updatedBy: string
}

// Common Status
export enum CommonStatus {
  DISABLED = 0,
  ENABLED = 1,
}
