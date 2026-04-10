# 功能完整性开发规范

> **⚠️ 强制性规范** - 功能必须完整做完才能合并到 main
>
> 每一项都是强制要求，不满足则不允许合并。

---

## 1. 功能完成的定义

**一个功能模块完成的标准：**

| 类别 | 要求 | 状态 |
|------|------|------|
| 后端 API | 所有接口开发完成 | ⬜ |
| 后端测试 | 单元测试覆盖 >= 80% | ⬜ |
| 前端页面 | 完整 UI 页面开发完成 | ⬜ |
| 前端交互 | 用户交互流程可正常使用 | ⬜ |
| 前后端联调 | API 调用验证通过 | ⬜ |
| 浏览器测试 | 页面无 JS 报错 | ⬜ |
| E2E 测试 | Playwright E2E 测试通过 | ⬜ |

---

## 2. 分支管理规范

### 2.1 分支创建原则

```
一个功能模块 = 一个独立分支

✅ 正确做法：
- feature/user-auth          # 包含后端+前端+联调
- feature/system-management   # 包含域+系统+应用的后端+前端

❌ 错误做法：
- feature/user-auth-backend  # 把一个功能拆成多个分支
- feature/user-auth-frontend
```

### 2.2 分支生命周期

```
1. 创建分支 (从 main)
   git checkout main
   git checkout -b feature/xxx

2. 开发
   - 后端开发
   - 前端开发
   - 联调测试

3. 功能验证通过后
   git push origin feature/xxx
   创建 PR → main

4. PR 合并后
   git checkout main
   git pull origin main
   git branch -d feature/xxx                    # 删除本地
   git push origin --delete feature/xxx         # 删除远程
```

---

## 3. 开发顺序规范

### 3.1 正确的开发顺序

```
Phase X 功能开发：

Step 1: 后端 API 开发
├── 1.1 Entity/Mapper
├── 1.2 Service + 单元测试
├── 1.3 Controller
└── 1.4 后端测试通过 (mvn test)

Step 2: 前端页面开发
├── 2.1 API 接口封装
├── 2.2 页面组件开发
└── 2.3 页面交互实现

Step 3: 前后端联调
├── 3.1 启动后端服务
├── 3.2 启动前端服务
├── 3.3 API 调用验证
└── 3.4 浏览器交互验证

Step 4: E2E 测试
└── 4.1 Playwright E2E 测试通过

Step 5: 提交合并
└── 5.1 CodeReview + 合并到 main
```

### 3.2 禁止的行为

```
❌ 禁止后端写完就合并，必须等前端也完成
❌ 禁止前端写完就合并，必须等联调通过
❌ 禁止只做后端或只做前端
❌ 禁止跳过联调直接合并
❌ 禁止合并后不删除分支
```

---

## 4. 前后端联调规范

### 4.1 联调检查清单

**启动服务：**
```bash
# 终端1: 启动后端
cd osm-backend
mvn spring-boot:run
# 或 java -jar target/osm-server.jar

# 终端2: 启动前端
cd osm-frontend
npm run dev
```

**联调验证项：**
```
□ 登录流程正常（获取token，存储到localStorage）
□ 所有API请求携带token
□ 页面加载数据正常
□ 表单提交成功
□ 删除操作正常
□ 分页正常
□ 无跨域错误
□ 浏览器Console无Error级别报错
```

### 4.2 前端开发规范

**API 调用规范：**
```typescript
// ✅ 正确：封装 API 调用
import api from '@/services/api'

export const login = (data: LoginForm) => api.post('/auth/login', data)
export const getUsers = () => api.get('/users')
export const createUser = (data: UserForm) => api.post('/users', data)

// ❌ 错误：直接在组件中使用 axios
import axios from 'axios'
axios.post('/api/auth/login', data)
```

**数据流规范：**
```typescript
// ✅ 正确：使用 React hooks + TypeScript
const [users, setUsers] = useState<UserVO[]>([])
const [loading, setLoading] = useState(false)

useEffect(() => {
  loadUsers()
}, [])

const loadUsers = async () => {
  setLoading(true)
  try {
    const data = await getUsers()
    setUsers(data)
  } finally {
    setLoading(false)
  }
}

// ❌ 错误：any 类型滥用
const [data, setData] = useState<any>([])
```

---

## 5. E2E 测试规范

### 5.1 E2E 测试范围

每个功能模块必须包含 E2E 测试：

```typescript
// e2e/auth.spec.ts
test.describe('登录功能', () => {
  test('登录成功', async ({ page }) => {
    await page.goto('/login')
    await page.fill('[name="username"]', 'admin')
    await page.fill('[name="password"]', 'admin123')
    await page.click('button[type="submit"]')
    await expect(page).toHaveURL('/dashboard')
  })

  test('登录失败', async ({ page }) => {
    await page.goto('/login')
    await page.fill('[name="username"]', 'admin')
    await page.fill('[name="password"]', 'wrong')
    await page.click('button[type="submit"]')
    await expect(page.locator('.ant-message')).toContainText('用户名或密码错误')
  })
})
```

### 5.2 E2E 测试执行

```bash
# 启动服务
npm run dev &
mvn spring-boot:run &

# 运行 E2E 测试
npx playwright test

# 查看报告
npx playwright show-report
```

---

## 6. PR 合并检查清单

**PR 创建前必须全部通过：**

```
## 后端检查
□ mvn test 通过
□ mvn compile 无警告
□ 测试覆盖率 >= 80%
□ API 接口文档已更新

## 前端检查
□ npm run build 通过
□ npm run test 通过
□ TypeScript 类型检查通过

## 联调检查
□ 后端服务正常启动
□ 前端服务正常启动
□ 所有 API 调用正常
□ 页面交互正常
□ 浏览器 Console 无 Error

## E2E 检查
□ Playwright E2E 测试全部通过
□ 核心流程测试覆盖

## 分支检查
□ 代码已 push 到远程
□ PR 已创建

## 合并后
□ PR 已合并到 main
□ feature 分支已删除（本地+远程）
```

---

## 7. 错误的代价

**违反规范的处罚：**

| 违规行为 | 后果 |
|----------|------|
| 后端写完未做前端就合并 | PR 被拒绝，代码回滚 |
| 跳过联调直接合并 | PR 被拒绝，需重新联调 |
| 合并后未删除分支 | 强制删除，警告记录 |
| E2E 测试失败仍合并 | PR 被拒绝，需修复后重新 |

---

## 8. 示例：正确的功能开发流程

```
目标：完成用户认证模块（Phase 0）

Day 1:
├── 1. 创建分支 feature/user-auth
├── 2. 后端：User/Role/Permission Entity + Mapper
├── 3. 后端：AuthService + JwtTokenProvider
├── 4. 后端：AuthController + UserController + RoleController
├── 5. 后端：单元测试
└── 6. 后端：mvn test 通过

Day 2:
├── 7. 前端：LoginPage 登录页面
├── 8. 前端：UserManagePage 用户管理页面
├── 9. 前端：RoleManagePage 角色管理页面
├── 10. 前端：API 封装
└── 11. 前端：npm run build 通过

Day 3:
├── 12. 启动后端服务
├── 13. 启动前端服务
├── 14. 登录联调验证
├── 15. 用户管理联调验证
├── 16. 角色管理联调验证
├── 17. 浏览器无报错
└── 18. Playwright E2E 测试通过

Day 4:
├── 19. 创建 PR
├── 20. CodeReview
├── 21. 合并到 main
└── 22. 删除 feature/user-auth 分支

✅ 用户认证模块完成
```

---

**记忆口诀：后端完、前端完、联调完、测试完，才能合并到 main！**
