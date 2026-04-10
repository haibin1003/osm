import { test, expect, Page } from '@playwright/test'

// ============ Helper Functions ============

async function login(page: Page) {
  await page.goto('/login')
  await page.waitForLoadState('networkidle')
  await page.getByPlaceholder('用户名: admin').fill('admin')
  await page.getByPlaceholder('密码: admin123').fill('admin123')
  await page.locator('button').filter({ hasText: /登\s?录/ }).click()
  await expect(page.getByText('登录成功')).toBeVisible({ timeout: 10000 })
}

async function getToken(page: Page): Promise<string> {
  return await page.evaluate(() => localStorage.getItem('token') || '')
}

// ============ Login Tests ============

test.describe('1. 登录功能测试', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/login')
    await page.waitForLoadState('networkidle')
  })

  test('登录页面正确加载', async ({ page }) => {
    await expect(page).toHaveTitle(/OSM/)
    await expect(page.getByPlaceholder('用户名: admin')).toBeVisible()
    await expect(page.getByPlaceholder('密码: admin123')).toBeVisible()
  })

  test('admin/admin123 登录成功', async ({ page }) => {
    await login(page)
    await expect(page).toHaveURL(/\//)
  })

  test('错误密码登录失败', async ({ page }) => {
    await page.getByPlaceholder('用户名: admin').fill('admin')
    await page.getByPlaceholder('密码: admin123').fill('wrongpassword')
    await page.locator('button').filter({ hasText: /登\s?录/ }).click()
    await page.waitForTimeout(3000)
    await expect(page).toHaveURL(/\/login/)
  })
})

// ============ Dashboard Tests ============

test.describe('2. 仪表盘测试', () => {
  test('登录后显示仪表盘', async ({ page }) => {
    await login(page)
    await expect(page).toHaveURL(/\//)
    // 验证显示欢迎信息
    await expect(page.getByText(/欢迎回来/)).toBeVisible()
  })
})

// ============ User Management Tests ============

test.describe('3. 用户管理功能测试', () => {
  test.beforeEach(async ({ page }) => {
    await login(page)
  })

  test('用户列表页面可访问', async ({ page }) => {
    // 通过导航或直接访问
    await page.goto('/users')
    await page.waitForLoadState('networkidle')
    // 验证表格存在
    await expect(page.locator('.ant-table')).toBeVisible({ timeout: 10000 })
    // 验证 admin 用户存在
    await expect(page.getByText('admin')).toBeVisible()
  })

  test('API - 获取用户列表', async ({ page }) => {
    const token = await getToken(page)
    const response = await page.request.get('/api/users', {
      headers: { Authorization: `Bearer ${token}` },
    })
    expect(response.status()).toBe(200)
    const data = await response.json()
    expect(data.code).toBe(200)
    expect(data.data.length).toBeGreaterThan(0)
    expect(data.data.some((u: any) => u.username === 'admin')).toBeTruthy()
  })

  test('API - 获取单个用户', async ({ page }) => {
    const token = await getToken(page)
    const response = await page.request.get('/api/users/1', {
      headers: { Authorization: `Bearer ${token}` },
    })
    expect(response.status()).toBe(200)
    const data = await response.json()
    expect(data.code).toBe(200)
    expect(data.data.username).toBe('admin')
  })

  test('API - 创建新用户', async ({ page }) => {
    const token = await getToken(page)
    const newUser = {
      username: `testuser_${Date.now()}`,
      password: 'test123',
      email: 'test@example.com',
      status: 1,
    }
    const response = await page.request.post('/api/users', {
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      data: newUser,
    })
    expect(response.status()).toBe(200)
    const data = await response.json()
    expect(data.code).toBe(200)
    expect(data.data).toBeGreaterThan(0)
  })

  test('API - 更新用户', async ({ page }) => {
    const token = await getToken(page)
    // 先创建用户
    const createResponse = await page.request.post('/api/users', {
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      data: {
        username: `updateuser_${Date.now()}`,
        password: 'test123',
        email: 'update@example.com',
        status: 1,
      },
    })
    const userId = (await createResponse.json()).data

    // 更新用户
    const updateResponse = await page.request.put(`/api/users/${userId}`, {
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      data: { email: 'updated@example.com' },
    })
    expect(updateResponse.status()).toBe(200)
    const data = await updateResponse.json()
    expect(data.code).toBe(200)
  })

  test('API - 删除用户', async ({ page }) => {
    const token = await getToken(page)
    // 先创建用户
    const createResponse = await page.request.post('/api/users', {
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      data: {
        username: `deleteuser_${Date.now()}`,
        password: 'test123',
        email: 'delete@example.com',
        status: 1,
      },
    })
    const userId = (await createResponse.json()).data

    // 删除用户
    const deleteResponse = await page.request.delete(`/api/users/${userId}`, {
      headers: { Authorization: `Bearer ${token}` },
    })
    expect(deleteResponse.status()).toBe(200)
  })
})

// ============ Role Management Tests ============

test.describe('4. 角色管理功能测试', () => {
  test.beforeEach(async ({ page }) => {
    await login(page)
  })

  test('API - 获取角色列表', async ({ page }) => {
    const token = await getToken(page)
    const response = await page.request.get('/api/roles', {
      headers: { Authorization: `Bearer ${token}` },
    })
    expect(response.status()).toBe(200)
    const data = await response.json()
    expect(data.code).toBe(200)
    expect(data.data.length).toBeGreaterThan(0)
    expect(data.data.some((r: any) => r.code === 'ADMIN')).toBeTruthy()
  })

  test('API - 获取单个角色', async ({ page }) => {
    const token = await getToken(page)
    const response = await page.request.get('/api/roles/1', {
      headers: { Authorization: `Bearer ${token}` },
    })
    expect(response.status()).toBe(200)
    const data = await response.json()
    expect(data.code).toBe(200)
    expect(data.data.code).toBe('ADMIN')
  })

  test('API - 创建新角色', async ({ page }) => {
    const token = await getToken(page)
    const newRole = {
      code: `TEST_ROLE_${Date.now()}`,
      name: '测试角色',
      description: 'E2E测试创建的角色',
    }
    const response = await page.request.post('/api/roles', {
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      data: newRole,
    })
    expect(response.status()).toBe(200)
    const data = await response.json()
    expect(data.code).toBe(200)
  })

  test('API - 更新角色', async ({ page }) => {
    const token = await getToken(page)
    const response = await page.request.put('/api/roles/2', {
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      data: { description: '更新后的描述' },
    })
    expect(response.status()).toBe(200)
  })
})

// ============ Domain Management Tests ============

test.describe('5. 域管理功能测试', () => {
  test.beforeEach(async ({ page }) => {
    await login(page)
  })

  test('API - 获取域列表', async ({ page }) => {
    const token = await getToken(page)
    const response = await page.request.get('/api/domains', {
      headers: { Authorization: `Bearer ${token}` },
    })
    expect(response.status()).toBe(200)
    const data = await response.json()
    expect(data.code).toBe(200)
    expect(data.data.length).toBeGreaterThan(0)
  })

  test('API - 创建新域', async ({ page }) => {
    const token = await getToken(page)
    const newDomain = {
      name: `测试域_${Date.now()}`,
      description: 'E2E测试创建的域',
    }
    const response = await page.request.post('/api/domains', {
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      data: newDomain,
    })
    expect(response.status()).toBe(200)
    const data = await response.json()
    expect(data.code).toBe(200)
  })

  test('API - 更新域', async ({ page }) => {
    const token = await getToken(page)
    // 先创建域
    const createResponse = await page.request.post('/api/domains', {
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      data: {
        name: `update_domain_${Date.now()}`,
        description: '待更新',
      },
    })
    const domainId = (await createResponse.json()).data

    // 更新域
    const updateResponse = await page.request.put(`/api/domains/${domainId}`, {
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      data: { description: '已更新描述' },
    })
    expect(updateResponse.status()).toBe(200)
  })

  test('API - 删除域', async ({ page }) => {
    const token = await getToken(page)
    // 先创建域
    const createResponse = await page.request.post('/api/domains', {
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      data: {
        name: `delete_domain_${Date.now()}`,
        description: '待删除',
      },
    })
    const domainId = (await createResponse.json()).data

    // 删除域
    const deleteResponse = await page.request.delete(`/api/domains/${domainId}`, {
      headers: { Authorization: `Bearer ${token}` },
    })
    expect(deleteResponse.status()).toBe(200)
  })
})

// ============ System Management Tests ============

test.describe('6. 系统管理功能测试', () => {
  test.beforeEach(async ({ page }) => {
    await login(page)
  })

  test('API - 获取系统列表', async ({ page }) => {
    const token = await getToken(page)
    const response = await page.request.get('/api/systems', {
      headers: { Authorization: `Bearer ${token}` },
    })
    expect(response.status()).toBe(200)
    const data = await response.json()
    expect(data.code).toBe(200)
  })

  test('API - 按域获取系统列表', async ({ page }) => {
    const token = await getToken(page)
    const response = await page.request.get('/api/systems/domain/1', {
      headers: { Authorization: `Bearer ${token}` },
    })
    expect(response.status()).toBe(200)
    const data = await response.json()
    expect(data.code).toBe(200)
  })

  test('API - 创建新系统', async ({ page }) => {
    const token = await getToken(page)
    const newSystem = {
      name: `测试系统_${Date.now()}`,
      code: `TEST_SYS_${Date.now()}`,
      domainId: 1,
      description: 'E2E测试创建的系统',
    }
    const response = await page.request.post('/api/systems', {
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      data: newSystem,
    })
    expect(response.status()).toBe(200)
    const data = await response.json()
    expect(data.code).toBe(200)
  })
})

// ============ Application Management Tests ============

test.describe('7. 应用管理功能测试', () => {
  test.beforeEach(async ({ page }) => {
    await login(page)
  })

  test('API - 获取应用列表', async ({ page }) => {
    const token = await getToken(page)
    const response = await page.request.get('/api/applications', {
      headers: { Authorization: `Bearer ${token}` },
    })
    expect(response.status()).toBe(200)
    const data = await response.json()
    expect(data.code).toBe(200)
  })

  test('API - 按系统获取应用列表', async ({ page }) => {
    const token = await getToken(page)
    const response = await page.request.get('/api/applications/system/1', {
      headers: { Authorization: `Bearer ${token}` },
    })
    expect(response.status()).toBe(200)
    const data = await response.json()
    expect(data.code).toBe(200)
  })

  test('API - 创建新应用', async ({ page }) => {
    const token = await getToken(page)
    const newApp = {
      name: `测试应用_${Date.now()}`,
      code: `TEST_APP_${Date.now()}`,
      systemId: 1,
      description: 'E2E测试创建的应用',
    }
    const response = await page.request.post('/api/applications', {
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      data: newApp,
    })
    expect(response.status()).toBe(200)
    const data = await response.json()
    expect(data.code).toBe(200)
  })
})

// ============ Dict Tests ============

test.describe('8. 字典功能测试', () => {
  test.beforeEach(async ({ page }) => {
    await login(page)
  })

  test('API - 获取技术分类字典', async ({ page }) => {
    const token = await getToken(page)
    const response = await page.request.get('/api/dicts/tech_category', {
      headers: { Authorization: `Bearer ${token}` },
    })
    expect(response.status()).toBe(200)
    const data = await response.json()
    expect(data.code).toBe(200)
    expect(data.data.length).toBeGreaterThan(0)
  })

  test('API - 获取许可证类型字典', async ({ page }) => {
    const token = await getToken(page)
    const response = await page.request.get('/api/dicts/license_type', {
      headers: { Authorization: `Bearer ${token}` },
    })
    expect(response.status()).toBe(200)
    const data = await response.json()
    expect(data.code).toBe(200)
    expect(data.data.length).toBeGreaterThan(0)
  })
})

// ============ Auth Tests ============

test.describe('9. 认证信息测试', () => {
  test('API - 获取当前用户信息', async ({ page }) => {
    await login(page)
    const token = await getToken(page)
    const response = await page.request.get('/api/auth/me', {
      headers: { Authorization: `Bearer ${token}` },
    })
    expect(response.status()).toBe(200)
    const data = await response.json()
    expect(data.code).toBe(200)
    expect(data.data.username).toBe('admin')
    expect(data.data.roles).toContain('ADMIN')
  })
})

// ============ Security Tests ============

test.describe('10. 安全测试', () => {
  test('不带token访问API应返回403', async ({ page }) => {
    await page.goto('/login')
    await page.waitForLoadState('networkidle')

    // 不登录直接访问API
    const response = await page.request.get('/api/users')
    expect(response.status()).toBe(403)
  })

  test('带无效token访问API应返回403', async ({ page }) => {
    await page.goto('/login')
    await page.waitForLoadState('networkidle')

    const response = await page.request.get('/api/users', {
      headers: { Authorization: 'Bearer invalid_token' },
    })
    expect(response.status()).toBe(403)
  })

  test('登录后token应存储在localStorage', async ({ page }) => {
    await login(page)
    const token = await page.evaluate(() => localStorage.getItem('token'))
    expect(token).toBeTruthy()
    expect(token!.length).toBeGreaterThan(20)
  })
})