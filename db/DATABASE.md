# OSM 数据库配置

## 开发数据库连接信息

```
地址: 114.66.38.81
端口: 3036
用户名: root
密码: root123
数据库: osm
```

## Flyway 管理

使用 Flyway 自动管理数据库版本，无需手动执行 SQL。

### 脚本目录

```
osm-backend/src/main/resources/db/migration/
├── V1__init_schema.sql    -- 表结构
├── V2__init_data.sql      -- 初始数据
└── V3__xxx.sql            -- 后续变更
```

### 后端配置

`application-dev.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://114.66.38.81:3036/osm?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false
    username: root
    password: root123
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
```

### 新增变更流程

1. 创建新脚本 `V{N}__{description}.sql`
2. 放在 `db/migration/` 目录
3. 启动应用时 Flyway 自动执行

## 注意事项

- 脚本命名必须遵循 `V{版本号}__{描述}.sql` 格式
- 已执行的脚本请勿修改，有问题新建脚本修复
- 开发环境使用 `114.66.38.81:3036`，生产环境使用独立配置
