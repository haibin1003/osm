-- OSM 用户权限模块数据库迁移
-- V2: 用户、角色、权限、字典表

USE osm;

-- 用户表
CREATE TABLE IF NOT EXISTS osm_user (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(64) NOT NULL DEFAULT '' COMMENT '用户名',
    password VARCHAR(128) NOT NULL DEFAULT '' COMMENT '密码（BCrypt加密）',
    email VARCHAR(128) NOT NULL DEFAULT '' COMMENT '邮箱',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(0-禁用,1-启用)',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by VARCHAR(64) NOT NULL DEFAULT '' COMMENT '创建人',
    updated_by VARCHAR(64) NOT NULL DEFAULT '' COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
    UNIQUE KEY uk_username (username),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 角色表
CREATE TABLE IF NOT EXISTS osm_role (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    code VARCHAR(32) NOT NULL DEFAULT '' COMMENT '角色代码',
    name VARCHAR(64) NOT NULL DEFAULT '' COMMENT '角色名称',
    description VARCHAR(255) DEFAULT '' COMMENT '角色描述',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
    UNIQUE KEY uk_code (code),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 权限表
CREATE TABLE IF NOT EXISTS osm_permission (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    code VARCHAR(64) NOT NULL DEFAULT '' COMMENT '权限代码',
    name VARCHAR(64) NOT NULL DEFAULT '' COMMENT '权限名称',
    description VARCHAR(255) DEFAULT '' COMMENT '权限描述',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
    UNIQUE KEY uk_code (code),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS osm_user_role (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '用户ID',
    role_id BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '角色ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
    UNIQUE KEY uk_user_role (user_id, role_id),
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS osm_role_permission (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    role_id BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '角色ID',
    permission_id BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '权限ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
    UNIQUE KEY uk_role_permission (role_id, permission_id),
    INDEX idx_role_id (role_id),
    INDEX idx_permission_id (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- 字典表
CREATE TABLE IF NOT EXISTS osm_dict (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    type VARCHAR(32) NOT NULL DEFAULT '' COMMENT '字典类型',
    label VARCHAR(64) NOT NULL DEFAULT '' COMMENT '字典标签',
    value VARCHAR(128) NOT NULL DEFAULT '' COMMENT '字典值',
    sort INT NOT NULL DEFAULT 0 COMMENT '排序',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(0-禁用,1-启用)',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
    INDEX idx_type (type),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典表';

-- 插入初始角色数据
INSERT INTO osm_role (code, name, description) VALUES
('ADMIN', '管理员', '平台管理员，拥有所有权限'),
('DEVELOPER', '开发人员', '普通开发人员，受限权限');

-- 插入初始权限数据
INSERT INTO osm_permission (code, name, description) VALUES
('user:read', '查看用户', '查看用户信息'),
('user:write', '管理用户', '创建、编辑、删除用户'),
('role:read', '查看角色', '查看角色信息'),
('role:write', '管理角色', '创建、编辑、删除角色'),
('domain:read', '查看域', '查看域信息'),
('domain:write', '管理域', '创建、编辑、删除域'),
('software:read', '查看软件', '查看软件信息'),
('software:write', '管理软件', '创建、编辑、删除软件'),
('order:read', '查看申请', '查看订购申请'),
('order:write', '提交申请', '提交订购申请'),
('order:approve', '审批申请', '审批订购申请'),
('usage:read', '查看使用记录', '查看使用记录'),
('usage:write', '管理使用记录', '创建、编辑使用记录'),
('dict:read', '查看字典', '查看字典数据'),
('dict:write', '管理字典', '创建、编辑字典数据');

-- 插入初始字典数据
INSERT INTO osm_dict (type, label, value, sort) VALUES
('tech_category', '数据库', 'DATABASE', 1),
('tech_category', '缓存', 'CACHE', 2),
('tech_category', '消息队列', 'MQ', 3),
('tech_category', 'Web框架', 'WEB', 4),
('tech_category', '工具类', 'UTILITY', 5),
('license_type', 'MIT', 'MIT', 1),
('license_type', 'Apache-2.0', 'APACHE', 2),
('license_type', 'GPL-3.0', 'GPL', 3),
('license_type', 'BSD', 'BSD', 4),
('license_type', 'MPL-2.0', 'MPL', 5);

-- 插入默认用户（密码是 BCrypt 加密后的 admin123）
-- 原始密码: admin123 -> BCrypt 加密
INSERT INTO osm_user (username, password, email) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'admin@company.com'),
('developer', '$2a$10$X5wFutsrWd86F2qU8P9mQO2E8yJyP3q2H9Ywv2O6J1K9Z3L5Q7rGe', 'dev@company.com');

-- 关联用户和角色
INSERT INTO osm_user_role (user_id, role_id) VALUES
(1, 1),  -- admin -> ADMIN
(2, 2);  -- developer -> DEVELOPER

-- 角色权限关联
-- ADMIN 拥有所有权限
INSERT INTO osm_role_permission (role_id, permission_id)
SELECT 1, id FROM osm_permission WHERE deleted = 0;

-- DEVELOPER 拥有部分权限
INSERT INTO osm_role_permission (role_id, permission_id)
SELECT 2, id FROM osm_permission WHERE code IN (
    'role:read', 'domain:read', 'software:read',
    'order:read', 'order:write', 'usage:read', 'usage:write', 'dict:read'
);
