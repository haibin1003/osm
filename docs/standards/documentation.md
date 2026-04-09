# 文档规范

> API文档、技术文档、代码注释规范

---

## 1. API 文档规范

### 1.1 使用 OpenAPI (Swagger)

```java
@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "订购管理", description = "开源软件订购申请相关接口")
public class OrderController {

    @Operation(
        summary = "提交订购申请",
        description = "开发人员提交新的开源软件订购申请"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "成功",
            content = @Content(schema = @Schema(implementation = Result.class))),
        @ApiResponse(responseCode = "400", description = "参数错误"),
        @ApiResponse(responseCode = "403", description = "无权限")
    })
    @PostMapping
    public Result<Long> createOrder(
        @RequestBody @Valid @Parameter(description = "订购申请信息", required = true)
        CreateOrderRequest request
    ) {
        // ...
    }
}
```

### 1.2 接口文档必备信息

```yaml
# 每个接口必须包含：
- 接口名称（简洁明了）
- 接口描述（业务场景）
- 请求参数（类型、必填、示例、说明）
- 响应格式（状态码、数据结构、示例）
- 错误码（业务错误码定义）
- 权限要求（角色要求）
```

### 1.3 接口变更管理

```
API 版本控制：
- /api/v1/orders      # 当前版本
- /api/v2/orders      # 新版本

变更类型：
- 新增接口：直接添加
- 修改接口：保持 v1 兼容，新增 v2
- 废弃接口：标记 @Deprecated，保留至少一个版本

变更日志：
docs/api/CHANGELOG.md
```

---

## 2. 代码注释规范

### 2.1 Java 注释

```java
/**
 * 订购服务
 * 
 * <p>处理开源软件订购申请的业务逻辑，包括：</p>
 * <ul>
 *   <li>提交订购申请</li>
 *   <li>审批流程处理</li>
 *   <li>自动生成使用记录</li>
 * </ul>
 *
 * @author haibin1003
 * @since 1.0.0
 */
@Service
public class OrderService {

    /**
     * 提交订购申请
     *
     * <p>开发人员提交新的订购申请，系统会：</p>
     * <ol>
     *   <li>校验参数合法性</li>
     *   <li>检查用户是否有权限操作目标系统</li>
     *   <li>创建订单记录，状态为"草稿"</li>
     * </ol>
     *
     * @param request 订购申请信息，包含系统ID、软件ID、使用用途等
     * @return 创建的订单ID
     * @throws BusinessException 当系统不存在或无权限时抛出
     * 
     * @example
     * <pre>
     * CreateOrderRequest request = new CreateOrderRequest();
     * request.setSystemId(1L);
     * request.setSoftwareId(2L);
     * request.setPurpose("用于订单缓存");
     * Long orderId = orderService.submitOrder(request);
     * </pre>
     */
    public Long submitOrder(CreateOrderRequest request) {
        // ...
    }

    // 简单方法可以用单行注释
    /** 获取当前登录用户ID */
    private String getCurrentUserId() {
        // ...
    }
}
```

### 2.2 TypeScript/React 注释

```tsx
/**
 * 订购申请表单组件
 * 
 * @example
 * ```tsx
 * <OrderForm 
 *   systemId={1}
 *   onSuccess={() => navigate('/orders')}
 * />
 * ```
 */
interface OrderFormProps {
  /** 预选的系统ID */
  systemId?: number;
  /** 提交成功回调 */
  onSuccess?: () => void;
  /** 取消回调 */
  onCancel?: () => void;
}

/**
 * 订购申请表单
 * 
 * 用于开发人员提交新的开源软件订购申请
 */
export const OrderForm: React.FC<OrderFormProps> = ({ 
  systemId, 
  onSuccess, 
  onCancel 
}) => {
  // ...
}
```

### 2.3 注释原则

```
✅ 应该注释：
- 类/接口的职责说明
- 复杂业务逻辑的解释
- 非直观的代码（为什么这么做）
- 公共 API 的参数、返回值、异常
- 魔法数字的含义
- FIXME/TODO 标记

❌ 不需要注释：
- 自解释的代码（getOrderById）
- 简单的 getter/setter
- 与代码重复的注释
- 过期的注释（必须同步更新）
```

---

## 3. 技术文档规范

### 3.1 文档结构

```
docs/
├── README.md                    # 文档总览
├── development/
│   ├── setup.md                 # 开发环境搭建
│   ├── workflow.md              # 开发流程
│   └── deployment.md            # 部署文档
├── architecture/
│   ├── overview.md              # 架构总览
│   ├── database.md              # 数据库设计
│   └── api/                     # API文档
│       ├── authentication.md
│       └── errors.md
├── design/                      # 设计文档
│   └── 2025-04-09-order-flow.md
└── standards/                   # 规范文档
    ├── workflow.md
    ├── backend.md
    ├── frontend.md
    ├── testing.md
    └── documentation.md
```

### 3.2 设计文档模板

```markdown
# 功能名称设计文档

## 背景
为什么要做这个功能？解决什么问题？

## 目标
- 核心目标1
- 核心目标2

## 方案对比

| 方案 | 优点 | 缺点 | 结论 |
|------|------|------|------|
| A | ... | ... | 推荐 |
| B | ... | ... | 放弃 |

## 详细设计

### 数据模型
```
ER图或表结构
```

### 接口设计
| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 创建订单 | POST | /api/v1/orders | ... |

### 流程图
```
时序图或流程图
```

### 权限控制
谁可以做什么？

## 测试计划
- [ ] 单元测试
- [ ] 集成测试
- [ ] E2E测试

## 风险与应对
| 风险 | 概率 | 影响 | 应对措施 |
|------|------|------|----------|
| ... | ... | ... | ... |

## 排期
- 设计：1天
- 开发：3天
- 测试：2天

---
日期：2025-04-09
作者：xxx
状态：已确认
```

### 3.3 文档更新要求

```
变更类型        需要更新的文档
─────────────────────────────────────────
新增接口        API文档、CHANGELOG
修改接口        API文档、CHANGELOG、通知调用方
新增表/字段     数据库设计文档、ER图
修改表结构      数据库设计文档、Migration说明
架构变更        架构总览文档
新增规范        对应规范文档
```

---

## 4. README 规范

### 4.1 项目 README

```markdown
# OSM - 开源软件治理平台

[![Build Status](https://github.com/haibin1003/osm/workflows/Test/badge.svg)](https://github.com/haibin1003/osm/actions)
[![Coverage](https://codecov.io/gh/haibin1003/osm/branch/main/graph/badge.svg)](https://codecov.io/gh/haibin1003/osm)

## 简介

OSM (Open Source Management) 是一个企业级开源软件治理平台，
用于管理企业内部使用的开源软件资产，包括软件清单、版本管理、
使用登记、订购审批等功能。

## 功能特性

- 📦 开源软件库管理（分类、版本）
- 🏢 业务系统架构管理（域/系统/应用）
- 📝 订购申请与审批流程
- 📊 使用情况登记与追溯
- 📈 数据统计与可视化

## 技术栈

- 后端：Java 17 + Spring Boot 3.x + MyBatis-Plus
- 前端：React 18 + TypeScript + Ant Design
- 数据库：MySQL 8.0

## 快速开始

### 环境要求
- JDK 17+
- Node.js 18+
- MySQL 8.0+

### 后端启动
```bash
cd osm-backend
mvn clean install
mvn spring-boot:run
```

### 前端启动
```bash
cd osm-frontend
npm install
npm run dev
```

### 访问
- 前端：http://localhost:5173
- 后端API：http://localhost:8080
- Swagger：http://localhost:8080/swagger-ui.html

## 文档

- [开发规范](docs/standards/)
- [API文档](http://localhost:8080/swagger-ui.html)
- [架构设计](docs/architecture/)

## 贡献指南

1. Fork 本仓库
2. 创建 feature 分支 (`git checkout -b feature/amazing-feature`)
3. 提交更改 (`git commit -m 'feat: add amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 创建 Pull Request

## License

[MIT](LICENSE)
```

---

## 5. 变更日志 (CHANGELOG)

```markdown
# Changelog

所有重要的变更都会记录在这个文件中。

格式基于 [Keep a Changelog](https://keepachangelog.com/en/1.0.0/)，
版本号遵循 [Semantic Versioning](https://semver.org/lang/zh-CN/)。

## [Unreleased]

### Added
- 新增订购申请功能
- 新增审批流程

### Changed
- 优化系统列表查询性能

### Fixed
- 修复登录过期无提示的问题

## [1.0.0] - 2025-04-09

### Added
- 开源软件库管理
- 业务系统管理
- 使用登记功能
- 数据统计面板

### Security
- 实现JWT认证
```

---

## 6. 检查清单

### 新增接口检查

```
□ Swagger注解完整
□ 参数校验注解
□ 错误码文档更新
□ CHANGELOG记录
□ Postman集合更新（如有）
```

### 代码提交检查

```
□ 复杂逻辑有注释
□ 公共方法有JavaDoc
□ 没有与代码重复的注释
□ 没有过期注释
```

### 技术文档检查

```
□ 文档结构清晰
□ 图表可正常显示
□ 示例代码可运行
□ 链接有效
□ 版本号正确
```
