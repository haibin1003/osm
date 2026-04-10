import axios from 'axios'
import { message } from 'antd'
import type {
  LoginRequest,
  CreateUserRequest,
  UpdateUserRequest,
  CreateRoleRequest,
  UpdateRoleRequest,
  CreateDomainRequest,
  UpdateDomainRequest,
  CreateSystemRequest,
  UpdateSystemRequest,
  CreateApplicationRequest,
  UpdateApplicationRequest,
} from './types'

const api = axios.create({
  baseURL: '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Request interceptor
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor
api.interceptors.response.use(
  (response) => {
    return response.data
  },
  (error) => {
    const msg = error.response?.data?.message || error.message || '网络错误'
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.location.href = '/login'
    }
    message.error(msg)
    return Promise.reject(error)
  }
)

// ============ Auth API ============
export const authApi = {
  login: (data: LoginRequest) => api.post<any, any>('/auth/login', data),
  getCurrentUser: () => api.get<any, any>('/auth/me'),
}

// ============ User API ============
export const userApi = {
  list: () => api.get<any, any>('/users'),
  getById: (id: number) => api.get<any, any>(`/users/${id}`),
  create: (data: CreateUserRequest) => api.post<any, any>('/users', data),
  update: (id: number, data: UpdateUserRequest) => api.put<any, any>(`/users/${id}`, data),
  delete: (id: number) => api.delete<any, any>(`/users/${id}`),
  assignRoles: (id: number, roleIds: number[]) => api.put<any, any>(`/users/${id}/roles`, roleIds),
}

// ============ Role API ============
export const roleApi = {
  list: () => api.get<any, any>('/roles'),
  getById: (id: number) => api.get<any, any>(`/roles/${id}`),
  create: (data: CreateRoleRequest) => api.post<any, any>('/roles', data),
  update: (id: number, data: UpdateRoleRequest) => api.put<any, any>(`/roles/${id}`, data),
  delete: (id: number) => api.delete<any, any>(`/roles/${id}`),
  assignPermissions: (id: number, permissionIds: number[]) =>
    api.put<any, any>(`/roles/${id}/permissions`, permissionIds),
}

// ============ Dict API ============
export const dictApi = {
  getByType: (type: string) => api.get<any, any>(`/dicts/${type}`),
}

// ============ Domain API ============
export const domainApi = {
  list: () => api.get<any, any>('/domains'),
  getById: (id: number) => api.get<any, any>(`/domains/${id}`),
  create: (data: CreateDomainRequest) => api.post<any, any>('/domains', data),
  update: (id: number, data: UpdateDomainRequest) => api.put<any, any>(`/domains/${id}`, data),
  delete: (id: number) => api.delete<any, any>(`/domains/${id}`),
}

// ============ System API ============
export const systemApi = {
  list: () => api.get<any, any>('/systems'),
  getById: (id: number) => api.get<any, any>(`/systems/${id}`),
  create: (data: CreateSystemRequest) => api.post<any, any>('/systems', data),
  update: (id: number, data: UpdateSystemRequest) => api.put<any, any>(`/systems/${id}`, data),
  delete: (id: number) => api.delete<any, any>(`/systems/${id}`),
  listByDomain: (domainId: number) => api.get<any, any>(`/systems/domain/${domainId}`),
}

// ============ Application API ============
export const applicationApi = {
  list: () => api.get<any, any>('/applications'),
  getById: (id: number) => api.get<any, any>(`/applications/${id}`),
  create: (data: CreateApplicationRequest) => api.post<any, any>('/applications', data),
  update: (id: number, data: UpdateApplicationRequest) => api.put<any, any>(`/applications/${id}`, data),
  delete: (id: number) => api.delete<any, any>(`/applications/${id}`),
  listBySystem: (systemId: number) => api.get<any, any>(`/applications/system/${systemId}`),
}

export default api
