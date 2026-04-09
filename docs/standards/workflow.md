# 开发流程规范

> 定义项目的开发流程、分支策略、PR流程和发布流程

---

## 1. 分支策略

### 分支类型

```
main
├── develop
│   ├── feature/order-approval
│   ├── feature/software-library
│   └── feature/data-visualization
└── hotfix/security-fix
```

| 分支 | 用途 | 来源 | 合并目标 |
|------|------|------|----------|
| `main` | 生产环境代码 | - | - |
| `develop` | 开发集成环境 | main | main |
| `feature/*` | 功能开发 | develop | develop |
| `hotfix/*` | 紧急修复 | main | main + develop |
| `release/*` | 版本发布 | develop | main |

### 分支命名规范

```
feature/<模块>-<简述>
  例: feature/order-submit-flow
  例: feature/system-management-crud

hotfix/<问题简述>
  例: hotfix/login-nullpointer
  例: hotfix/api-response-error

release/<版本号>
  例: release/v1.0.0
  例: release/v1.1.0-beta
```

---

## 2. 开发流程

### 2.1 新功能开发

```
1. 从 develop 切出新分支
   git checkout develop
   git pull origin develop
   git checkout -b feature/order-submit

2. 开发（TDD模式）
   - 写测试
   - 写实现
   - 运行测试
   - 重构

3. 本地验证
   mvn test                    # 后端测试通过
   mvn jacoco:report           # 检查覆盖率 >= 80%
   npm run test                # 前端测试通过

4. 提交代码
   git add .
   git commit -m "feat(order): 添加订购申请提交接口"
   git push -u origin feature/order-submit

5. 创建 PR
   - 目标分支: develop
   - 填写 PR 模板
   - 关联相关 Issue
   - 添加 Reviewer

6. Code Review
   - Reviewer 检查代码
   - 处理 Review 意见
   - CI 通过

7. 合并代码
   - 使用 Squash Merge
   - 删除 feature 分支
```

### 2.2 Bug 修复

```
1. 从 develop 切出 fix 分支（普通bug）
   git checkout -b fix/order-status-calc

   或从 main 切出 hotfix 分支（紧急bug）
   git checkout -b hotfix/login-error

2. 修复 + 测试

3. 提交 PR（普通bug → develop，紧急bug → main）

4. Review 合并
   - hotfix 需要同时合并到 main 和 develop
```

---

## 3. PR 流程

### PR 模板

```markdown
## 变更内容
- 功能: 添加订购申请提交接口
- 影响范围: order 模块

## 测试
- [ ] 单元测试已添加/更新
- [ ] 集成测试已添加/更新
- [ ] 手动测试通过
- [ ] 测试覆盖率 >= 80%

## 文档
- [ ] API 文档已更新
- [ ] 技术文档已更新（如有）

## 检查清单
- [ ] 代码遵循项目规范
- [ ] 无编译警告
- [ ] 无调试代码

## 关联 Issue
Fixes #123
```

### Review 规范

**Reviewer 检查项：**

```
□ 代码逻辑正确
□ 测试覆盖充分
□ 命名规范清晰
□ 异常处理完善
□ 无安全问题
□ 性能无隐患
□ 文档同步更新
```

**Review 意见级别：**

| 级别 | 标识 | 说明 | 处理方式 |
|------|------|------|----------|
| Blocker | 🔴 | 严重问题，必须修改 | 不改不合并 |
| Major | 🟠 | 重要问题，建议修改 | 解释不修改原因 |
| Minor | 🟡 | 小问题，可选修改 | 自行决定 |
| Nit | 🟢 | 代码风格建议 | 可选 |

---

## 4. 发布流程

### 4.1 版本号规范（SemVer）

```
主版本号.次版本号.修订号
  1.   0.    0

- 主版本号：不兼容的 API 修改
- 次版本号：向下兼容的功能新增
- 修订号：向下兼容的问题修复
```

### 4.2 发布步骤

```
1. 从 develop 切出 release 分支
   git checkout -b release/v1.0.0

2. 版本号更新 + 更新 CHANGELOG

3. 测试验证
   - 集成测试
   - 回归测试

4. 合并到 main
   git checkout main
   git merge --no-ff release/v1.0.0
   git tag v1.0.0
   git push origin main --tags

5. 合并回 develop
   git checkout develop
   git merge release/v1.0.0
   git push origin develop

6. 删除 release 分支
```

---

## 5. 冲突解决

### 解决步骤

```
1. 更新本地 develop
   git checkout develop
   git pull origin develop

2. 切换回 feature 分支
   git checkout feature/order-submit

3. 执行 rebase
   git rebase develop

4. 解决冲突
   - 打开冲突文件
   - 保留需要的代码
   - 删除冲突标记
   - git add .
   - git rebase --continue

5. 强制推送（rebase 后历史改变）
   git push -f origin feature/order-submit
```

---

## 6. 回滚流程

### 6.1 回滚已合并代码

```
方式1: Revert（推荐，保留历史）
   git revert <commit-hash>
   git push origin develop

方式2: Reset（仅本地未推送）
   git reset --hard <commit-hash>
```

### 6.2 生产环境回滚

```
1. 定位问题 commit
2. 创建 hotfix 分支从 main
3. revert 问题 commit
4. 快速 review 合并
5. 紧急发布
```
