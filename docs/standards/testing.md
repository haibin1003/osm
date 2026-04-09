# 测试规范

> 单元测试、集成测试规范与要求

---

## 1. 测试原则

### 1.1 TDD 开发流程

```
1. 先写测试（RED）
   - 定义接口和预期行为
   - 运行测试，确认失败

2. 实现功能（GREEN）
   - 写最小代码让测试通过
   - 不追求完美，先跑通

3. 重构优化（IMPROVE）
   - 代码优化
   - 保持测试通过
```

### 1.2 测试金字塔

```
         /\
        /  \
       / E2E \        (少量，核心流程)
      /--------\
     /          \
    / Integration \   (中等，关键集成)
   /----------------\
  /                  \
 /    Unit Tests      \  (大量，核心逻辑)
/______________________\
```

| 类型 | 占比 | 速度 | 作用 |
|------|------|------|------|
| 单元测试 | 70% | 快（毫秒） | 验证业务逻辑 |
| 集成测试 | 20% | 中（秒） | 验证组件协作 |
| E2E 测试 | 10% | 慢（分钟） | 验证用户场景 |

---

## 2. 单元测试规范

### 2.1 后端单元测试（JUnit 5 + Mockito）

#### 测试类结构

```java
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private SystemService systemService;

    @InjectMocks
    private OrderServiceImpl orderService;

    // 测试方法...
}
```

#### 测试方法命名

```java
// 格式: should<ExpectedBehavior>When<Condition>
@Test
void shouldCreateOrderSuccessfullyWhenValidRequest() { }

@Test
void shouldThrowExceptionWhenSystemNotFound() { }

@Test
void shouldRejectOrderWhenInsufficientPermission() { }

// 或 Given_When_Then
@Test
void givenValidRequest_whenCreateOrder_thenReturnOrderId() { }
```

#### 完整测试示例

```java
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private SystemService systemService;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    @DisplayName("成功提交订购申请")
    void shouldCreateOrderSuccessfullyWhenValidRequest() {
        // Given - 准备数据
        CreateOrderRequest request = new CreateOrderRequest();
        request.setSystemId(1L);
        request.setSoftwareId(2L);
        request.setPurpose("用于缓存");

        System system = new System();
        system.setId(1L);
        system.setName("支付系统");
        system.setOwners(Collections.singletonList("user1"));

        when(systemService.getById(1L)).thenReturn(system);
        when(orderMapper.insert(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(100L);
            return 1;
        });

        // When - 执行操作
        Long orderId = orderService.submitOrder(request);

        // Then - 验证结果
        assertNotNull(orderId);
        assertEquals(100L, orderId);

        // 验证交互
        verify(systemService).getById(1L);
        verify(orderMapper).insert(argThat(order ->
            order.getSystemId().equals(1L) &&
            order.getSoftwareId().equals(2L) &&
            order.getStatus() == OrderStatus.DRAFT
        ));
    }

    @Test
    @DisplayName("系统不存在时抛出异常")
    void shouldThrowExceptionWhenSystemNotFound() {
        // Given
        CreateOrderRequest request = new CreateOrderRequest();
        request.setSystemId(999L);

        when(systemService.getById(999L)).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> orderService.submitOrder(request)
        );

        assertEquals("系统不存在", exception.getMessage());
        verify(orderMapper, never()).insert(any());
    }

    @Test
    @DisplayName("无权限时抛出异常")
    void shouldThrowExceptionWhenNoPermission() {
        // Given
        CreateOrderRequest request = new CreateOrderRequest();
        request.setSystemId(1L);

        System system = new System();
        system.setId(1L);
        system.setOwners(Collections.singletonList("otherUser"));

        when(systemService.getById(1L)).thenReturn(system);

        // 模拟当前用户
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserId).thenReturn("currentUser");

            // When & Then
            assertThrows(BusinessException.class,
                () -> orderService.submitOrder(request));
        }
    }
}
```

### 2.2 前端单元测试（Jest + React Testing Library）

#### 测试文件位置

```
Component.tsx
Component.test.tsx      # 同目录
```

#### 测试示例

```tsx
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import OrderList from './OrderList';
import * as orderService from '../services/order';

// Mock API
jest.mock('../services/order');

describe('OrderList', () => {
  const mockOrders = [
    { id: 1, systemName: '支付系统', softwareName: 'Redis', status: 'APPROVED' },
    { id: 2, systemName: '订单系统', softwareName: 'Kafka', status: 'PENDING' },
  ];

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render order list correctly', async () => {
    // Given
    (orderService.getOrderList as jest.Mock).mockResolvedValue({
      data: mockOrders,
      total: 2,
    });

    // When
    render(<OrderList />);

    // Then
    await waitFor(() => {
      expect(screen.getByText('支付系统')).toBeInTheDocument();
      expect(screen.getByText('Redis')).toBeInTheDocument();
    });
  });

  it('should call API with correct params when searching', async () => {
    // Given
    (orderService.getOrderList as jest.Mock).mockResolvedValue({
      data: mockOrders,
      total: 2,
    });

    render(<OrderList />);

    // When
    const searchInput = screen.getByPlaceholderText('搜索订单');
    await userEvent.type(searchInput, 'Redis');
    fireEvent.click(screen.getByText('搜索'));

    // Then
    await waitFor(() => {
      expect(orderService.getOrderList).toHaveBeenCalledWith(
        expect.objectContaining({ keyword: 'Redis' })
      );
    });
  });

  it('should show empty state when no data', async () => {
    // Given
    (orderService.getOrderList as jest.Mock).mockResolvedValue({
      data: [],
      total: 0,
    });

    // When
    render(<OrderList />);

    // Then
    await waitFor(() => {
      expect(screen.getByText('暂无数据')).toBeInTheDocument();
    });
  });
});
```

---

## 3. 集成测试规范

### 3.1 后端集成测试（Spring Boot Test）

```java
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OrderApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Test
    @DisplayName("完整订购流程测试")
    void completeOrderFlow() throws Exception {
        // 1. 创建订单
        CreateOrderRequest request = new CreateOrderRequest();
        request.setSystemId(1L);
        request.setSoftwareId(2L);
        request.setPurpose("测试用途");

        String response = mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").isNumber())
            .andReturn()
            .getResponse()
            .getContentAsString();

        Long orderId = objectMapper.readTree(response).path("data").asLong();

        // 2. 查询订单详情
        mockMvc.perform(get("/api/v1/orders/{id}", orderId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.purpose").value("测试用途"));

        // 3. 审批通过
        ReviewOrderRequest reviewRequest = new ReviewOrderRequest();
        reviewRequest.setApproved(true);
        reviewRequest.setComment("同意");

        mockMvc.perform(post("/api/v1/orders/{id}/review", orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewRequest)))
            .andExpect(status().isOk());

        // 4. 验证数据库状态
        Order order = orderMapper.selectById(orderId);
        assertEquals(OrderStatus.APPROVED, order.getStatus());

        // 5. 验证使用记录自动创建
        // ...
    }
}
```

### 3.2 API 契约测试

```java
@Test
@DisplayName("API 响应格式符合契约")
void apiResponseShouldMatchContract() throws Exception {
    mockMvc.perform(get("/api/v1/orders/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").exists())
        .andExpect(jsonPath("$.message").exists())
        .andExpect(jsonPath("$.data").exists())
        .andExpect(jsonPath("$.data.id").isNumber())
        .andExpect(jsonPath("$.data.systemName").isString())
        .andExpect(jsonPath("$.data.status").isString());
}
```

---

## 4. E2E 测试规范

### 4.1 关键流程覆盖

```
必须覆盖的核心流程：
1. 订购申请提交 → 审批 → 使用记录生成
2. 存量使用登记 → 查询统计
3. 开源软件录入 → 版本管理
4. 业务系统管理（域/系统/应用）
5. 三级关联图谱展示
```

### 4.2 Playwright 测试示例

```typescript
// e2e/order-flow.spec.ts
import { test, expect } from '@playwright/test';

test.describe('订购申请流程', () => {
  test.beforeEach(async ({ page }) => {
    // 登录
    await page.goto('/login');
    await page.fill('[name="username"]', 'testuser');
    await page.fill('[name="password"]', 'password');
    await page.click('button[type="submit"]');
    await page.waitForURL('/dashboard');
  });

  test('开发人员可以提交订购申请', async ({ page }) => {
    // 进入订购页面
    await page.click('text=订购申请');
    await page.click('text=新建申请');

    // 填写表单
    await page.click('[name="systemId"]');
    await page.click('text=支付系统');

    await page.click('[name="softwareId"]');
    await page.click('text=Redis');

    await page.click('[name="versionId"]');
    await page.click('text=6.0');

    await page.fill('[name="purpose"]', '用于订单缓存');

    // 提交
    await page.click('text=提交申请');

    // 验证成功提示
    await expect(page.locator('.ant-message-success')).toContainText('提交成功');

    // 验证列表中有新订单
    await expect(page.locator('text=Redis')).toBeVisible();
  });

  test('管理人员可以审批订购申请', async ({ page }) => {
    // 切换到管理员账号
    // ...

    // 进入审批页面
    await page.click('text=审批管理');

    // 找到待审批的订单
    await page.click('text=待审批');

    // 点击审批
    await page.click('text=审批', { hasText: '审批' });

    // 填写审批意见
    await page.fill('[name="comment"]', '同意使用');
    await page.click('text=通过');

    // 验证状态变为已通过
    await expect(page.locator('text=已通过')).toBeVisible();
  });
});
```

---

## 5. 测试覆盖率要求

### 5.1 覆盖率指标

```
覆盖率类型        最低要求    目标
行覆盖率          80%        85%
分支覆盖率        70%        80%
方法覆盖率        80%        90%
类覆盖率          90%        100%
```

### 5.2 必须测试的内容

```
✅ 必须测试：
- 所有 Service 层业务逻辑
- 所有 Controller 层接口
- 所有工具类（Utils）
- 所有枚举值处理
- 所有异常分支
- 权限控制逻辑

⚠️ 可选测试：
- 简单的 getter/setter
- 配置类
- 纯数据类（DTO/VO）

❌ 不需要测试：
- 框架生成的代码
- 第三方库
```

### 5.3 覆盖率检查

```bash
# 后端
mvn clean test jacoco:report

# 查看报告
target/site/jacoco/index.html

# 前端
npm run test:coverage

# 查看报告
coverage/lcov-report/index.html
```

---

## 6. 测试数据管理

### 6.1 测试数据库

```yaml
# application-test.yml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;MODE=MySQL
    driver-class-name: org.h2.Driver

  sql:
    init:
      schema-locations: classpath:db/schema.sql
      data-locations: classpath:db/test-data.sql
```

### 6.2 数据准备

```java
@Test
@Sql(scripts = "/sql/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
void testWithData() {
    // 使用预设数据测试
}

// 或编程式准备
@BeforeEach
void setUp() {
    System system = new System();
    system.setName("测试系统");
    systemMapper.insert(system);
}
```

---

## 7. 持续集成

### 7.1 CI 流程

```yaml
# .github/workflows/test.yml
name: Test

on: [push, pull_request]

jobs:
  backend-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      - run: mvn clean test
      - run: mvn jacoco:report
      - name: Check coverage
        run: |
          coverage=$(cat target/site/jacoco/index.html | grep -oP 'Total[^%]+%' | head -1)
          echo "Coverage: $coverage"

  frontend-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-node@v3
        with:
          node-version: '18'
      - run: npm ci
      - run: npm run test:coverage
```

---

## 8. 检查清单

### 单元测试检查清单

```
□ 测试方法名清晰表达意图
□ Given-When-Then 结构清晰
□ 每个测试独立，不依赖其他测试
□ Mock 外部依赖（DB、Service、API）
□ 验证结果和交互
□ 边界条件测试（空值、最大值、异常）
□ 测试失败时有清晰的错误信息
```

### 集成测试检查清单

```
□ 使用真实数据库（H2/TestContainers）
□ 测试完整业务流
□ 验证数据库状态
□ 测试事务回滚
□ 清理测试数据
```

### E2E 测试检查清单

```
□ 覆盖核心用户场景
□ 使用独立测试账号
□ 每个测试后清理状态
□ 不依赖其他测试的执行顺序
□ 有明确的断言
```
