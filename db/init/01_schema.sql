-- OSM 开源软件治理平台 - 数据库初始化脚本
-- 执行方式: mysql -h 114.66.38.81 -P 3036 -u <username> -p < 01_schema.sql

-- 创建数据库
CREATE DATABASE IF NOT EXISTS osm CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE osm;

-- 业务域表
CREATE TABLE IF NOT EXISTS osm_domain (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(128) NOT NULL DEFAULT '' COMMENT '域名称',
    description VARCHAR(500) DEFAULT '' COMMENT '域描述',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by VARCHAR(64) NOT NULL DEFAULT '' COMMENT '创建人',
    updated_by VARCHAR(64) NOT NULL DEFAULT '' COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
    INDEX idx_created_at (created_at),
    UNIQUE KEY uk_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='业务域表';

-- 业务系统表
CREATE TABLE IF NOT EXISTS osm_system (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(128) NOT NULL DEFAULT '' COMMENT '系统名称',
    domain_id BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '所属域ID',
    description VARCHAR(500) DEFAULT '' COMMENT '系统简介',
    owners VARCHAR(500) NOT NULL DEFAULT '' COMMENT '负责人列表，逗号分隔',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by VARCHAR(64) NOT NULL DEFAULT '' COMMENT '创建人',
    updated_by VARCHAR(64) NOT NULL DEFAULT '' COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
    INDEX idx_domain_id (domain_id),
    INDEX idx_created_at (created_at),
    UNIQUE KEY uk_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='业务系统表';

-- 应用表
CREATE TABLE IF NOT EXISTS osm_application (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(128) NOT NULL DEFAULT '' COMMENT '应用名称',
    system_id BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '所属系统ID',
    description VARCHAR(500) DEFAULT '' COMMENT '应用描述',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by VARCHAR(64) NOT NULL DEFAULT '' COMMENT '创建人',
    updated_by VARCHAR(64) NOT NULL DEFAULT '' COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
    INDEX idx_system_id (system_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='应用表';

-- 开源软件表
CREATE TABLE IF NOT EXISTS osm_software (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(128) NOT NULL DEFAULT '' COMMENT '软件名称',
    tech_category VARCHAR(64) NOT NULL DEFAULT '' COMMENT '技术类型',
    license_type VARCHAR(64) NOT NULL DEFAULT '' COMMENT '许可证类型',
    description TEXT COMMENT '软件简介',
    doc_url VARCHAR(500) DEFAULT '' COMMENT '文档链接',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态(0-草稿,1-已发布,2-已下线)',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by VARCHAR(64) NOT NULL DEFAULT '' COMMENT '创建人',
    updated_by VARCHAR(64) NOT NULL DEFAULT '' COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
    INDEX idx_tech_category (tech_category),
    INDEX idx_license_type (license_type),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    UNIQUE KEY uk_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='开源软件表';

-- 软件版本表
CREATE TABLE IF NOT EXISTS osm_software_version (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    software_id BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '软件ID',
    version VARCHAR(64) NOT NULL DEFAULT '' COMMENT '版本号',
    description VARCHAR(500) DEFAULT '' COMMENT '版本说明',
    release_date DATE COMMENT '发布日期',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态(0-正常,1-存在漏洞,2-已弃用)',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by VARCHAR(64) NOT NULL DEFAULT '' COMMENT '创建人',
    updated_by VARCHAR(64) NOT NULL DEFAULT '' COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
    INDEX idx_software_id (software_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='软件版本表';

-- 订购申请表
CREATE TABLE IF NOT EXISTS osm_order (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    order_no VARCHAR(64) NOT NULL DEFAULT '' COMMENT '订单编号',
    system_id BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '系统ID',
    software_id BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '软件ID',
    version_id BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '版本ID',
    purpose VARCHAR(500) NOT NULL DEFAULT '' COMMENT '使用用途',
    expected_date DATE COMMENT '期望上线时间',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态(0-草稿,1-已提交,2-审批中,3-已通过,4-已驳回)',
    submitter VARCHAR(64) NOT NULL DEFAULT '' COMMENT '申请人',
    submit_time DATETIME COMMENT '提交时间',
    reviewer VARCHAR(64) DEFAULT '' COMMENT '审批人',
    review_time DATETIME COMMENT '审批时间',
    review_comment VARCHAR(500) DEFAULT '' COMMENT '审批意见',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by VARCHAR(64) NOT NULL DEFAULT '' COMMENT '创建人',
    updated_by VARCHAR(64) NOT NULL DEFAULT '' COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
    INDEX idx_system_id (system_id),
    INDEX idx_software_id (software_id),
    INDEX idx_status (status),
    INDEX idx_submitter (submitter),
    INDEX idx_submit_time (submit_time),
    INDEX idx_order_no (order_no),
    UNIQUE KEY uk_order_no (order_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订购申请表';

-- 使用记录表
CREATE TABLE IF NOT EXISTS osm_usage (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    system_id BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '系统ID',
    software_id BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '软件ID',
    version_id BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '版本ID',
    purpose VARCHAR(500) NOT NULL DEFAULT '' COMMENT '使用用途',
    source_type TINYINT NOT NULL DEFAULT 0 COMMENT '来源(0-历史补录,1-审批自动)',
    order_id BIGINT UNSIGNED DEFAULT 0 COMMENT '关联订单ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by VARCHAR(64) NOT NULL DEFAULT '' COMMENT '创建人',
    updated_by VARCHAR(64) NOT NULL DEFAULT '' COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
    INDEX idx_system_id (system_id),
    INDEX idx_software_id (software_id),
    INDEX idx_source_type (source_type),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='使用记录表';
