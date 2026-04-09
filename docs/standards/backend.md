# 后端开发规范

> Java 17 + Spring Boot 3.x + MyBatis-Plus 编码规范

---

## 1. 项目结构

```
osm-backend/
├── src/
│   ├── main/
│   │   ├── java/com/osm/
│   │   │   ├── OsmApplication.java          # 启动类
│   │   │   ├──
│   │   │   ├── common/                       # 通用组件
│   │   │   │   ├── config/                   # 配置类
│   │   │   │   ├── exception/                # 异常处理
│   │   │   │   ├── result/                   # 统一响应
│   │   │   │   ├── util/                     # 工具类
│   │   │   │   └── constant/                 # 常量定义
│   │   │   ├──
│   │   │   ├── domain/                       # 领域层
│   │   │   │   ├── order/                    # 订购模块
│   │   │   │   │   ├── entity/               # 实体
│   │   │   │   │   ├── mapper/               # 数据访问
│   │   │   │   │   ├── service/              # 业务逻辑
│   │   │   │   │   ├── dto/                  # 数据传输对象
│   │   │   │   │   ├── vo/                   # 视图对象
│   │   │   │   │   └── enums/                # 枚举
│   │   │   │   ├── system/                   # 系统管理模块
│   │   │   │   ├── software/                 # 软件库模块
│   │   │   │   └── usage/                    # 使用登记模块
│   │   │   ├──
│   │   │   └── interfaces/                   # 接口层
│   │   │       ├── controller/               # REST API
│   │   │       └── aspect/                   # 切面
│   │   │
│   │   └── resources/
│   │       ├── application.yml               # 主配置
│   │       ├── application-dev.yml           # 开发环境
│   │       ├── application-prod.yml          # 生产环境
│   │       └── mapper/                       # XML映射文件
│   │
│   └── test/                                 # 测试代码
│       └── java/com/osm/
│
└── pom.xml
```

---

## 2. 命名规范

### 2.1 包名

```java
// 全部小写，点分隔
com.osm.domain.order.service
com.osm.interfaces.controller
```

### 2.2 类名

```java
// 大驼峰，名词
OrderService
OrderController
OrderMapper
OrderDTO
OrderVO
OrderStatusEnum
OrderException
```

### 2.3 方法名

```java
// 小驼峰，动词开头
createOrder()
getOrderById()
updateOrderStatus()
deleteOrder()
listOrdersBySystemId()
```

### 2.4 变量名

```java
// 小驼峰
Long orderId;
String orderNo;
List<Order> orderList;
```

### 2.5 常量名

```java
// 全大写，下划线分隔
public static final int DEFAULT_PAGE_SIZE = 20;
public static final String ORDER_STATUS_PENDING = "PENDING";
```

### 2.6 数据库命名

```sql
-- 表名：小写，下划线分隔，复数
os_order
os_system
os_software

-- 字段名：小写，下划线分隔
id
created_at
updated_at
system_id
software_name

-- 索引名：idx_表名_字段名
idx_order_system_id
idx_order_status
```

---

## 3. 代码规范

### 3.1 实体类 (Entity)

```java
@Data
@TableName("os_order")
public class Order {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long systemId;

    private Long softwareId;

    private Long versionId;

    private String purpose;

    private LocalDate expectedDate;

    private OrderStatus status;

    private String submitter;

    private LocalDateTime submitTime;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // 业务方法，不是只有 getter/setter
    public void submit() {
        this.status = OrderStatus.SUBMITTED;
        this.submitTime = LocalDateTime.now();
    }

    public void approve(String reviewer) {
        if (this.status != OrderStatus.IN_REVIEW) {
            throw new BusinessException("订单不在审批中状态");
        }
        this.status = OrderStatus.APPROVED;
        this.reviewer = reviewer;
        this.reviewTime = LocalDateTime.now();
    }
}
```

### 3.2 Mapper 接口

```java
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    // 简单查询用 MyBatis-Plus 内置方法
    // 复杂查询自定义 SQL

    @Select("""
        SELECT o.*, s.name as system_name, sw.name as software_name
        FROM os_order o
        LEFT JOIN os_system s ON o.system_id = s.id
        LEFT JOIN os_software sw ON o.software_id = sw.id
        WHERE o.system_id = #{systemId}
        ORDER BY o.created_at DESC
        """)
    List<OrderDetailVO> selectOrderDetailBySystemId(@Param("systemId") Long systemId);
}
```

### 3.3 Service 接口

```java
public interface OrderService {

    /**
     * 提交订购申请
     *
     * @param request 申请信息
     * @return 订单ID
     */
    Long submitOrder(CreateOrderRequest request);

    /**
     * 审批订单
     *
     * @param orderId 订单ID
     * @param approved 是否通过
     * @param comment 审批意见
     */
    void reviewOrder(Long orderId, boolean approved, String comment);

    /**
     * 分页查询订单列表
     *
     * @param query 查询条件
     * @return 分页结果
     */
    Page<OrderVO> listOrders(OrderQuery query);
}
```

### 3.4 Service 实现

```java
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final SystemService systemService;
    private final UsageService usageService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitOrder(CreateOrderRequest request) {
        // 参数校验
        Assert.notNull(request.getSystemId(), "系统ID不能为空");
        Assert.notNull(request.getSoftwareId(), "软件ID不能为空");

        // 业务校验
        System system = systemService.getById(request.getSystemId());
        if (system == null) {
            throw new BusinessException("系统不存在");
        }

        // 检查权限（开发人员只能操作自己负责的系统）
        if (!system.getOwners().contains(getCurrentUserId())) {
            throw new BusinessException("无权操作该系统");
        }

        // 创建订单
        Order order = new Order();
        BeanUtils.copyProperties(request, order);
        order.setStatus(OrderStatus.DRAFT);
        order.setSubmitter(getCurrentUserId());

        orderMapper.insert(order);

        log.info("Order created: id={}, systemId={}, softwareId={}",
                order.getId(), order.getSystemId(), order.getSoftwareId());

        return order.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reviewOrder(Long orderId, boolean approved, String comment) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        if (approved) {
            order.approve(getCurrentUserId());
            orderMapper.updateById(order);

            // 自动创建使用记录
            usageService.createFromOrder(order);
        } else {
            order.reject(comment);
            orderMapper.updateById(order);
        }
    }
}
```

### 3.5 Controller

```java
@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Validated
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public Result<Long> createOrder(@RequestBody @Valid CreateOrderRequest request) {
        Long orderId = orderService.submitOrder(request);
        return Result.success(orderId);
    }

    @PostMapping("/{orderId}/review")
    public Result<Void> reviewOrder(
            @PathVariable Long orderId,
            @RequestBody @Valid ReviewOrderRequest request) {
        orderService.reviewOrder(orderId, request.isApproved(), request.getComment());
        return Result.success();
    }

    @GetMapping
    public Result<Page<OrderVO>> listOrders(OrderQuery query) {
        Page<OrderVO> page = orderService.listOrders(query);
        return Result.success(page);
    }
}
```

### 3.6 统一响应

```java
@Data
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMessage(message);
        return result;
    }

    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}
```

### 3.7 异常处理

```java
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("Business exception: {}", e.getMessage());
        return Result.error(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return Result.error(400, message);
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("System error", e);
        return Result.error("系统繁忙，请稍后重试");
    }
}
```

---

## 4. DTO/VO 规范

```java
// Request DTO - 入参
@Data
public class CreateOrderRequest {
    @NotNull(message = "系统ID不能为空")
    private Long systemId;

    @NotNull(message = "软件ID不能为空")
    private Long softwareId;

    @NotNull(message = "版本ID不能为空")
    private Long versionId;

    @NotBlank(message = "使用用途不能为空")
    @Size(max = 500, message = "使用用途不能超过500字")
    private String purpose;

    @Future(message = "期望上线时间必须是未来日期")
    private LocalDate expectedDate;
}

// Response VO - 出参
@Data
@Builder
public class OrderVO {
    private Long id;
    private String systemName;
    private String softwareName;
    private String version;
    private String purpose;
    private String status;
    private String statusDesc;
    private String submitter;
    private LocalDateTime submitTime;
}

// Query - 查询参数
@Data
public class OrderQuery extends PageQuery {
    private Long systemId;
    private Long softwareId;
    private OrderStatus status;
    private String keyword;
}
```

---

## 5. 日志规范

```java
@Slf4j
@Service
public class OrderService {

    public void createOrder(CreateOrderRequest request) {
        // 入口日志
        log.info("[OrderService.createOrder] start, request={}", request);

        try {
            // 业务逻辑
            Long orderId = doCreate(request);

            // 成功日志
            log.info("[OrderService.createOrder] success, orderId={}", orderId);
        } catch (BusinessException e) {
            // 业务异常日志
            log.warn("[OrderService.createOrder] business error, request={}, error={}",
                    request, e.getMessage());
            throw e;
        } catch (Exception e) {
            // 系统异常日志
            log.error("[OrderService.createOrder] system error, request={}", request, e);
            throw e;
        }
    }
}
```

---

## 6. 配置规范

```yaml
# application.yml
spring:
  application:
    name: osm-server
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}
  datasource:
    url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:osm}?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: ${DB_USER:root}
    password: ${DB_PASSWORD:}

mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0

logging:
  level:
    com.osm: DEBUG
```

---

## 7. 检查清单

提交后端代码前自检：

```
□ 编译通过: mvn clean compile
□ 测试通过: mvn test
□ 覆盖率 >= 80%: mvn jacoco:report
□ 无 SonarLint 严重问题
□ 无 System.out.println
□ 无硬编码（URL、密码、魔法数字）
□ 所有接口都有参数校验 @Valid
□ 所有异常都有日志记录
□ API 返回统一 Result 格式
□ 数据库字段有索引（列表查询）
□ SQL 使用参数化查询
```
