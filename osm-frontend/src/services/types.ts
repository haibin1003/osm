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

// ============ Auth Types ============
export interface LoginRequest {
  username: string
  password: string
}

export interface LoginResponse {
  token: string
  user: UserInfo
}

export interface UserInfo {
  id: number
  username: string
  email: string
  roles: string[]
}

// ============ User Types ============
export interface UserVO {
  id: number
  username: string
  email: string
  status: number
  roles: string[]
  createdAt: string
  updatedAt: string
}

export interface CreateUserRequest {
  username: string
  password: string
  email: string
  status?: number
}

export interface UpdateUserRequest {
  username?: string
  password?: string
  email?: string
  status?: number
}

// ============ Role Types ============
export interface RoleVO {
  id: number
  code: string
  name: string
  description: string
  permissions: string[]
  createdAt: string
  updatedAt: string
}

export interface CreateRoleRequest {
  code: string
  name: string
  description?: string
}

export interface UpdateRoleRequest {
  name: string
  description?: string
}

// ============ Dict Types ============
export interface DictVO {
  id: number
  type: string
  label: string
  value: string
  sort: number
  status: number
}

// ============ Domain Types ============
export interface DomainVO {
  id: number
  name: string
  description: string
  createdAt: string
  updatedAt: string
}

export interface CreateDomainRequest {
  name: string
  description?: string
}

export interface UpdateDomainRequest {
  name: string
  description?: string
}

// ============ System Types ============
export interface SystemVO {
  id: number
  name: string
  description: string
  domainId: number
  code: string
  createdAt: string
  updatedAt: string
}

export interface CreateSystemRequest {
  name: string
  description?: string
  domainId: number
  code: string
}

export interface UpdateSystemRequest {
  name: string
  description?: string
  domainId?: number
  code: string
}

// ============ Application Types ============
export interface ApplicationVO {
  id: number
  name: string
  description: string
  systemId: number
  code: string
  createdAt: string
  updatedAt: string
}

export interface CreateApplicationRequest {
  name: string
  description?: string
  systemId: number
  code: string
}

export interface UpdateApplicationRequest {
  name: string
  description?: string
  systemId?: number
  code: string
}
