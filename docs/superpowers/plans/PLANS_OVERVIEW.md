# OSM 项目实施计划概览

## 已完成计划

1. **2025-04-09-01-foundation.md** - 基础架构搭建 ✅
2. **2025-04-09-02-system-management.md** - 系统管理模块 ✅
3. **2025-04-09-03-software-library.md** - 开源软件库模块 ✅

## 待完成计划

### 4. 使用登记模块 (2025-04-09-04-usage-management.md)

**范围:**
- 使用记录(Usage)实体和CRUD
- 存量补录功能（历史登记）
- 关联系统、软件、版本
- 来源类型区分（历史补录 vs 审批自动）
- 开发人员视角的使用记录管理

**关键API:**
- POST /v1/usages - 登记使用记录
- GET /v1/usages - 查询使用记录（分页）
- GET /v1/usages/by-system/{systemId} - 查询系统使用记录
- GET /v1/usages/by-software/{softwareId} - 查询软件使用记录

**前端页面:**
- UsageList.tsx - 使用记录列表
- UsageForm.tsx - 登记使用记录表单
- UsageDetail.tsx - 使用记录详情

### 5. 订购与审批模块 (2025-04-09-05-order-workflow.md)

**范围:**
- 订购申请(Order)工作流
- 状态流转：草稿 → 已提交 → 审批中 → 已通过/已驳回
- 审批通过后自动创建使用记录
- 草稿保存和重新申请
- 管理人员审批界面

**关键API:**
- POST /v1/orders - 创建订购申请（草稿）
- POST /v1/orders/{id}/submit - 提交申请
- POST /v1/orders/{id}/review - 审批（通过/驳回）
- GET /v1/orders - 查询申请列表
- GET /v1/orders/my - 查询我的申请

**前端页面:**
- OrderList.tsx - 订购申请列表
- OrderForm.tsx - 订购申请表单（3步：选系统 → 选软件 → 填写信息）
- OrderDetail.tsx - 申请详情
- OrderReview.tsx - 审批界面（管理员）

### 6. 数据统计与可视化 (2025-04-09-06-statistics-visualization.md)

**范围:**
- 管理员首页数据面板
- 统计卡片（软件总数、系统数量、待审批、处理率）
- 图表组件（饼图、折线图、仪表盘）
- 三级关联图谱（域 → 系统 → 软件）
- 支持下钻查看详情

**关键API:**
- GET /v1/statistics/dashboard - 仪表盘数据
- GET /v1/statistics/distribution - 分类分布统计
- GET /v1/statistics/trends - 趋势统计
- GET /v1/graph/relations - 关系图谱数据

**前端页面:**
- Dashboard.tsx - 管理员首页（数据面板）
- RelationGraph.tsx - 关联图谱（全屏可视化）
- 图表组件：PieChart, LineChart, GaugeChart, StatCard

---

## 实施建议

**开发顺序：**
1. 先完成 4-使用登记（基础数据记录）
2. 再完成 5-订购工作流（依赖使用登记）
3. 最后完成 6-统计可视化（依赖前面所有模块的数据）

**每个计划包含：**
- 详细的文件结构
- 实体、Mapper、Service、Controller
- DTO、VO、Enums
- 前端页面和组件
- 测试用例
- 具体的Git提交命令

**预计开发时间：**
- 使用登记：2-3天
- 订购工作流：3-4天
- 统计可视化：3-4天
- 总计：8-11天
