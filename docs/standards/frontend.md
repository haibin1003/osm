# 前端开发规范

> React 18 + TypeScript + Ant Design 5.x 编码规范

---

## 1. 项目结构

```
osm-frontend/
├── public/                          # 静态资源
│   └── index.html
├──
├── src/
│   ├──
│   ├── assets/                      # 图片、字体等
│   │   └── images/
│   │
│   ├── components/                  # 公共组件
│   │   ├── Layout/                  # 布局组件
│   │   ├── CommonTable/             # 通用表格
│   │   ├── SearchForm/              # 搜索表单
│   │   └── Graph/                   # 关系图谱
│   │
│   ├── pages/                       # 页面组件
│   │   ├── Dashboard/               # 首页仪表板
│   │   ├── SystemManage/            # 系统管理
│   │   │   ├── DomainList.tsx
│   │   │   ├── SystemList.tsx
│   │   │   └── ApplicationList.tsx
│   │   ├── SoftwareLibrary/         # 软件库
│   │   ├── OrderManage/             # 订购管理
│   │   ├── UsageManage/             # 使用登记
│   │   └── Statistics/              # 统计分析
│   │
│   ├── hooks/                       # 自定义 Hooks
│   │   ├── useTable.ts              # 表格逻辑
│   │   └── useGraph.ts              # 图谱逻辑
│   │
│   ├── services/                    # API 服务
│   │   ├── api.ts                   # axios 实例
│   │   ├── order.ts                 # 订单接口
│   │   ├── system.ts                # 系统接口
│   │   └── software.ts              # 软件接口
│   │
│   ├── stores/                      # 状态管理 (Zustand)
│   │   ├── userStore.ts
│   │   └── appStore.ts
│   │
│   ├── types/                       # TypeScript 类型
│   │   ├── order.ts
│   │   ├── system.ts
│   │   └── common.ts
│   │
│   ├── utils/                       # 工具函数
│   │   ├── request.ts               # 请求封装
│   │   ├── format.ts                # 格式化
│   │   └── validators.ts            # 校验
│   │
│   ├── App.tsx                      # 根组件
│   ├── main.tsx                     # 入口
│   └── routes.tsx                   # 路由配置
│
├── .eslintrc.cjs
├── .prettierrc
├── tsconfig.json
└── package.json
```

---

## 2. 命名规范

### 2.1 文件命名

```
// 组件文件 - 大驼峰
DomainList.tsx
OrderDetailModal.tsx

// 工具/服务文件 - 小驼峰
useTable.ts
orderService.ts
formatDate.ts

// 样式文件 - 同名
DomainList.tsx
DomainList.css / DomainList.module.css

// 测试文件 - 同名.test.tsx
DomainList.test.tsx
```

### 2.2 组件命名

```tsx
// 大驼峰，语义化
const OrderList: React.FC = () => { ... }
const UserProfileCard: React.FC<UserProfileProps> = (props) => { ... }

// 避免无意义命名
❌ const Component: React.FC = () => { ... }
❌ const Temp: React.FC = () => { ... }
```

### 2.3 变量/函数命名

```tsx
// 小驼峰
const [orderList, setOrderList] = useState<Order[]>([]);
const [loading, setLoading] = useState(false);

const fetchOrderList = async () => { ... }
const handleSubmit = () => { ... }
const formatDate = (date: string) => { ... }

// 布尔值使用 is/has/can 前缀
const [isVisible, setIsVisible] = useState(false);
const [hasError, setHasError] = useState(false);
const [canSubmit, setCanSubmit] = useState(true);

// 事件处理使用 handle 前缀
const handleClick = () => { ... }
const handleFormSubmit = () => { ... }
const handleInputChange = (e: ChangeEvent<HTMLInputElement>) => { ... }
```

---

## 3. TypeScript 规范

### 3.1 类型定义

```tsx
// 接口命名 - 大驼峰 I 前缀（可选）
interface Order {
  id: number;
  systemId: number;
  softwareName: string;
  status: OrderStatus;
  createdAt: string;
}

interface CreateOrderRequest {
  systemId: number;
  softwareId: number;
  purpose: string;
}

// 枚举定义
enum OrderStatus {
  DRAFT = 'DRAFT',
  SUBMITTED = 'SUBMITTED',
  IN_REVIEW = 'IN_REVIEW',
  APPROVED = 'APPROVED',
  REJECTED = 'REJECTED',
}

// 组件 Props 类型
interface OrderListProps {
  systemId?: number;
  onSelect?: (order: Order) => void;
}

const OrderList: React.FC<OrderListProps> = ({ systemId, onSelect }) => {
  // ...
}
```

### 3.2 避免 any

```tsx
// ❌ 错误
const handleResponse = (data: any) => {
  console.log(data.orderId);
}

// ✅ 正确
const handleResponse = (data: ApiResponse<Order>) => {
  console.log(data.data?.id);
}

// 未知类型使用 unknown
const handleUnknown = (data: unknown) => {
  if (typeof data === 'string') {
    console.log(data.toUpperCase());
  }
}
```

---

## 4. 组件规范

### 4.1 函数组件

```tsx
// ✅ 正确 - 使用函数声明
const OrderList: React.FC<OrderListProps> = ({ systemId }) => {
  const [orders, setOrders] = useState<Order[]>([]);
  const [loading, setLoading] = useState(false);

  const fetchOrders = useCallback(async () => {
    setLoading(true);
    try {
      const res = await getOrderList({ systemId });
      setOrders(res.data || []);
    } finally {
      setLoading(false);
    }
  }, [systemId]);

  useEffect(() => {
    fetchOrders();
  }, [fetchOrders]);

  return (
    <Table
      dataSource={orders}
      loading={loading}
      columns={columns}
    />
  );
};

export default OrderList;
```

### 4.2 Props 解构

```tsx
// ✅ 正确 - 直接解构
const UserCard: React.FC<UserCardProps> = ({ name, avatar, onEdit }) => {
  return (...);
};

// ❌ 错误 - 不解构直接使用 props
const UserCard: React.FC<UserCardProps> = (props) => {
  return <div>{props.name}</div>;
};
```

### 4.3 条件渲染

```tsx
// ✅ 正确 - 使用三元表达式
return (
  <div>
    {loading ? (
      <Spin />
    ) : orders.length > 0 ? (
      <Table dataSource={orders} />
    ) : (
      <Empty description="暂无数据" />
    )}
  </div>
);

// ✅ 正确 - 提前返回
if (loading) return <Spin />;
if (orders.length === 0) return <Empty />;
return <Table dataSource={orders} />;
```

---

## 5. Hooks 规范

### 5.1 useState

```tsx
// ✅ 正确 - 使用对象合并多个相关状态
const [formState, setFormState] = useState({
  systemId: undefined as number | undefined,
  softwareId: undefined as number | undefined,
  purpose: '',
});

setFormState(prev => ({ ...prev, purpose: value }));

// ❌ 错误 - 分散的 useState
const [systemId, setSystemId] = useState<number>();
const [softwareId, setSoftwareId] = useState<number>();
const [purpose, setPurpose] = useState('');
```

### 5.2 useEffect

```tsx
// ✅ 正确 - 清晰的依赖项
useEffect(() => {
  fetchOrders();
}, [systemId, pageNum, pageSize]); // 明确的依赖

// ❌ 错误 - 缺少依赖或过度依赖
useEffect(() => {
  fetchOrders();
}, []); // eslint-disable-line react-hooks/exhaustive-deps

useEffect(() => {
  // 会无限循环
  setCount(count + 1);
}, [count]);
```

### 5.3 自定义 Hooks

```tsx
// hooks/useTable.ts
import { useState, useCallback } from 'react';

interface UseTableOptions<T> {
  fetchData: (params: any) => Promise<{ data: T[]; total: number }>;
}

export function useTable<T>(options: UseTableOptions<T>) {
  const [data, setData] = useState<T[]>([]);
  const [loading, setLoading] = useState(false);
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 20,
    total: 0,
  });

  const fetchData = useCallback(async (params = {}) => {
    setLoading(true);
    try {
      const res = await options.fetchData({
        pageNum: pagination.current,
        pageSize: pagination.pageSize,
        ...params,
      });
      setData(res.data);
      setPagination(prev => ({ ...prev, total: res.total }));
    } finally {
      setLoading(false);
    }
  }, [options, pagination.current, pagination.pageSize]);

  return {
    data,
    loading,
    pagination,
    fetchData,
  };
}

// 使用
const { data, loading, pagination, fetchData } = useTable({
  fetchData: getOrderList,
});
```

---

## 6. API 请求规范

### 6.1 请求封装

```tsx
// services/api.ts
import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api/v1',
  timeout: 30000,
});

// 请求拦截器
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// 响应拦截器
api.interceptors.response.use(
  (response) => {
    const { data } = response;
    if (data.code !== 200) {
      message.error(data.message || '请求失败');
      return Promise.reject(data);
    }
    return data.data;
  },
  (error) => {
    message.error(error.message || '网络错误');
    return Promise.reject(error);
  }
);

export default api;
```

### 6.2 API 定义

```tsx
// services/order.ts
import api from './api';
import type { Order, CreateOrderRequest, OrderQuery } from '../types/order';

export const getOrderList = (params: OrderQuery) => {
  return api.get('/orders', { params });
};

export const getOrderDetail = (id: number) => {
  return api.get(`/orders/${id}`);
};

export const createOrder = (data: CreateOrderRequest) => {
  return api.post('/orders', data);
};

export const reviewOrder = (id: number, approved: boolean, comment?: string) => {
  return api.post(`/orders/${id}/review`, { approved, comment });
};
```

---

## 7. Ant Design 使用规范

### 7.1 表单

```tsx
// ✅ 正确
const [form] = Form.useForm();

const handleSubmit = async () => {
  const values = await form.validateFields();
  await createOrder(values);
  message.success('提交成功');
  form.resetFields();
};

return (
  <Form form={form} layout="vertical" onFinish={handleSubmit}>
    <Form.Item
      name="systemId"
      label="业务系统"
      rules={[{ required: true, message: '请选择业务系统' }]}
    >
      <Select placeholder="请选择">
        {systemList.map(s => (
          <Select.Option key={s.id} value={s.id}>{s.name}</Select.Option>
        ))}
      </Select>
    </Form.Item>

    <Form.Item>
      <Button type="primary" htmlType="submit">
        提交
      </Button>
    </Form.Item>
  </Form>
);
```

### 7.2 表格

```tsx
// ✅ 正确
const columns: ColumnsType<Order> = [
  {
    title: '订单编号',
    dataIndex: 'id',
    width: 100,
  },
  {
    title: '业务系统',
    dataIndex: 'systemName',
    width: 150,
  },
  {
    title: '软件',
    dataIndex: 'softwareName',
    width: 150,
  },
  {
    title: '状态',
    dataIndex: 'status',
    width: 100,
    render: (status) => <OrderStatusTag status={status} />,
  },
  {
    title: '操作',
    key: 'action',
    width: 150,
    render: (_, record) => (
      <Space>
        <Button type="link" onClick={() => handleView(record)}>查看</Button>
        <Button type="link" onClick={() => handleEdit(record)}>编辑</Button>
      </Space>
    ),
  },
];

return (
  <Table
    rowKey="id"
    columns={columns}
    dataSource={data}
    loading={loading}
    pagination={{
      ...pagination,
      showSizeChanger: true,
      showTotal: (total) => `共 ${total} 条`,
    }}
    onChange={(page) => setPagination({ current: page.current, pageSize: page.pageSize })}
  />
);
```

---

## 8. 样式规范

### 8.1 CSS Modules

```tsx
// OrderList.module.css
.container {
  padding: 24px;
  background: #fff;
}

.header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
}

// OrderList.tsx
import styles from './OrderList.module.css';

return (
  <div className={styles.container}>
    <div className={styles.header}>...</div>
  </div>
);
```

### 8.2 内联样式（少用）

```tsx
// ✅ 正确 - 使用 className
<div className={styles.container}>

// ❌ 错误 - 内联样式
<div style={{ padding: 24, background: '#fff' }}>
```

---

## 9. 检查清单

提交前端代码前自检：

```
□ TypeScript 编译通过: npm run type-check
□ ESLint 无错误: npm run lint
□ 代码格式化: npm run format
□ 测试通过: npm run test
□ 构建成功: npm run build
□ 无 console.log 调试代码
□ 无 debugger
□ 组件 Props 有类型定义
□ API 调用有错误处理
□ 列表页面有 loading 状态
□ 表单有校验规则
```
