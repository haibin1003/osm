# 前后端联调规范

> 定义前后端联调流程、 playwright-cli 验证规范、验收标准

---

## 1. 联调流程

### 1.1 联调触发条件

```
前后端各自完成单元测试后，必须进行联调验证：

后端完成标准：
- ✅ 所有接口单元测试通过
- ✅ 接口文档（Swagger）已更新
- ✅ 代码已合并到 develop 分支

前端完成标准：
- ✅ 页面组件单元测试通过
- ✅ API 服务已对接
- ✅ 代码已合并到 develop 分支
```

### 1.2 联调步骤

```
Step 1: 环境准备
- 启动后端服务（localhost:8080）
- 启动前端开发服务器（localhost:5173）
- 确认数据库连接正常

Step 2: 接口连通性检查
- 使用 Swagger UI 验证后端接口
- 使用浏览器 DevTools 检查前端 API 调用

Step 3: 功能联调
- 按用户场景逐一验证
- 使用 playwright-cli 录制验证过程

Step 4: 问题修复
- 记录所有问题到 Issue
- 前后端协商修复方案
- 修复后重新联调

Step 5: 验收通过
- 所有场景验证通过
- playwright 测试用例补充完整
- 合并到 develop 分支
```

---

## 2. Playwright-CLI 验证规范

### 2.1 必须使用 Playwright-CLI 的场景

```
❗ 以下场景必须使用 playwright-cli 进行验证：

1. 新功能开发完成后的首次联调
2. 涉及多个页面交互的复杂流程
3. 数据可视化页面（图表、图谱）
4. 表单提交后的状态变化验证
5. 权限相关的页面访问控制
6. 任何 PR 合并前的最终验证
```

### 2.2 Playwright-CLI 使用规范

#### 启动命令

```bash
# 使用会话隔离启动（推荐）
PW_SESSION="${PILOT_SESSION_ID:-osm-$(date +%s)}"
playwright-cli -s="$PW_SESSION" open http://localhost:5173

# 或直接启动
playwright-cli open http://localhost:5173
```

#### 验证脚本模板

```typescript
// e2e/integration/xxx.spec.ts
import { test, expect } from '@playwright/test';

test.describe('功能联调 - XXX模块', () => {
  test.beforeEach(async ({ page }) => {
    // 设置视口
    await page.setViewportSize({ width: 1920, height: 1080 });
    
    // 访问首页并登录
    await page.goto('http://localhost:5173');
    await page.fill('[name="username"]', 'admin');
    await page.fill('[name="password"]', 'admin123');
    await page.click('button[type="submit"]');
    await page.waitForURL('/dashboard');
  });

  test('完整场景描述', async ({ page }) => {
    // 1. 前置条件检查
    await expect(page.locator('.user-profile')).toContainText('管理员');
    
    // 2. 执行操作（每个步骤都要有截图或断言）
    await page.click('text=菜单名');
    await page.waitForSelector('.page-loaded', { timeout: 5000 });
    
    // 截图记录状态
    await page.screenshot({ path: 'screenshots/step1-list.png' });
    
    // 3. 填写表单
    await page.fill('[name="field"]', 'test-value');
    await page.click('text=提交');
    
    // 4. 验证结果
    await expect(page.locator('.ant-message-success')).toContainText('成功');
    await expect(page.locator('text=test-value')).toBeVisible();
    
    // 5. 验证后端数据（通过API或页面展示）
    const response = await page.waitForResponse('**/api/**');
    expect(response.status()).toBe(200);
    
    // 最终截图
    await page.screenshot({ path: 'screenshots/step2-result.png' });
  });
});
```

### 2.3 联调验证检查清单

```
□ 页面正常加载，无白屏、无404
□ 所有接口返回200，无500错误
□ 前端正确处理后端错误响应
□ 表单提交后正确显示成功/失败提示
□ 列表分页、筛选、排序功能正常
□ 数据增删改查后页面自动刷新
□ 权限控制正确（无权限页面显示403）
□ 浏览器控制台无报错（Console无红色error）
□ 网络面板无异常请求（Network无红色失败）
□ 响应时间在可接受范围（< 2s）
```

---

## 3. 联调验收标准

### 3.1 功能验收

| 检查项 | 通过标准 | 验证方式 |
|--------|----------|----------|
| 页面渲染 | 与UI设计稿一致，无样式错乱 | 肉眼检查+截图对比 |
| 接口连通 | 所有API正常响应，数据正确 | DevTools Network |
| 交互响应 | 点击后<500ms有反馈 | 人工操作+录屏 |
| 错误处理 | 异常场景有友好提示 | 模拟错误场景 |
| 数据一致性 | 前端展示与数据库一致 | 直接查库验证 |

### 3.2 性能验收

```
□ 首屏加载 < 3s（ Lighthouse Performance > 70）
□ 接口响应 < 500ms（P95 < 1s）
□ 列表页滚动流畅，无卡顿
□ 大数据量表格分页正常
□ 图表渲染 < 1s
```

### 3.3 兼容性验收

```
□ Chrome 最新版 ✅
□ Edge 最新版 ✅
□ 分辨率: 1920x1080, 1366x768, 1440x900
□ 移动端适配（如需要）
```

---

## 4. 问题记录与修复

### 4.1 联调问题模板

```markdown
## 联调问题 #{编号}

**模块**: 系统管理 / 订购管理 / ...
**发现时间**: 2025-04-09
**发现人**: xxx
**优先级**: P0(阻塞) / P1(严重) / P2(一般)

**问题描述**:
xxx

**复现步骤**:
1. xxx
2. xxx

**期望结果**:
xxx

**实际结果**:
xxx

**截图/录屏**:
xxx

**相关日志**:
```
后端日志: xxx
前端报错: xxx
```

**责任人**: 前端/后端 @xxx
**修复期限**: xxx
**状态**: 待修复 / 修复中 / 待验证 / 已关闭
```

### 4.2 修复验证流程

```
问题修复 → 本地自测 → 提交PR → 重新联调 → 验证通过 → 关闭Issue
```

---

## 5. 联调通过标准

```
一个功能模块联调通过必须满足：

1. ✅ 所有联调检查项通过
2. ✅ Playwright 测试用例全部通过
3. ✅ 代码审查通过
4. ✅ 测试覆盖率 ≥ 80%
5. ✅ 无 P0/P1 级别问题

才能合并到 develop 分支。
```

---

## 6. 常用命令速查

```bash
# 启动后端
mvn spring-boot:run

# 启动前端
cd osm-frontend && npm run dev

# 启动 Playwright CLI
playwright-cli open http://localhost:5173

# 运行 E2E 测试
npm run test:e2e

# 查看测试报告
npx playwright show-report
```
