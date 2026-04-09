# OSM 项目 Claude Code 开发规范

> **⚠️ 强制性规范** - 所有 AI 和开发人员必须严格遵守
> 
> 违反以下红线，代码将被拒绝合并。

---

## 项目概述

| 项目 | 内容 |
|------|------|
| **项目名称** | OSM (Open Source Management) - 开源软件治理平台 |
| **后端** | Java 17 + Spring Boot 3.x + MyBatis-Plus |
| **前端** | React 18 + TypeScript + Ant Design 5.x |
| **数据库** | MySQL 8.0 |
| **开发模式** | Superpowers 结构化开发 |

---

## 核心原则

1. **先设计后编码** - 严禁无设计文档直接写代码
2. **TDD 开发** - 先写测试，再写实现，测试覆盖率 >= 80%
3. **小步提交** - 每次 commit 聚焦一个变更，频繁提交
4. **代码审查** - 所有代码必须通过 PR，至少 1 人 Review
5. **Superpowers 流程** - 复杂功能必须使用 brainstorming → writing-plans → subagent-driven-development

---

## 红线（绝对禁止）

### 代码编写红线

```
❌ 严禁在 main 分支直接写代码，必须先创建 feature/* 分支
❌ 严禁在没有文档的情况下写代码，必须先编写设计、测试计划
❌ 严禁不写测试直接提交生产代码
❌ 严禁硬编码（URL、密码、魔法数字、SQL 拼接）
❌ 严禁无参数校验直接入库
❌ 严禁大表查询不分页
❌ 严禁提交调试代码（System.out.println）和敏感信息
❌ 严禁破坏 API 向后兼容（不通知的 URL/参数/返回值变更）
```

### 架构红线

```
❌ Controller 直接调用 Mapper（跳过 Service）
❌ Service 之间循环依赖
❌ 跨服务直接操作数据库
❌ 领域层引入技术细节（HTTP、DB 等）
❌ 贫血模型（Service 全是过程式代码，Entity 只有 getter/setter）
```

### 代码质量红线

```
❌ 编译警告不修复
❌ 测试覆盖率低于 80%
❌ SonarLint 严重问题不修复
❌ 重复代码不抽象
```

### Git 红线

```
❌ 严禁在 main 分支直接提交代码，所有变更必须在新分支
❌ 混合多个无关变更到一个 commit
❌ commit message 含糊不清（如 "update", "fix bug"）
❌ 提交无法通过 CI 的代码
```

---

## 代码编写要求

### 分支策略

```
main:      生产分支，只能合并，不能提交

develop:   开发分支，集成测试通过的功能

feature/*: 功能分支，从 develop 切出
  示例: feature/order-approval

hotfix/*:  紧急修复，从 main 切出
  示例: hotfix/login-nullpointer
```

### 提交流程

```
feature/login → develop → main
     ↑              ↑
   开发测试      PR Review
```

### 提交信息规范

```
<type>(<scope>): <subject>

# Type
- feat: 新功能
- fix: Bug修复
- docs: 文档
- style: 格式（不影响代码运行）
- refactor: 重构
- test: 测试
- chore: 构建/工具/配置

# Scope（模块）
- order: 订购模块
- system: 系统管理
- software: 软件库
- usage: 使用登记
- admin: 管理后台

# 示例
feat(order): 添加订购申请提交接口
fix(user): 修复用户登录空指针异常
docs(api): 更新接口文档
refactor(system): 优化系统查询性能
```

---

## 开发流程

### 标准开发流程（Superpowers）

```
需求澄清 → 编写设计文档 → 编写测试计划 → 
创建 feature 分支 → TDD 开发 → 自测 → 
提交 PR → Code Review → 合并 → 部署
```

### 复杂功能开发流程

```
1. 需求澄清
   └─ 使用 superpowers:brainstorming 明确需求

2. 编写设计文档
   └─ 输出到 docs/design/YYYY-MM-DD-<feature>.md

3. 编写实施计划
   └─ 使用 superpowers:writing-plans
   └─ 输出到 docs/superpowers/plans/YYYY-MM-DD-<feature>.md

4. 开发实施
   └─ 使用 superpowers:subagent-driven-development
   └─ 或 superpowers:executing-plans

5. 代码审查
   └─ 使用 superpowers:requesting-code-review

6. 完成分支
   └─ 使用 superpowers:finishing-a-development-branch
```

### 简单功能开发流程

```
（无需 design doc 和 plan doc）

1. 创建 feature 分支
2. 先写测试（TDD）
3. 实现功能
4. 自测通过
5. 提交 PR
6. Code Review
7. 合并
```

---

## 调试与日志

### 日志规范

```java
// ✅ 正确 - 使用 Slf4j
@Slf4j
@Service
public class OrderService {
    public void createOrder(CreateOrderRequest request) {
        log.info("Creating order, systemId={}, softwareId={}", 
                 request.getSystemId(), request.getSoftwareId());
        
        try {
            // ...
        } catch (Exception e) {
            log.error("Failed to create order, request={}", request, e);
            throw new BusinessException("创建订单失败", e);
        }
    }
}

// ❌ 错误 - 使用 System.out.println
System.out.println("Creating order: " + request);
```

### 常用命令

```bash
# 后端开发
# ==========
# 编译
mvn clean compile

# 运行测试
mvn test

# 测试覆盖率报告
mvn jacoco:report

# 打包
mvn clean package -DskipTests

# 运行
java -jar target/osm-server.jar

# 前端开发
# ==========
# 安装依赖
cd frontend && npm install

# 启动开发服务器
npm run dev

# 构建
npm run build

# 运行测试
npm run test

# 数据库
# ==========
# 连接 MySQL
mysql -h localhost -u root -p osm

# 查看表结构
DESCRIBE os_order;

# 导出表结构
mysqldump -d osm > schema.sql
```

---

## 相关文档

| 文档 | 路径 | 内容 |
|------|------|------|
| **开发流程规范** | [docs/standards/workflow.md](docs/standards/workflow.md) | 分支策略、PR流程、发布流程 |
| **后端开发规范** | [docs/standards/backend.md](docs/standards/backend.md) | Java/Spring Boot 编码规范 |
| **前端开发规范** | [docs/standards/frontend.md](docs/standards/frontend.md) | React/TypeScript/Ant Design 规范 |
| **测试规范** | [docs/standards/testing.md](docs/standards/testing.md) | 单元测试、集成测试、覆盖率要求 |
| **文档规范** | [docs/standards/documentation.md](docs/standards/documentation.md) | API文档、技术文档规范 |
| **数据库规范** | [docs/standards/database.md](docs/standards/database.md) | 命名规范、索引规范、Migration |

---

## 快速检查清单

提交代码前自检：

```
□ 我在 feature/* 分支上，不是 main
□ 所有测试都通过了（mvn test）
□ 测试覆盖率 >= 80%
□ 没有编译警告
□ 没有 System.out.println 调试代码
□ 没有硬编码敏感信息
□ commit message 符合规范
□ 相关文档已同步更新
```

---

## 确认签名

参与本项目的开发人员需在此确认已阅读并理解上述约束：

- [ ] @haibin1003 - 2025-04-09
