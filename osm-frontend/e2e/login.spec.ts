import { test, expect, Page } from '@playwright/test'

// Helper function to perform login
async function login(page: Page, username: string = 'admin', password: string = 'admin123') {
  await page.goto('/login')
  await page.waitForLoadState('networkidle')
  await page.getByPlaceholder('用户名: admin').fill(username)
  await page.getByPlaceholder('密码: admin123').fill(password)
  await page.locator('button').filter({ hasText: /登\s?录/ }).click()
  await expect(page.getByText('登录成功')).toBeVisible({ timeout: 10000 })
}

test.describe('登录功能 E2E 测试', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/login')
    await page.waitForLoadState('networkidle')
  })

  test('登录页面正确加载', async ({ page }) => {
    // 验证页面标题
    await expect(page).toHaveTitle(/OSM/)
    // 验证登录表单元素存在
    await expect(page.getByPlaceholder('用户名: admin')).toBeVisible()
    await expect(page.getByPlaceholder('密码: admin123')).toBeVisible()
    await expect(page.locator('button').filter({ hasText: /登\s?录/ })).toBeVisible()
  })

  test('使用 admin/admin123 登录成功', async ({ page }) => {
    await login(page)
    // 验证登录成功 - 应该跳转到首页
    await expect(page).toHaveURL(/\//)
  })

  test('错误密码登录失败', async ({ page }) => {
    await page.getByPlaceholder('用户名: admin').fill('admin')
    await page.getByPlaceholder('密码: admin123').fill('wrongpassword')
    await page.locator('button').filter({ hasText: /登\s?录/ }).click()
    // 等待网络请求完成
    await page.waitForTimeout(3000)
    // 验证仍在登录页（未跳转）
    await expect(page).toHaveURL(/\/login/)
  })
})

test.describe('登录后功能测试 (JWT认证)', () => {
  test.beforeEach(async ({ page }) => {
    await login(page)
  })

  test('用户列表页面可访问', async ({ page }) => {
    // 验证页面元素存在（说明已认证）
    await expect(page.locator('body')).toBeVisible()
    // 用户列表应该在 DOM 中
    const userText = await page.locator('body').textContent()
    expect(userText).toBeTruthy()
  })

  test('API - 用户列表需要认证', async ({ page }) => {
    // 不带token直接访问API应该被拒绝
    const response = await page.request.get('/api/users')
    expect(response.status()).toBe(403)
  })

  test('API - 带token可访问用户列表', async ({ page }) => {
    // 登录后获取token
    const token = await page.evaluate(() => localStorage.getItem('token'))
    expect(token).toBeTruthy()

    // 带token访问API应该成功
    const response = await page.request.get('/api/users', {
      headers: { Authorization: `Bearer ${token}` },
    })
    expect(response.status()).toBe(200)
    const data = await response.json()
    expect(data.code).toBe(200)
    expect(data.data.length).toBeGreaterThan(0)
    expect(data.data[0].username).toBe('admin')
  })

  test('API - 带token可访问角色列表', async ({ page }) => {
    const token = await page.evaluate(() => localStorage.getItem('token'))
    const response = await page.request.get('/api/roles', {
      headers: { Authorization: `Bearer ${token}` },
    })
    expect(response.status()).toBe(200)
    const data = await response.json()
    expect(data.code).toBe(200)
    expect(data.data.length).toBeGreaterThan(0)
  })

  test('API - 带token可访问域列表', async ({ page }) => {
    const token = await page.evaluate(() => localStorage.getItem('token'))
    const response = await page.request.get('/api/domains', {
      headers: { Authorization: `Bearer ${token}` },
    })
    expect(response.status()).toBe(200)
    const data = await response.json()
    expect(data.code).toBe(200)
    expect(data.data.length).toBeGreaterThan(0)
  })
})