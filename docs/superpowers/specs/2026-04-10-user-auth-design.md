# 用户权限模块设计方案

> 文档版本: v1.0
> 创建日期: 2026-04-10
> 状态: 已确认

---

## 1. 模块概述

### 1.1 目标
实现用户认证和基于角色的权限控制，为其他业务模块提供基础安全支撑。

### 1.2 核心功能
- 用户管理（CRUD）
- 角色管理（开发人员、管理员）
- JWT Token 认证
- 权限拦截控制

---

## 2. 数据模型

### 2.1 数据库表设计

#### osm_user（用户表）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| username | VARCHAR(64) | 用户名（唯一） |
| password | VARCHAR(128) | 密码（BCrypt加密） |
| email | VARCHAR(128) | 邮箱 |
| status | TINYINT | 状态（0-禁用，1-启用） |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

#### osm_role（角色表）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| code | VARCHAR(32) | 角色代码（唯一） |
| name | VARCHAR(64) | 角色名称 |
| description | VARCHAR(255) | 角色描述 |
| created_at | DATETIME | 创建时间 |

#### osm_permission（权限表）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| code | VARCHAR(64) | 权限代码（唯一） |
| name | VARCHAR(64) | 权限名称 |
| description | VARCHAR(255) | 权限描述 |
| created_at | DATETIME | 创建时间 |

#### osm_user_role（用户角色关联）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 用户ID |
| role_id | BIGINT | 角色ID |

#### osm_role_permission（角色权限关联）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| role_id | BIGINT | 角色ID |
| permission_id | BIGINT | 权限ID |

#### osm_dict（字典表）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| type | VARCHAR(32) | 字典类型 |
| label | VARCHAR(64) | 字典标签 |
| value | VARCHAR(128) | 字典值 |
| sort | INT | 排序 |
| status | TINYINT | 状态 |

---

## 3. API 设计

### 3.1 认证相关

#### POST /api/auth/login
**请求**:
```json
{
  "username": "admin",
  "password": "password123"
}
```
**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "user": {
      "id": 1,
      "username": "admin",
      "email": "admin@company.com",
      "roles": ["ADMIN"]
    }
  }
}
```

#### GET /api/auth/me
**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "admin",
    "email": "admin@company.com",
    "roles": ["ADMIN"]
  }
}
```

### 3.2 用户管理

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | /api/users | 创建用户 | ADMIN |
| PUT | /api/users/{id} | 更新用户 | ADMIN |
| GET | /api/users/{id} | 获取用户详情 | ADMIN |
| GET | /api/users | 获取用户列表 | ADMIN |
| DELETE | /api/users/{id} | 删除用户 | ADMIN |
| PUT | /api/users/{id}/roles | 分配用户角色 | ADMIN |

### 3.3 角色管理

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | /api/roles | 创建角色 | ADMIN |
| PUT | /api/roles/{id} | 更新角色 | ADMIN |
| GET | /api/roles/{id} | 获取角色详情 | ADMIN |
| GET | /api/roles | 获取角色列表 | ADMIN/DEVELOPER |
| DELETE | /api/roles/{id} | 删除角色 | ADMIN |
| PUT | /api/roles/{id}/permissions | 分配角色权限 | ADMIN |

### 3.4 字典管理

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /api/dicts/{type} | 获取字典项 | ALL |
| GET | /api/dicts | 获取所有字典 | ADMIN |

---

## 4. 权限矩阵

| 权限代码 | 说明 | 管理员 | 开发人员 |
|----------|------|--------|----------|
| user:read | 查看用户 | ✓ | ✗ |
| user:write | 管理用户 | ✓ | ✗ |
| role:read | 查看角色 | ✓ | ✓ |
| role:write | 管理角色 | ✓ | ✗ |
| domain:read | 查看域 | ✓ | ✓（仅负责） |
| domain:write | 管理域 | ✓ | ✗ |
| software:read | 查看软件 | ✓ | ✓ |
| software:write | 管理软件 | ✓ | ✗ |
| order:read | 查看申请 | ✓ | ✓（仅自己） |
| order:write | 提交申请 | ✓ | ✓ |
| order:approve | 审批申请 | ✓ | ✗ |
| usage:read | 查看使用记录 | ✓ | ✓（仅负责） |
| usage:write | 管理使用记录 | ✓ | ✓（仅创建） |
| dict:read | 查看字典 | ✓ | ✓ |
| dict:write | 管理字典 | ✓ | ✗ |

---

## 5. 技术方案

### 5.1 技术选型
- **认证**: JWT (JSON Web Token)
- **安全**: Spring Security 6.x (简化版)
- **密码加密**: BCrypt
- **Token过期**: 24小时

### 5.2 核心组件

#### JwtTokenProvider
- 生成JWT Token
- 验证Token有效性
- 解析用户信息

#### JwtAuthenticationFilter
- 拦截请求，验证Token
- 将用户信息注入SecurityContext

#### SecurityConfig
- 配置Spring Security
- 配置白名单（登录接口等）

#### CustomUserDetailsService
- 实现UserDetailsService
- 加载用户权限信息

---

## 6. 文件结构

```
src/main/java/com/osm/
├── auth/
│   ├── controller/AuthController.java
│   ├── service/AuthService.java
│   ├── service/impl/AuthServiceImpl.java
│   ├── security/
│   │   ├── JwtTokenProvider.java
│   │   ├── JwtAuthenticationFilter.java
│   │   └── CustomUserDetailsService.java
│   └── dto/
│       ├── LoginRequest.java
│       └── LoginResponse.java
├── user/
│   ├── controller/UserController.java
│   ├── service/UserService.java
│   ├── service/impl/UserServiceImpl.java
│   ├── entity/User.java
│   ├── mapper/UserMapper.java
│   ├── dto/CreateUserRequest.java
│   ├── dto/UpdateUserRequest.java
│   └── vo/UserVO.java
├── role/
│   ├── controller/RoleController.java
│   ├── service/RoleService.java
│   ├── service/impl/RoleServiceImpl.java
│   ├── entity/Role.java
│   ├── mapper/RoleMapper.java
│   ├── dto/CreateRoleRequest.java
│   └── vo/RoleVO.java
├── permission/
│   ├── entity/Permission.java
│   └── mapper/PermissionMapper.java
├── dict/
│   ├── controller/DictController.java
│   ├── service/DictService.java
│   ├── service/impl/DictServiceImpl.java
│   ├── entity/Dict.java
│   ├── mapper/DictMapper.java
│   └── vo/DictVO.java
└── config/
    └── SecurityConfig.java
```

---

## 7. 初始数据

### 7.1 角色
| code | name | description |
|------|------|-------------|
| ADMIN | 管理员 | 平台管理员，拥有所有权限 |
| DEVELOPER | 开发人员 | 普通开发人员，受限权限 |

### 7.2 权限
| code | name |
|------|------|
| user:read | 查看用户 |
| user:write | 管理用户 |
| role:read | 查看角色 |
| role:write | 管理角色 |
| domain:read | 查看域 |
| domain:write | 管理域 |
| software:read | 查看软件 |
| software:write | 管理软件 |
| order:read | 查看申请 |
| order:write | 提交申请 |
| order:approve | 审批申请 |
| usage:read | 查看使用记录 |
| usage:write | 管理使用记录 |
| dict:read | 查看字典 |
| dict:write | 管理字典 |

### 7.3 默认用户
| username | password | roles |
|----------|----------|-------|
| admin | admin123 | ADMIN |
| developer | dev123 | DEVELOPER |

### 7.4 字典数据
| type | label | value |
|------|-------|-------|
| tech_category | 数据库 | DATABASE |
| tech_category | 缓存 | CACHE |
| tech_category | 消息队列 | MQ |
| tech_category | Web框架 | WEB |
| tech_category | 工具类 | UTILITY |
| license_type | MIT | MIT |
| license_type | Apache-2.0 | APACHE |
| license_type | GPL-3.0 | GPL |
| license_type | BSD | BSD |
| license_type | MPL-2.0 | MPL |
