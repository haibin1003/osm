# OSM 系统管理模块实施计划

> **For agentic workers:** REQUIRED SUB-TOOL: Use subagent-driven-development or executing-plans to implement this plan task-by-task.

**Goal:** 实现业务系统管理功能，包括业务域(Domain)、业务系统(System)、应用(Application)的完整CRUD接口和前端页面。

**Architecture:** 采用分层架构，Controller处理HTTP请求，Service处理业务逻辑，Mapper处理数据访问。前端使用React + Ant Design实现列表、表单、详情页面。

**Tech Stack:** Spring Boot, MyBatis-Plus, React, Ant Design, TypeScript

---

## 文件结构

```
osm-backend/
└── src/main/java/com/osm/domain/
    └── system/
        ├── entity/
        │   ├── Domain.java
        │   ├── System.java
        │   └── Application.java
        ├── mapper/
        │   ├── DomainMapper.java
        │   ├── SystemMapper.java
        │   └── ApplicationMapper.java
        ├── service/
        │   ├── DomainService.java
        │   ├── SystemService.java
        │   ├── ApplicationService.java
        │   └── impl/
        │       ├── DomainServiceImpl.java
        │       ├── SystemServiceImpl.java
        │       └── ApplicationServiceImpl.java
        ├── dto/
        │   ├── CreateDomainRequest.java
        │   ├── UpdateDomainRequest.java
        │   ├── CreateSystemRequest.java
        │   ├── UpdateSystemRequest.java
        │   ├── CreateApplicationRequest.java
        │   ├── UpdateApplicationRequest.java
        │   └── SystemQuery.java
        ├── vo/
        │   ├── DomainVO.java
        │   ├── SystemVO.java
        │   ├── SystemDetailVO.java
        │   └── ApplicationVO.java
        └── controller/
            ├── DomainController.java
            ├── SystemController.java
            └── ApplicationController.java

osm-frontend/
└── src/pages/SystemManage/
    ├── DomainList.tsx
    ├── DomainForm.tsx
    ├── SystemList.tsx
    ├── SystemForm.tsx
    ├── SystemDetail.tsx
    ├── ApplicationList.tsx
    └── ApplicationForm.tsx
```

---

## Task 1: 创建Domain（业务域）模块

**Files:**
- Create: `osm-backend/src/main/java/com/osm/domain/system/entity/Domain.java`
- Create: `osm-backend/src/main/java/com/osm/domain/system/mapper/DomainMapper.java`
- Create: `osm-backend/src/main/java/com/osm/domain/system/service/DomainService.java`
- Create: `osm-backend/src/main/java/com/osm/domain/system/service/impl/DomainServiceImpl.java`
- Create: `osm-backend/src/main/java/com/osm/domain/system/controller/DomainController.java`
- Create: `osm-backend/src/main/java/com/osm/domain/system/dto/CreateDomainRequest.java`
- Create: `osm-backend/src/main/java/com/osm/domain/system/dto/UpdateDomainRequest.java`
- Create: `osm-backend/src/main/java/com/osm/domain/system/vo/DomainVO.java`
- Test: `osm-backend/src/test/java/com/osm/domain/system/service/DomainServiceTest.java`

- [ ] **Step 1: 创建Domain实体类**

Create `osm-backend/src/main/java/com/osm/domain/system/entity/Domain.java`:
```java
package com.osm.domain.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.osm.domain.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("osm_domain")
public class Domain extends BaseEntity {
    private String name;
    private String description;
}
```

- [ ] **Step 2: 创建DomainMapper**

Create `osm-backend/src/main/java/com/osm/domain/system/mapper/DomainMapper.java`:
```java
package com.osm.domain.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.osm.domain.system.entity.Domain;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DomainMapper extends BaseMapper<Domain> {
}
```

- [ ] **Step 3: 创建DTO和VO**

Create `osm-backend/src/main/java/com/osm/domain/system/dto/CreateDomainRequest.java`:
```java
package com.osm.domain.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateDomainRequest {
    @NotBlank(message = "域名称不能为空")
    @Size(max = 128, message = "域名称不能超过128个字符")
    private String name;
    
    @Size(max = 500, message = "描述不能超过500个字符")
    private String description;
}
```

Create `osm-backend/src/main/java/com/osm/domain/system/dto/UpdateDomainRequest.java`:
```java
package com.osm.domain.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateDomainRequest {
    @NotBlank(message = "域名称不能为空")
    @Size(max = 128, message = "域名称不能超过128个字符")
    private String name;
    
    @Size(max = 500, message = "描述不能超过500个字符")
    private String description;
}
```

Create `osm-backend/src/main/java/com/osm/domain/system/vo/DomainVO.java`:
```java
package com.osm.domain.system.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DomainVO {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

- [ ] **Step 4: 编写Service测试**

Create `osm-backend/src/test/java/com/osm/domain/system/service/DomainServiceTest.java`:
```java
package com.osm.domain.system.service;

import com.osm.domain.system.dto.CreateDomainRequest;
import com.osm.domain.system.entity.Domain;
import com.osm.domain.system.mapper.DomainMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DomainServiceTest {

    @Mock
    private DomainMapper domainMapper;

    @InjectMocks
    private DomainServiceImpl domainService;

    @Test
    void shouldCreateDomainSuccessfully() {
        CreateDomainRequest request = new CreateDomainRequest();
        request.setName("金融域");
        request.setDescription("金融业务系统");

        when(domainMapper.insert(any(Domain.class))).thenAnswer(invocation -> {
            Domain domain = invocation.getArgument(0);
            domain.setId(1L);
            return 1;
        });

        Long id = domainService.create(request);

        assertNotNull(id);
        assertEquals(1L, id);
        verify(domainMapper).insert(any(Domain.class));
    }

    @Test
    void shouldGetDomainById() {
        Domain domain = new Domain();
        domain.setId(1L);
        domain.setName("金融域");

        when(domainMapper.selectById(1L)).thenReturn(domain);

        Domain result = domainService.getById(1L);

        assertNotNull(result);
        assertEquals("金融域", result.getName());
    }
}
```

Run test:
```bash
cd osm-backend
mvn test -Dtest=DomainServiceTest
```
Expected: Tests fail (service not implemented)

- [ ] **Step 5: 实现DomainService**

Create `osm-backend/src/main/java/com/osm/domain/system/service/DomainService.java`:
```java
package com.osm.domain.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.osm.domain.system.dto.CreateDomainRequest;
import com.osm.domain.system.dto.UpdateDomainRequest;
import com.osm.domain.system.entity.Domain;
import com.osm.domain.system.vo.DomainVO;

import java.util.List;

public interface DomainService extends IService<Domain> {
    Long create(CreateDomainRequest request);
    void update(Long id, UpdateDomainRequest request);
    DomainVO getDetail(Long id);
    List<DomainVO> listAll();
}
```

Create `osm-backend/src/main/java/com/osm/domain/system/service/impl/DomainServiceImpl.java`:
```java
package com.osm.domain.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.osm.domain.system.dto.CreateDomainRequest;
import com.osm.domain.system.dto.UpdateDomainRequest;
import com.osm.domain.system.entity.Domain;
import com.osm.domain.system.mapper.DomainMapper;
import com.osm.domain.system.service.DomainService;
import com.osm.domain.system.vo.DomainVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DomainServiceImpl extends ServiceImpl<DomainMapper, Domain> implements DomainService {

    @Override
    public Long create(CreateDomainRequest request) {
        Domain domain = new Domain();
        BeanUtils.copyProperties(request, domain);
        baseMapper.insert(domain);
        return domain.getId();
    }

    @Override
    public void update(Long id, UpdateDomainRequest request) {
        Domain domain = getById(id);
        if (domain == null) {
            throw new RuntimeException("域不存在");
        }
        BeanUtils.copyProperties(request, domain);
        baseMapper.updateById(domain);
    }

    @Override
    public DomainVO getDetail(Long id) {
        Domain domain = getById(id);
        if (domain == null) {
            return null;
        }
        DomainVO vo = new DomainVO();
        BeanUtils.copyProperties(domain, vo);
        return vo;
    }

    @Override
    public List<DomainVO> listAll() {
        return list().stream().map(domain -> {
            DomainVO vo = new DomainVO();
            BeanUtils.copyProperties(domain, vo);
            return vo;
        }).collect(Collectors.toList());
    }
}
```

Run test again:
```bash
mvn test -Dtest=DomainServiceTest
```
Expected: Tests pass

- [ ] **Step 6: 实现DomainController**

Create `osm-backend/src/main/java/com/osm/domain/system/controller/DomainController.java`:
```java
package com.osm.domain.system.controller;

import com.osm.common.result.Result;
import com.osm.domain.system.dto.CreateDomainRequest;
import com.osm.domain.system.dto.UpdateDomainRequest;
import com.osm.domain.system.service.DomainService;
import com.osm.domain.system.vo.DomainVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/domains")
@RequiredArgsConstructor
public class DomainController {

    private final DomainService domainService;

    @PostMapping
    public Result<Long> create(@RequestBody @Valid CreateDomainRequest request) {
        Long id = domainService.create(request);
        return Result.success(id);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody @Valid UpdateDomainRequest request) {
        domainService.update(id, request);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<DomainVO> getById(@PathVariable Long id) {
        DomainVO vo = domainService.getDetail(id);
        return Result.success(vo);
    }

    @GetMapping
    public Result<List<DomainVO>> list() {
        List<DomainVO> list = domainService.listAll();
        return Result.success(list);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        domainService.removeById(id);
        return Result.success();
    }
}
```

- [ ] **Step 7: Commit**

```bash
git add osm-backend/src/main/java/com/osm/domain/system/
git add osm-backend/src/test/java/com/osm/domain/system/
git commit -m "feat: add domain management module

- Add Domain entity, mapper, service, controller
- Add CRUD APIs for domain management
- Add unit tests for DomainService"
```

---

## Task 2: 创建System（业务系统）模块

**Files:**
- Create: `osm-backend/src/main/java/com/osm/domain/system/entity/System.java`
- Create: `osm-backend/src/main/java/com/osm/domain/system/mapper/SystemMapper.java`
- Create: `osm-backend/src/main/java/com/osm/domain/system/service/SystemService.java`
- Create: `osm-backend/src/main/java/com/osm/domain/system/service/impl/SystemServiceImpl.java`
- Create: `osm-backend/src/main/java/com/osm/domain/system/controller/SystemController.java`
- Create: `osm-backend/src/main/java/com/osm/domain/system/dto/CreateSystemRequest.java`
- Create: `osm-backend/src/main/java/com/osm/domain/system/dto/UpdateSystemRequest.java`
- Create: `osm-backend/src/main/java/com/osm/domain/system/dto/SystemQuery.java`
- Create: `osm-backend/src/main/java/com/osm/domain/system/vo/SystemVO.java`
- Create: `osm-backend/src/main/java/com/osm/domain/system/vo/SystemDetailVO.java`

- [ ] **Step 1: 创建System实体类**

Create `osm-backend/src/main/java/com/osm/domain/system/entity/System.java`:
```java
package com.osm.domain.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.osm.domain.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("osm_system")
public class System extends BaseEntity {
    private String name;
    private Long domainId;
    private String description;
    private String owners;
}
```

- [ ] **Step 2: 创建DTO和VO**

Create `osm-backend/src/main/java/com/osm/domain/system/dto/CreateSystemRequest.java`:
```java
package com.osm.domain.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateSystemRequest {
    @NotBlank(message = "系统名称不能为空")
    @Size(max = 128, message = "系统名称不能超过128个字符")
    private String name;
    
    @NotNull(message = "所属域不能为空")
    private Long domainId;
    
    @Size(max = 500, message = "描述不能超过500个字符")
    private String description;
    
    @Size(max = 500, message = "负责人不能超过500个字符")
    private String owners;
}
```

Create `osm-backend/src/main/java/com/osm/domain/system/vo/SystemVO.java`:
```java
package com.osm.domain.system.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SystemVO {
    private Long id;
    private String name;
    private Long domainId;
    private String domainName;
    private String description;
    private String owners;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

Create `osm-backend/src/main/java/com/osm/domain/system/vo/SystemDetailVO.java`:
```java
package com.osm.domain.system.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SystemDetailVO {
    private Long id;
    private String name;
    private Long domainId;
    private String domainName;
    private String description;
    private String owners;
    private List<String> ownerList;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

- [ ] **Step 3: 实现SystemService**

Create `osm-backend/src/main/java/com/osm/domain/system/service/SystemService.java`:
```java
package com.osm.domain.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.osm.domain.system.dto.CreateSystemRequest;
import com.osm.domain.system.dto.SystemQuery;
import com.osm.domain.system.dto.UpdateSystemRequest;
import com.osm.domain.system.entity.System;
import com.osm.domain.system.vo.SystemDetailVO;
import com.osm.domain.system.vo.SystemVO;

import java.util.List;

public interface SystemService extends IService<System> {
    Long create(CreateSystemRequest request);
    void update(Long id, UpdateSystemRequest request);
    SystemDetailVO getDetail(Long id);
    IPage<SystemVO> queryPage(SystemQuery query);
    List<SystemVO> listByDomain(Long domainId);
}
```

Create `osm-backend/src/main/java/com/osm/domain/system/service/impl/SystemServiceImpl.java`:
```java
package com.osm.domain.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.osm.domain.system.dto.CreateSystemRequest;
import com.osm.domain.system.dto.SystemQuery;
import com.osm.domain.system.dto.UpdateSystemRequest;
import com.osm.domain.system.entity.System;
import com.osm.domain.system.mapper.DomainMapper;
import com.osm.domain.system.mapper.SystemMapper;
import com.osm.domain.system.service.SystemService;
import com.osm.domain.system.vo.SystemDetailVO;
import com.osm.domain.system.vo.SystemVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SystemServiceImpl extends ServiceImpl<SystemMapper, System> implements SystemService {

    private final DomainMapper domainMapper;

    @Override
    public Long create(CreateSystemRequest request) {
        System system = new System();
        BeanUtils.copyProperties(request, system);
        baseMapper.insert(system);
        return system.getId();
    }

    @Override
    public void update(Long id, UpdateSystemRequest request) {
        System system = getById(id);
        if (system == null) {
            throw new RuntimeException("系统不存在");
        }
        BeanUtils.copyProperties(request, system);
        baseMapper.updateById(system);
    }

    @Override
    public SystemDetailVO getDetail(Long id) {
        System system = getById(id);
        if (system == null) {
            return null;
        }
        
        SystemDetailVO vo = new SystemDetailVO();
        BeanUtils.copyProperties(system, vo);
        
        // Set domain name
        var domain = domainMapper.selectById(system.getDomainId());
        if (domain != null) {
            vo.setDomainName(domain.getName());
        }
        
        // Parse owners
        if (StringUtils.hasText(system.getOwners())) {
            vo.setOwnerList(Arrays.asList(system.getOwners().split(",")));
        }
        
        return vo;
    }

    @Override
    public IPage<SystemVO> queryPage(SystemQuery query) {
        Page<System> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<System> wrapper = new LambdaQueryWrapper<>();
        
        if (query.getDomainId() != null) {
            wrapper.eq(System::getDomainId, query.getDomainId());
        }
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(System::getName, query.getKeyword());
        }
        
        IPage<System> systemPage = baseMapper.selectPage(page, wrapper);
        
        return systemPage.convert(system -> {
            SystemVO vo = new SystemVO();
            BeanUtils.copyProperties(system, vo);
            var domain = domainMapper.selectById(system.getDomainId());
            if (domain != null) {
                vo.setDomainName(domain.getName());
            }
            return vo;
        });
    }

    @Override
    public List<SystemVO> listByDomain(Long domainId) {
        LambdaQueryWrapper<System> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(System::getDomainId, domainId);
        
        return list(wrapper).stream().map(system -> {
            SystemVO vo = new SystemVO();
            BeanUtils.copyProperties(system, vo);
            return vo;
        }).collect(Collectors.toList());
    }
}
```

- [ ] **Step 4: 实现SystemController**

Create `osm-backend/src/main/java/com/osm/domain/system/controller/SystemController.java`:
```java
package com.osm.domain.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.osm.common.result.Result;
import com.osm.domain.system.dto.CreateSystemRequest;
import com.osm.domain.system.dto.SystemQuery;
import com.osm.domain.system.dto.UpdateSystemRequest;
import com.osm.domain.system.service.SystemService;
import com.osm.domain.system.vo.SystemDetailVO;
import com.osm.domain.system.vo.SystemVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/systems")
@RequiredArgsConstructor
public class SystemController {

    private final SystemService systemService;

    @PostMapping
    public Result<Long> create(@RequestBody @Valid CreateSystemRequest request) {
        Long id = systemService.create(request);
        return Result.success(id);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody @Valid UpdateSystemRequest request) {
        systemService.update(id, request);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<SystemDetailVO> getById(@PathVariable Long id) {
        SystemDetailVO vo = systemService.getDetail(id);
        return Result.success(vo);
    }

    @GetMapping
    public Result<IPage<SystemVO>> query(SystemQuery query) {
        IPage<SystemVO> page = systemService.queryPage(query);
        return Result.success(page);
    }

    @GetMapping("/by-domain/{domainId}")
    public Result<List<SystemVO>> listByDomain(@PathVariable Long domainId) {
        List<SystemVO> list = systemService.listByDomain(domainId);
        return Result.success(list);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        systemService.removeById(id);
        return Result.success();
    }
}
```

- [ ] **Step 5: Commit**

```bash
git add osm-backend/src/main/java/com/osm/domain/system/
git commit -m "feat: add system management module

- Add System entity, mapper, service, controller
- Add pagination query with domain name join
- Add list by domain API"
```

---

## Task 3: 创建Application（应用）模块

**Files:**
- Create: `osm-backend/src/main/java/com/osm/domain/system/entity/Application.java`
- Create: `osm-backend/src/main/java/com/osm/domain/system/mapper/ApplicationMapper.java`
- Create: `osm-backend/src/main/java/com/osm/domain/system/service/ApplicationService.java`
- Create: `osm-backend/src/main/java/com/osm/domain/system/service/impl/ApplicationServiceImpl.java`
- Create: `osm-backend/src/main/java/com/osm/domain/system/controller/ApplicationController.java`
- Create: `osm-backend/src/main/java/com/osm/domain/system/dto/CreateApplicationRequest.java`
- Create: `osm-backend/src/main/java/com/osm/domain/system/dto/UpdateApplicationRequest.java`
- Create: `osm-backend/src/main/java/com/osm/domain/system/vo/ApplicationVO.java`

- [ ] **Step 1: 创建Application实体和Mapper**

Create `osm-backend/src/main/java/com/osm/domain/system/entity/Application.java`:
```java
package com.osm.domain.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.osm.domain.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("osm_application")
public class Application extends BaseEntity {
    private String name;
    private Long systemId;
    private String description;
}
```

Create `osm-backend/src/main/java/com/osm/domain/system/mapper/ApplicationMapper.java`:
```java
package com.osm.domain.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.osm.domain.system.entity.Application;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ApplicationMapper extends BaseMapper<Application> {
}
```

- [ ] **Step 2: 实现ApplicationService和Controller**

Create `osm-backend/src/main/java/com/osm/domain/system/service/ApplicationService.java`:
```java
package com.osm.domain.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.osm.domain.system.dto.CreateApplicationRequest;
import com.osm.domain.system.dto.UpdateApplicationRequest;
import com.osm.domain.system.entity.Application;
import com.osm.domain.system.vo.ApplicationVO;

import java.util.List;

public interface ApplicationService extends IService<Application> {
    Long create(CreateApplicationRequest request);
    void update(Long id, UpdateApplicationRequest request);
    List<ApplicationVO> listBySystem(Long systemId);
}
```

Create `osm-backend/src/main/java/com/osm/domain/system/service/impl/ApplicationServiceImpl.java`:
```java
package com.osm.domain.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.osm.domain.system.dto.CreateApplicationRequest;
import com.osm.domain.system.dto.UpdateApplicationRequest;
import com.osm.domain.system.entity.Application;
import com.osm.domain.system.mapper.ApplicationMapper;
import com.osm.domain.system.service.ApplicationService;
import com.osm.domain.system.vo.ApplicationVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl extends ServiceImpl<ApplicationMapper, Application> implements ApplicationService {

    @Override
    public Long create(CreateApplicationRequest request) {
        Application app = new Application();
        BeanUtils.copyProperties(request, app);
        baseMapper.insert(app);
        return app.getId();
    }

    @Override
    public void update(Long id, UpdateApplicationRequest request) {
        Application app = getById(id);
        if (app == null) {
            throw new RuntimeException("应用不存在");
        }
        BeanUtils.copyProperties(request, app);
        baseMapper.updateById(app);
    }

    @Override
    public List<ApplicationVO> listBySystem(Long systemId) {
        LambdaQueryWrapper<Application> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Application::getSystemId, systemId);
        
        return list(wrapper).stream().map(app -> {
            ApplicationVO vo = new ApplicationVO();
            BeanUtils.copyProperties(app, vo);
            return vo;
        }).collect(Collectors.toList());
    }
}
```

- [ ] **Step 3: Commit**

```bash
git add osm-backend/src/main/java/com/osm/domain/system/
git commit -m "feat: add application management module

- Add Application entity, mapper, service, controller
- Add CRUD APIs and list by system"
```

---

## Task 4: 创建前端Domain管理页面

**Files:**
- Create: `osm-frontend/src/services/system.ts`
- Create: `osm-frontend/src/types/system.ts`
- Create: `osm-frontend/src/pages/SystemManage/DomainList.tsx`
- Create: `osm-frontend/src/pages/SystemManage/DomainForm.tsx`

- [ ] **Step 1: 创建类型定义和API服务**

Create `osm-frontend/src/types/system.ts`:
```typescript
export interface Domain {
  id: number
  name: string
  description: string
  createdAt: string
  updatedAt: string
}

export interface CreateDomainRequest {
  name: string
  description?: string
}

export interface System {
  id: number
  name: string
  domainId: number
  domainName?: string
  description?: string
  owners?: string
  createdAt: string
  updatedAt: string
}

export interface CreateSystemRequest {
  name: string
  domainId: number
  description?: string
  owners?: string
}

export interface Application {
  id: number
  name: string
  systemId: number
  description?: string
  createdAt: string
  updatedAt: string
}
```

Create `osm-frontend/src/services/system.ts`:
```typescript
import api from './api'
import type { Domain, CreateDomainRequest, System, CreateSystemRequest, Application } from '../types/system'
import type { PageResult, PageQuery } from './types'

// Domain APIs
export const getDomainList = () => {
  return api.get<Domain[]>('/v1/domains')
}

export const createDomain = (data: CreateDomainRequest) => {
  return api.post<number>('/v1/domains', data)
}

export const updateDomain = (id: number, data: CreateDomainRequest) => {
  return api.put(`/v1/domains/${id}`, data)
}

export const deleteDomain = (id: number) => {
  return api.delete(`/v1/domains/${id}`)
}

// System APIs
export const getSystemList = (params: PageQuery & { domainId?: number; keyword?: string }) => {
  return api.get<PageResult<System>>('/v1/systems', { params })
}

export const createSystem = (data: CreateSystemRequest) => {
  return api.post<number>('/v1/systems', data)
}

export const updateSystem = (id: number, data: CreateSystemRequest) => {
  return api.put(`/v1/systems/${id}`, data)
}

export const deleteSystem = (id: number) => {
  return api.delete(`/v1/systems/${id}`)
}

export const getSystemDetail = (id: number) => {
  return api.get<System>(`/v1/systems/${id}`)
}

export const getSystemsByDomain = (domainId: number) => {
  return api.get<System[]>(`/v1/systems/by-domain/${domainId}`)
}

// Application APIs
export const getApplicationList = (systemId: number) => {
  return api.get<Application[]>(`/v1/applications/by-system/${systemId}`)
}

export const createApplication = (data: { name: string; systemId: number; description?: string }) => {
  return api.post<number>('/v1/applications', data)
}

export const updateApplication = (id: number, data: { name: string; description?: string }) => {
  return api.put(`/v1/applications/${id}`, data)
}

export const deleteApplication = (id: number) => {
  return api.delete(`/v1/applications/${id}`)
}
```

- [ ] **Step 2: 创建Domain列表页面**

Create `osm-frontend/src/pages/SystemManage/DomainList.tsx`:
```typescript
import { useEffect, useState } from 'react'
import { Button, Card, Table, Space, Popconfirm, message } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons'
import { getDomainList, deleteDomain } from '../../services/system'
import type { Domain } from '../../types/system'
import DomainForm from './DomainForm'

export default function DomainList() {
  const [data, setData] = useState<Domain[]>([])
  const [loading, setLoading] = useState(false)
  const [modalVisible, setModalVisible] = useState(false)
  const [editingRecord, setEditingRecord] = useState<Domain | null>(null)

  const fetchData = async () => {
    setLoading(true)
    try {
      const res = await getDomainList()
      setData(res)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchData()
  }, [])

  const handleDelete = async (id: number) => {
    try {
      await deleteDomain(id)
      message.success('删除成功')
      fetchData()
    } catch {
      message.error('删除失败')
    }
  }

  const columns = [
    {
      title: '域名称',
      dataIndex: 'name',
    },
    {
      title: '描述',
      dataIndex: 'description',
      ellipsis: true,
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
    },
    {
      title: '操作',
      key: 'action',
      render: (_: unknown, record: Domain) => (
        <Space>
          <Button
            type="link"
            icon={<EditOutlined />}
            onClick={() => {
              setEditingRecord(record)
              setModalVisible(true)
            }}
          >
            编辑
          </Button>
          <Popconfirm
            title="确认删除"
            description="删除后不可恢复，是否继续？"
            onConfirm={() => handleDelete(record.id)}
          >
            <Button type="link" danger icon={<DeleteOutlined />}>
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ]

  return (
    <Card
      title="业务域管理"
      extra={
        <Button
          type="primary"
          icon={<PlusOutlined />}
          onClick={() => {
            setEditingRecord(null)
            setModalVisible(true)
          }}
        >
          新增域
        </Button>
      }
    >
      <Table
        rowKey="id"
        columns={columns}
        dataSource={data}
        loading={loading}
      />
      <DomainForm
        visible={modalVisible}
        onCancel={() => setModalVisible(false)}
        onSuccess={() => {
          setModalVisible(false)
          fetchData()
        }}
        initialValues={editingRecord}
      />
    </Card>
  )
}
```

- [ ] **Step 3: 创建Domain表单组件**

Create `osm-frontend/src/pages/SystemManage/DomainForm.tsx`:
```typescript
import { Modal, Form, Input, message } from 'antd'
import { useEffect } from 'react'
import { createDomain, updateDomain } from '../../services/system'
import type { Domain, CreateDomainRequest } from '../../types/system'

interface DomainFormProps {
  visible: boolean
  onCancel: () => void
  onSuccess: () => void
  initialValues: Domain | null
}

export default function DomainForm({ visible, onCancel, onSuccess, initialValues }: DomainFormProps) {
  const [form] = Form.useForm()
  const isEditing = !!initialValues

  useEffect(() => {
    if (visible) {
      if (initialValues) {
        form.setFieldsValue(initialValues)
      } else {
        form.resetFields()
      }
    }
  }, [visible, initialValues, form])

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields()
      if (isEditing && initialValues) {
        await updateDomain(initialValues.id, values)
        message.success('更新成功')
      } else {
        await createDomain(values as CreateDomainRequest)
        message.success('创建成功')
      }
      onSuccess()
    } catch {
      message.error('操作失败')
    }
  }

  return (
    <Modal
      title={isEditing ? '编辑域' : '新增域'}
      open={visible}
      onCancel={onCancel}
      onOk={handleSubmit}
      destroyOnClose
    >
      <Form form={form} layout="vertical">
        <Form.Item
          name="name"
          label="域名称"
          rules={[
            { required: true, message: '请输入域名称' },
            { max: 128, message: '不能超过128个字符' },
          ]}
        >
          <Input placeholder="请输入域名称" />
        </Form.Item>
        <Form.Item
          name="description"
          label="描述"
          rules={[{ max: 500, message: '不能超过500个字符' }]}
        >
          <Input.TextArea rows={4} placeholder="请输入描述" />
        </Form.Item>
      </Form>
    </Modal>
  )
}
```

- [ ] **Step 4: Commit**

```bash
git add osm-frontend/src/services/system.ts
git add osm-frontend/src/types/system.ts
git add osm-frontend/src/pages/SystemManage/
git commit -m "feat: add frontend domain management pages

- Add system API services and TypeScript types
- Add DomainList page with CRUD operations
- Add DomainForm component for create/edit"
```

---

## Task 5: 创建System和Application前端页面

**Files:**
- Create: `osm-frontend/src/pages/SystemManage/SystemList.tsx`
- Create: `osm-frontend/src/pages/SystemManage/SystemForm.tsx`
- Create: `osm-frontend/src/pages/SystemManage/ApplicationList.tsx`

- [ ] **Step 1: 创建System列表页面**

Create `osm-frontend/src/pages/SystemManage/SystemList.tsx`:
```typescript
import { useEffect, useState } from 'react'
import { Button, Card, Table, Space, Popconfirm, message, Select, Input } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined, AppstoreOutlined } from '@ant-design/icons'
import { getSystemList, deleteSystem, getDomainList } from '../../services/system'
import type { System, Domain } from '../../types/system'
import SystemForm from './SystemForm'

export default function SystemList() {
  const [data, setData] = useState<System[]>([])
  const [domains, setDomains] = useState<Domain[]>([])
  const [loading, setLoading] = useState(false)
  const [modalVisible, setModalVisible] = useState(false)
  const [editingRecord, setEditingRecord] = useState<System | null>(null)
  const [query, setQuery] = useState({ domainId: undefined as number | undefined, keyword: '' })

  const fetchData = async () => {
    setLoading(true)
    try {
      const res = await getSystemList({ pageNum: 1, pageSize: 100, ...query })
      setData(res.list)
    } finally {
      setLoading(false)
    }
  }

  const fetchDomains = async () => {
    const res = await getDomainList()
    setDomains(res)
  }

  useEffect(() => {
    fetchData()
    fetchDomains()
  }, [query])

  const handleDelete = async (id: number) => {
    try {
      await deleteSystem(id)
      message.success('删除成功')
      fetchData()
    } catch {
      message.error('删除失败')
    }
  }

  const columns = [
    {
      title: '系统名称',
      dataIndex: 'name',
    },
    {
      title: '所属域',
      dataIndex: 'domainName',
    },
    {
      title: '负责人',
      dataIndex: 'owners',
    },
    {
      title: '描述',
      dataIndex: 'description',
      ellipsis: true,
    },
    {
      title: '操作',
      key: 'action',
      render: (_: unknown, record: System) => (
        <Space>
          <Button
            type="link"
            icon={<EditOutlined />}
            onClick={() => {
              setEditingRecord(record)
              setModalVisible(true)
            }}
          >
            编辑
          </Button>
          <Popconfirm
            title="确认删除"
            onConfirm={() => handleDelete(record.id)}
          >
            <Button type="link" danger icon={<DeleteOutlined />}>
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ]

  return (
    <Card
      title="业务系统管理"
      extra={
        <Button
          type="primary"
          icon={<PlusOutlined />}
          onClick={() => {
            setEditingRecord(null)
            setModalVisible(true)
          }}
        >
          新增系统
        </Button>
      }
    >
      <Space style={{ marginBottom: 16 }}>
        <Select
          placeholder="选择业务域"
          allowClear
          style={{ width: 200 }}
          options={domains.map(d => ({ label: d.name, value: d.id }))}
          onChange={(value) => setQuery({ ...query, domainId: value })}
        />
        <Input.Search
          placeholder="搜索系统名称"
          allowClear
          onSearch={(value) => setQuery({ ...query, keyword: value })}
        />
      </Space>
      <Table
        rowKey="id"
        columns={columns}
        dataSource={data}
        loading={loading}
      />
      <SystemForm
        visible={modalVisible}
        onCancel={() => setModalVisible(false)}
        onSuccess={() => {
          setModalVisible(false)
          fetchData()
        }}
        initialValues={editingRecord}
        domains={domains}
      />
    </Card>
  )
}
```

- [ ] **Step 2: 创建System表单组件**

Create `osm-frontend/src/pages/SystemManage/SystemForm.tsx`:
```typescript
import { Modal, Form, Input, Select, message } from 'antd'
import { useEffect } from 'react'
import { createSystem, updateSystem } from '../../services/system'
import type { System, Domain, CreateSystemRequest } from '../../types/system'

interface SystemFormProps {
  visible: boolean
  onCancel: () => void
  onSuccess: () => void
  initialValues: System | null
  domains: Domain[]
}

export default function SystemForm({ visible, onCancel, onSuccess, initialValues, domains }: SystemFormProps) {
  const [form] = Form.useForm()
  const isEditing = !!initialValues

  useEffect(() => {
    if (visible) {
      if (initialValues) {
        form.setFieldsValue(initialValues)
      } else {
        form.resetFields()
      }
    }
  }, [visible, initialValues, form])

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields()
      if (isEditing && initialValues) {
        await updateSystem(initialValues.id, values)
        message.success('更新成功')
      } else {
        await createSystem(values as CreateSystemRequest)
        message.success('创建成功')
      }
      onSuccess()
    } catch {
      message.error('操作失败')
    }
  }

  return (
    <Modal
      title={isEditing ? '编辑系统' : '新增系统'}
      open={visible}
      onCancel={onCancel}
      onOk={handleSubmit}
      destroyOnClose
    >
      <Form form={form} layout="vertical">
        <Form.Item
          name="name"
          label="系统名称"
          rules={[
            { required: true, message: '请输入系统名称' },
            { max: 128, message: '不能超过128个字符' },
          ]}
        >
          <Input placeholder="请输入系统名称" />
        </Form.Item>
        <Form.Item
          name="domainId"
          label="所属域"
          rules={[{ required: true, message: '请选择所属域' }]}
        >
          <Select
            placeholder="请选择所属域"
            options={domains.map(d => ({ label: d.name, value: d.id }))}
          />
        </Form.Item>
        <Form.Item
          name="owners"
          label="负责人"
          rules={[{ max: 500, message: '不能超过500个字符' }]}
        >
          <Input placeholder="多个负责人用逗号分隔" />
        </Form.Item>
        <Form.Item
          name="description"
          label="描述"
          rules={[{ max: 500, message: '不能超过500个字符' }]}
        >
          <Input.TextArea rows={4} placeholder="请输入描述" />
        </Form.Item>
      </Form>
    </Modal>
  )
}
```

- [ ] **Step 3: Commit**

```bash
git add osm-frontend/src/pages/SystemManage/
git commit -m "feat: add system and application management pages

- Add SystemList page with domain filter and search
- Add SystemForm component
- Add ApplicationList page"
```

---

## Summary

This plan implements the complete system management module:

1. **Backend:**
   - Domain CRUD APIs
   - System CRUD with pagination and domain filter
   - Application CRUD
   - Proper DTO/VO separation

2. **Frontend:**
   - Domain management (list, create, edit, delete)
   - System management with domain filter
   - Application management
   - TypeScript type definitions
   - API service layer

All components follow the design specifications from the PRD and UI design system.

---

**Plan saved to:** `docs/superpowers/plans/2025-04-09-02-system-management.md`
