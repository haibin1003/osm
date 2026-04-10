# OSM 项目进展记录

> 最后更新: 2026-04-10

---

## 项目概况

**项目名称**: OSM (Open Source Management) - 开源软件治理平台
**项目地址**: https://github.com/haibin1003/osm
**开发模式**: Superpowers 结构化开发

---

## 当前阶段

**阶段**: Phase 3 - Software Library 模块（待启动）

### 分支状态

| 分支 | 状态 | 说明 |
|------|------|------|
| `main` | ✅ 稳定 | 已合并 Phase 1 + Phase 2 |
| `feature/foundation` | ✅ 已合并 | 基础框架（Common模块） |
| `feature/system-management` | ✅ 已合并 | 系统管理模块（Domain/System/Application） |

### 已完成模块

#### Phase 1 ✅ - 基础框架
- [x] Common模块（Result、GlobalExceptionHandler、BusinessException）
- [x] BaseEntity实体基类
- [x] Flyway数据库迁移脚本（V1__init_schema.sql）
- [x] MyBatis-Plus配置

#### Phase 2 ✅ - System Management (2026-04-10 合并)
- [x] Domain CRUD 模块 (`/api/domains`)
- [x] System CRUD 模块 (`/api/systems`)
- [x] Application CRUD 模块 (`/api/applications`)
- [x] 单元测试（DomainServiceTest, SystemServiceTest, ApplicationServiceTest）

---

## 开发计划

### Phase 3 🔲 (待启动) - Software Library 模块

**功能需求**:
- 软件信息管理（名称、描述、官网、SoureForge/GitHub链接）
- 软件双维度分类（技术类型 + 许可证类型）
- 版本管理（版本号、发布时间、下载地址、Release Notes）
- 依赖关系管理（可选，Phase 2）

**API endpoints**:
```
POST   /api/software
PUT    /api/software/{id}
GET    /api/software/{id}
GET    /api/software
DELETE /api/software/{id}

POST   /api/software-versions
PUT    /api/software-versions/{id}
GET    /api/software-versions/{id}
GET    /api/software-versions/software/{softwareId}
DELETE /api/software-versions/{id}

GET    /api/software/by-tech-type/{techType}
GET    /api/software/by-license/{licenseType}
```

**文件结构**:
```
src/main/java/com/osm/domain/software/
├── entity/
│   ├── Software.java
│   └── SoftwareVersion.java
├── mapper/
│   ├── SoftwareMapper.java
│   └── SoftwareVersionMapper.java
├── dto/
│   ├── CreateSoftwareRequest.java
│   ├── UpdateSoftwareRequest.java
│   ├── CreateSoftwareVersionRequest.java
│   └── UpdateSoftwareVersionRequest.java
├── vo/
│   ├── SoftwareVO.java
│   └── SoftwareVersionVO.java
├── service/
│   ├── SoftwareService.java
│   └── impl/SoftwareServiceImpl.java
└── controller/
    └── SoftwareController.java
```

### Phase 4 🔲 (待启动) - Usage Management 模块

**功能需求**:
- 使用登记（存量补录 + 新申请）
- 使用记录查询
- 关联 System 和 Application

### Phase 5 🔲 (待启动) - Order Workflow 模块

**功能需求**:
- 订购申请（草稿 → 提交 → 审批中 → 通过/驳回）
- 审批流程
- 订单历史

### Phase 6 🔲 (待启动) - Statistics 模块

**功能需求**:
- 统计面板（ECharts）
- 三级关联图谱
- 数据导出

---

## 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| 后端框架 | Spring Boot | 3.2.x |
| 语言 | Java | 17 |
| ORM | MyBatis-Plus | 3.5.5 |
| 数据库迁移 | Flyway | - |
| 数据库 | MySQL | 8.0 |
| 前端框架 | React | 18 |
| UI库 | Ant Design | 5.x |
| 构建工具 | Vite | - |
| API文档 | Knife4j/SpringDoc | - |

---

## 数据库配置

| 配置项 | 值 |
|--------|-----|
| Host | 114.66.38.81 |
| Port | 3036 |
| Database | osm |
| User | root |
| Password | root123 |

---

## 待办事项

### 紧急

| 序号 | 任务 | 依赖 |
|------|------|------|
| 1 | Phase 3: Software Library 模块开发 | 无 |
| 2 | 前端页面开发（Domain/System/Application） | Phase 2后端完成 |

### 中期

| 序号 | 任务 | 依赖 |
|------|------|------|
| 1 | Phase 4: Usage Management | Phase 3完成 |
| 2 | Phase 5: Order Workflow | Phase 4完成 |
| 3 | 前端页面开发（Software/Usage/Order） | 后端对应模块完成 |

### 长期

| 序号 | 任务 | 依赖 |
|------|------|------|
| 1 | Phase 6: Statistics | Phase 5完成 |
| 2 | 前后端联调测试 | Phase 6完成 |
| 3 | E2E测试（playwright-cli） | 所有模块完成 |

---

## 决策记录

| 日期 | 决策项 | 决策内容 | 原因 |
|------|--------|----------|------|
| 2025-04-09 | 开发方案 | MVP先行，分阶段交付 | 快速验证，降低风险 |
| 2025-04-09 | 系统结构 | 域>系统>应用 三级 | 符合企业组织架构 |
| 2025-04-09 | 软件分类 | 技术类型+许可证 双维度 | 管理需要 |
| 2025-04-09 | 使用登记开关 | 初期开放，后期强制 | 存量数据补录 |
| 2025-04-09 | 订购审批 | 多状态流程 | 业务需要 |
| 2025-04-09 | 存储功能 | 放到Phase 2 | 降低MVP复杂度 |
| 2025-04-09 | 技术栈 | Java+React+MySQL | 企业主流，团队熟悉 |
| 2025-04-09 | 企业对接 | 放到Phase 2 | 先跑通核心流程 |

---

## 相关文档

| 文档 | 路径 |
|------|------|
| **核心约束** | `CLAUDE.md` |
| **PRD v1.0** | `docs/superpowers/specs/2025-04-09-osm-design.md` |
| **进展记录** | `docs/superpowers/progress.md` |
| **开发流程** | `docs/standards/workflow.md` |
| **后端规范** | `docs/standards/backend.md` |
| **前端规范** | `docs/standards/frontend.md` |
| **测试规范** | `docs/standards/testing.md` |
| **数据库规范** | `docs/standards/database.md` |
| **UI设计规范** | `docs/design/ui-design-system.md` |

---

## 下一步行动

**立即开始**: Phase 3 - Software Library 模块开发

**步骤**:
1. 创建 `feature/software-library` 分支
2. 编写 Software 实体和 CRUD
3. 编写 SoftwareVersion 实体和 CRUD
4. 编写单元测试
5. CodeReview + 合并到main

**阻塞项**: 无
