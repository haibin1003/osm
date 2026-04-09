# 数据库规范

> MySQL 8.0 数据库设计、命名、索引规范

---

## 1. 命名规范

### 1.1 数据库命名

```sql
-- 环境区分
osm_dev       -- 开发环境
osm_test      -- 测试环境
osm_prod      -- 生产环境
```

### 1.2 表命名

```sql
-- 格式：os_<模块>_<表名>
-- 小写，下划线分隔

os_order              -- 订购申请
os_order_history      -- 订购申请历史
os_system             -- 业务系统
os_software           -- 开源软件
os_software_version   -- 软件版本
os_usage              -- 使用记录
```

### 1.3 字段命名

```sql
-- 小写，下划线分隔
id                    -- 主键
created_at            -- 创建时间
updated_at            -- 更新时间
deleted               -- 逻辑删除标记

system_id             -- 系统ID（外键）
software_name         -- 软件名称
status                -- 状态
```

### 1.4 索引命名

```sql
-- 主键：pk_表名
PRIMARY KEY pk_order

-- 唯一索引：uk_表名_字段名
UNIQUE KEY uk_system_name (name)

-- 普通索引：idx_表名_字段名
INDEX idx_order_system_id (system_id)
INDEX idx_order_status_created_at (status, created_at)

-- 全文索引：ft_表名_字段名
FULLTEXT INDEX ft_software_description (description)
```

---

## 2. 字段设计规范

### 2.1 必备字段

```sql
-- 每个表必须有这5个字段
CREATE TABLE os_example (
    id              BIGINT UNSIGNED     AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    created_at      DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by      VARCHAR(64)         NOT NULL DEFAULT '' COMMENT '创建人',
    updated_by      VARCHAR(64)         NOT NULL DEFAULT '' COMMENT '更新人',
    -- deleted         TINYINT             NOT NULL DEFAULT 0 COMMENT '逻辑删除标记(0-未删除,1-已删除)',
    
    -- 业务字段...
    
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='示例表';
```

### 2.2 字段类型选择

| 数据类型 | 使用场景 | 示例 |
|----------|----------|------|
| `BIGINT UNSIGNED` | 主键ID、大整数 | id, user_id |
| `INT UNSIGNED` | 状态码、小整数 | status, sort_order |
| `VARCHAR(n)` | 短字符串（n<255） | name, code |
| `TEXT` | 长文本 | description, content |
| `DECIMAL(10,2)` | 金额、精确小数 | amount, price |
| `DATETIME` | 日期时间 | created_at, submit_time |
| `DATE` | 仅日期 | expected_date |
| `TINYINT` | 布尔值、小范围枚举 | is_deleted, type |
| `JSON` | 结构化数据（少用） | extra_config |

### 2.3 字段约束

```sql
-- NOT NULL 优先
`name`          VARCHAR(128)    NOT NULL DEFAULT '' COMMENT '名称',
`description`   VARCHAR(500)    NULL COMMENT '描述（可选）',

-- 状态字段用 TINYINT
`status`        TINYINT         NOT NULL DEFAULT 0 COMMENT '状态(0-草稿,1-已提交,2-审批中,3-已通过,4-已驳回)',

-- 外键字段类型一致
`system_id`     BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '系统ID',

-- 金额用 DECIMAL
`amount`        DECIMAL(10,2)   NOT NULL DEFAULT 0.00 COMMENT '金额',
```

---

## 3. 索引设计规范

### 3.1 索引原则

```
✅ 必须建索引：
- 主键（自动）
- 外键字段
- WHERE 条件字段
- ORDER BY 字段
- 联合查询关联字段

❌ 不要建索引：
- 区分度低的字段（如性别）
- 频繁更新的字段（索引维护成本高）
- 小表（数据量<1000）

⚠️ 谨慎建索引：
- 多列索引考虑最左前缀
- 索引过多影响写入性能
```

### 3.2 联合索引设计

```sql
-- 最左前缀原则
-- 查询条件为 WHERE a=1 AND b=2 时，索引应该这样建：

INDEX idx_a_b (a, b)      -- ✅ 能用上
-- INDEX idx_b_a (b, a)   -- ❌ 用不上

-- 查询条件为 WHERE a=1 ORDER BY b 时：
INDEX idx_a_b (a, b)      -- ✅ 条件和排序都覆盖

-- 等值查询在前，范围查询在后
-- WHERE a=1 AND b>2 AND c=3
INDEX idx_a_c_b (a, c, b) -- ✅ a和c是等值，b是范围
```

### 3.3 索引示例

```sql
CREATE TABLE os_order (
    id              BIGINT UNSIGNED     AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    system_id       BIGINT UNSIGNED     NOT NULL DEFAULT 0 COMMENT '系统ID',
    software_id     BIGINT UNSIGNED     NOT NULL DEFAULT 0 COMMENT '软件ID',
    version_id      BIGINT UNSIGNED     NOT NULL DEFAULT 0 COMMENT '版本ID',
    status          TINYINT             NOT NULL DEFAULT 0 COMMENT '状态',
    submitter       VARCHAR(64)         NOT NULL DEFAULT '' COMMENT '申请人',
    submit_time     DATETIME            NULL COMMENT '提交时间',
    created_at      DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 索引
    INDEX idx_system_id (system_id),
    INDEX idx_software_id (software_id),
    INDEX idx_status (status),
    INDEX idx_submitter (submitter),
    INDEX idx_submit_time (submit_time),
    -- 复合索引：最常用的查询组合
    INDEX idx_status_created_at (status, created_at),
    INDEX idx_system_status (system_id, status)
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订购申请表';
```

---

## 4. 表设计规范

### 4.1 字符集

```sql
-- 必须使用 utf8mb4（支持emoji和特殊字符）
DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
```

### 4.2 存储引擎

```sql
-- 必须使用 InnoDB（支持事务、行锁、外键）
ENGINE=InnoDB
```

### 4.3 大表处理

```sql
-- 预估数据量大的表，提前做好分表或归档设计

-- 1. 分区表（按时间）
CREATE TABLE os_order_log (
    id          BIGINT UNSIGNED AUTO_INCREMENT,
    order_id    BIGINT UNSIGNED NOT NULL,
    action      VARCHAR(64)     NOT NULL,
    created_at  DATETIME        NOT NULL,
    PRIMARY KEY (id, created_at)
) PARTITION BY RANGE (YEAR(created_at)) (
    PARTITION p2024 VALUES LESS THAN (2025),
    PARTITION p2025 VALUES LESS THAN (2026),
    PARTITION p_future VALUES LESS THAN MAXVALUE
);

-- 2. 历史表（归档）
os_order          -- 当前数据
os_order_history  -- 历史数据（已完成的订单）
```

### 4.4 关联关系

```sql
-- 一对多：在多方存储一方ID
-- 系统(1) - 订单(N)
os_order.system_id -> os_system.id

-- 多对多：中间关联表
-- 系统(N) - 软件(N)
os_usage (system_id, software_id)
```

---

## 5. SQL 编写规范

### 5.1 基本规范

```sql
-- 关键字大写，表名列名小写
SELECT id, name FROM os_system WHERE status = 1;

-- 使用表别名
SELECT 
    o.id,
    o.status,
    s.name AS system_name,
    sw.name AS software_name
FROM os_order o
LEFT JOIN os_system s ON o.system_id = s.id
LEFT JOIN os_software sw ON o.software_id = sw.id
WHERE o.status = 1
ORDER BY o.created_at DESC;

-- 避免 SELECT *
SELECT id, name, status FROM os_system;
```

### 5.2 分页查询

```sql
-- 必须带排序
SELECT * FROM os_order 
WHERE status = 1 
ORDER BY created_at DESC 
LIMIT 20 OFFSET 0;  -- 第1页，每页20条

-- 深度分页优化（数据量大时）
-- 避免 OFFSET 100000，使用覆盖索引
SELECT * FROM os_order 
WHERE id > (SELECT id FROM os_order ORDER BY id LIMIT 100000, 1)
ORDER BY id 
LIMIT 20;
```

### 5.3 批量操作

```sql
-- 批量插入（推荐）
INSERT INTO os_order (system_id, software_id, status) VALUES
(1, 1, 0),
(1, 2, 0),
(2, 1, 0);

-- 批量更新（限制数量）
-- 一次不超过 1000 条
UPDATE os_order 
SET status = 1 
WHERE id IN (1, 2, 3, ...);  -- 最多1000个

-- 大批量更新分批处理
-- 程序中循环，每批1000条
```

### 5.4 避免的操作

```sql
-- ❌ 隐式转换（索引失效）
WHERE system_id = '123'  -- system_id 是 BIGINT

-- ✅ 显式类型
WHERE system_id = 123

-- ❌ 在索引字段上使用函数
WHERE DATE(created_at) = '2025-04-09'

-- ✅ 范围查询
WHERE created_at >= '2025-04-09 00:00:00' 
  AND created_at < '2025-04-10 00:00:00'

-- ❌ 模糊查询前缀通配
WHERE name LIKE '%Redis%'

-- ✅ 后缀通配（能用索引）
WHERE name LIKE 'Redis%'

-- ❌ NOT IN 大量数据
WHERE id NOT IN (SELECT order_id FROM ...)

-- ✅ LEFT JOIN / NOT EXISTS
LEFT JOIN ... ON ...
WHERE ... IS NULL
```

---

## 6. 数据库变更管理

### 6.1 Migration 工具

使用 Flyway 或 Liquibase 管理数据库变更：

```
db/
├── migration/
│   ├── V1__Initial_schema.sql
│   ├── V2__Add_order_table.sql
│   ├── V3__Add_software_index.sql
│   └── ...
└── seed/
    └── R__Seed_data.sql
```

### 6.2 Migration 脚本规范

```sql
-- V2__Add_order_table.sql
-- 每个脚本一个变更，便于回滚

-- 创建表
CREATE TABLE os_order (
    id              BIGINT UNSIGNED     AUTO_INCREMENT PRIMARY KEY,
    system_id       BIGINT UNSIGNED     NOT NULL DEFAULT 0,
    software_id     BIGINT UNSIGNED     NOT NULL DEFAULT 0,
    status          TINYINT             NOT NULL DEFAULT 0,
    created_at      DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_system_id (system_id),
    INDEX idx_status (status)
) ENGINE=InnoDB COMMENT='订购申请表';

-- 回滚脚本（V2__Add_order_table_rollback.sql）
-- DROP TABLE IF EXISTS os_order;
```

### 6.3 变更原则

```
✅ 可以做的：
- 添加新表
- 添加新字段（有默认值或可为空）
- 添加索引
- 创建视图

❌ 避免做的：
- 删除字段（先标记废弃，下个版本删）
- 修改字段类型
- 重命名表/字段
- 删除数据

⚠️ 需要计划的：
- 大表添加字段（Online DDL）
- 大表添加索引（低峰期执行）
- 分表/分区
```

---

## 7. ER 图

```
┌─────────────────────────────────────────────────────────────────┐
│                            Domain                               │
│  ┌────────┐  id (PK)                                            │
│  │   id   │◄─────┐                                              │
│  │  name  │      │                                              │
│  └────────┘      │                                              │
│                  │                                              │
│                  │  1:N                                          │
│                  ▼                                              │
│  ┌──────────────────────────────────┐                           │
│  │             System               │                           │
│  │  ┌────────┐  id (PK)             │                           │
│  │  │   id   │◄──────┐              │                           │
│  │  │  name  │       │              │                           │
│  │  │domain_id (FK)  │              │                           │
│  │  │ owners │       │              │                           │
│  │  └────────┘       │              │                           │
│  │                   │              │                           │
│  │                   │  1:N         │                           │
│  │                   ▼              │                           │
│  │  ┌──────────────────────────┐   │                           │
│  │  │       Application        │   │                           │
│  │  │  id (PK)                 │   │                           │
│  │  │  system_id (FK)          │   │                           │
│  │  │  name                    │   │                           │
│  │  └──────────────────────────┘   │                           │
│  └──────────────────────────────────┘                           │
│                                                                 │
│  ┌──────────────────┐         ┌──────────────────┐             │
│  │    Software      │         │     Version      │             │
│  │  ┌────────┐      │  1:N    │  ┌────────┐     │             │
│  │  │   id   │◄───────────────│──│software_id    │             │
│  │  │  name  │      │         │  │version │     │             │
│  │  │category│      │         │  └────────┘     │             │
│  │  │license │      │         └──────────────────┘             │
│  │  └────────┘      │                                          │
│  └──────────────────┘                                           │
│         ▲                                                       │
│         │ N:M                                                   │
│         │                                                       │
│  ┌──────────────────────────────────────────────────┐          │
│  │                     Usage                         │          │
│  │  id (PK)                                          │          │
│  │  system_id (FK) ──────────────────────────────────┘          │
│  │  software_id (FK)                                            │
│  │  version_id (FK)                                             │
│  │  source_type (ENUM: MANUAL_REGISTER, APPROVAL_AUTO)          │
│  └──────────────────────────────────────────────────┘          │
│                                                                 │
│  ┌──────────────────────────────────────────────────┐          │
│  │                    Order                          │          │
│  │  id (PK)                                          │          │
│  │  system_id (FK)                                   │          │
│  │  software_id (FK)                                 │          │
│  │  version_id (FK)                                  │          │
│  │  status (ENUM: DRAFT/SUBMITTED/IN_REVIEW/APPROVED/REJECTED)│
│  └──────────────────────────────────────────────────┘          │
└─────────────────────────────────────────────────────────────────┘
```

---

## 8. 检查清单

### 建表检查

```
□ 表名符合规范（os_<模块>_<表名>）
□ 必备字段齐全（id, created_at, updated_at, created_by, updated_by）
□ 字段类型选择合理
□ 有合适的索引
□ 字符集 utf8mb4
□ 引擎 InnoDB
□ 有表注释
□ 所有字段有注释
```

### SQL 检查

```
□ 不使用 SELECT *
□ 查询条件有索引
□ 分页有 ORDER BY
□ 批量操作限制数量
□ 避免在索引字段上使用函数
□ 避免隐式类型转换
□ 大事务拆分为小事务
```

### 变更检查

```
□ 有对应的 Migration 脚本
□ 脚本可重复执行（幂等）
□ 有回滚脚本
□ 大表变更计划好执行时间
□ 更新 ER 图
□ 更新数据字典
```
