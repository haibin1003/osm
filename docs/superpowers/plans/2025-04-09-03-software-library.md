# OSM 开源软件库模块实施计划

> **For agentic workers:** REQUIRED SUB-TOOL: Use subagent-driven-development or executing-plans

**Goal:** 实现开源软件库管理功能，包括软件(Software)和版本(Version)的CRUD，支持技术类型和许可证双维度分类。

**Architecture:** 与系统管理模块类似的分层架构，增加枚举类型管理。

**Tech Stack:** Spring Boot, MyBatis-Plus, React, Ant Design

---

## 文件结构

```
osm-backend/
└── src/main/java/com/osm/domain/software/
    ├── entity/
    │   ├── Software.java
    │   └── SoftwareVersion.java
    ├── mapper/
    │   ├── SoftwareMapper.java
    │   └── SoftwareVersionMapper.java
    ├── service/
    │   ├── SoftwareService.java
    │   ├── SoftwareVersionService.java
    │   └── impl/
    │       ├── SoftwareServiceImpl.java
    │       └── SoftwareVersionServiceImpl.java
    ├── dto/
    │   ├── CreateSoftwareRequest.java
    │   ├── UpdateSoftwareRequest.java
    │   ├── CreateVersionRequest.java
    │   └── SoftwareQuery.java
    ├── vo/
    │   ├── SoftwareVO.java
    │   ├── SoftwareDetailVO.java
    │   └── VersionVO.java
    ├── enums/
    │   ├── TechCategory.java
    │   ├── LicenseType.java
    │   └── SoftwareStatus.java
    └── controller/
        └── SoftwareController.java

osm-frontend/
└── src/pages/SoftwareLibrary/
    ├── SoftwareList.tsx
    ├── SoftwareForm.tsx
    ├── SoftwareDetail.tsx
    └── VersionList.tsx
```

---

## Task 1: 创建枚举类型

**Files:**
- Create: `osm-backend/src/main/java/com/osm/domain/software/enums/TechCategory.java`
- Create: `osm-backend/src/main/java/com/osm/domain/software/enums/LicenseType.java`
- Create: `osm-backend/src/main/java/com/osm/domain/software/enums/SoftwareStatus.java`
- Create: `osm-backend/src/main/java/com/osm/domain/software/enums/VersionStatus.java`

- [ ] **Step 1: 创建技术类型枚举**

Create `osm-backend/src/main/java/com/osm/domain/software/enums/TechCategory.java`:
```java
package com.osm.domain.software.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum TechCategory {
    DATABASE("DATABASE", "数据库"),
    CACHE("CACHE", "缓存"),
    MESSAGE_QUEUE("MESSAGE_QUEUE", "消息队列"),
    WEB_FRAMEWORK("WEB_FRAMEWORK", "Web框架"),
    SEARCH_ENGINE("SEARCH_ENGINE", "搜索引擎"),
    LOAD_BALANCER("LOAD_BALANCER", "负载均衡"),
    MONITORING("MONITORING", "监控"),
    TOOL("TOOL", "工具"),
    OTHER("OTHER", "其他");

    @EnumValue
    private final String code;
    
    @JsonValue
    private final String label;

    TechCategory(String code, String label) {
        this.code = code;
        this.label = label;
    }
}
```

- [ ] **Step 2: 创建许可证类型枚举**

Create `osm-backend/src/main/java/com/osm/domain/software/enums/LicenseType.java`:
```java
package com.osm.domain.software.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum LicenseType {
    MIT("MIT", "MIT"),
    APACHE_2("APACHE_2", "Apache-2.0"),
    GPL_2("GPL_2", "GPL-2.0"),
    GPL_3("GPL_3", "GPL-3.0"),
    BSD_2("BSD_2", "BSD-2-Clause"),
    BSD_3("BSD_3", "BSD-3-Clause"),
    LGPL("LGPL", "LGPL"),
    OTHER("OTHER", "其他");

    @EnumValue
    private final String code;
    
    @JsonValue
    private final String label;

    LicenseType(String code, String label) {
        this.code = code;
        this.label = label;
    }
}
```

- [ ] **Step 3: 创建状态和提交**

Create `osm-backend/src/main/java/com/osm/domain/software/enums/SoftwareStatus.java`:
```java
package com.osm.domain.software.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum SoftwareStatus {
    DRAFT(0, "草稿"),
    PUBLISHED(1, "已发布"),
    OFFLINE(2, "已下线");

    @EnumValue
    private final Integer code;
    
    @JsonValue
    private final String label;

    SoftwareStatus(Integer code, String label) {
        this.code = code;
        this.label = label;
    }
}
```

```bash
git add osm-backend/src/main/java/com/osm/domain/software/enums/
git commit -m "feat: add software library enums

- Add TechCategory enum for software categorization
- Add LicenseType enum for license management
- Add SoftwareStatus enum for status tracking"
```

---

## Task 2: 创建Software实体和基础CRUD

**Files:**
- Create: `osm-backend/src/main/java/com/osm/domain/software/entity/Software.java`
- Create: `osm-backend/src/main/java/com/osm/domain/software/mapper/SoftwareMapper.java`
- Create: `osm-backend/src/main/java/com/osm/domain/software/service/SoftwareService.java`
- Create: `osm-backend/src/main/java/com/osm/domain/software/service/impl/SoftwareServiceImpl.java`
- Create: `osm-backend/src/main/java/com/osm/domain/software/controller/SoftwareController.java`

- [ ] **Step 1: 创建Software实体类**

Create `osm-backend/src/main/java/com/osm/domain/software/entity/Software.java`:
```java
package com.osm.domain.software.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.osm.domain.base.BaseEntity;
import com.osm.domain.software.enums.LicenseType;
import com.osm.domain.software.enums.SoftwareStatus;
import com.osm.domain.software.enums.TechCategory;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("osm_software")
public class Software extends BaseEntity {
    private String name;
    private TechCategory techCategory;
    private LicenseType licenseType;
    private String description;
    private String docUrl;
    private SoftwareStatus status;
}
```

- [ ] **Step 2: 创建DTO和VO**

Create `osm-backend/src/main/java/com/osm/domain/software/dto/CreateSoftwareRequest.java`:
```java
package com.osm.domain.software.dto;

import com.osm.domain.software.enums.LicenseType;
import com.osm.domain.software.enums.TechCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateSoftwareRequest {
    @NotBlank(message = "软件名称不能为空")
    @Size(max = 128, message = "软件名称不能超过128个字符")
    private String name;
    
    @NotNull(message = "技术类型不能为空")
    private TechCategory techCategory;
    
    @NotNull(message = "许可证类型不能为空")
    private LicenseType licenseType;
    
    @Size(max = 2000, message = "描述不能超过2000个字符")
    private String description;
    
    @Size(max = 500, message = "文档链接不能超过500个字符")
    private String docUrl;
}
```

Create `osm-backend/src/main/java/com/osm/domain/software/dto/SoftwareQuery.java`:
```java
package com.osm.domain.software.dto;

import com.osm.domain.software.enums.LicenseType;
import com.osm.domain.software.enums.TechCategory;
import lombok.Data;

@Data
public class SoftwareQuery {
    private Integer pageNum = 1;
    private Integer pageSize = 20;
    private TechCategory techCategory;
    private LicenseType licenseType;
    private String keyword;
    private Integer status;
}
```

- [ ] **Step 3: 实现Service和Controller**

Create `osm-backend/src/main/java/com/osm/domain/software/service/SoftwareService.java`:
```java
package com.osm.domain.software.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.osm.domain.software.dto.CreateSoftwareRequest;
import com.osm.domain.software.dto.SoftwareQuery;
import com.osm.domain.software.dto.UpdateSoftwareRequest;
import com.osm.domain.software.entity.Software;
import com.osm.domain.software.vo.SoftwareDetailVO;
import com.osm.domain.software.vo.SoftwareVO;

import java.util.List;

public interface SoftwareService extends IService<Software> {
    Long create(CreateSoftwareRequest request);
    void update(Long id, UpdateSoftwareRequest request);
    SoftwareDetailVO getDetail(Long id);
    IPage<SoftwareVO> queryPage(SoftwareQuery query);
    List<SoftwareVO> listPublished();
}
```

Create `osm-backend/src/main/java/com/osm/domain/software/service/impl/SoftwareServiceImpl.java`:
```java
package com.osm.domain.software.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.osm.domain.software.dto.CreateSoftwareRequest;
import com.osm.domain.software.dto.SoftwareQuery;
import com.osm.domain.software.dto.UpdateSoftwareRequest;
import com.osm.domain.software.entity.Software;
import com.osm.domain.software.enums.SoftwareStatus;
import com.osm.domain.software.mapper.SoftwareMapper;
import com.osm.domain.software.service.SoftwareService;
import com.osm.domain.software.vo.SoftwareDetailVO;
import com.osm.domain.software.vo.SoftwareVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SoftwareServiceImpl extends ServiceImpl<SoftwareMapper, Software> implements SoftwareService {

    @Override
    public Long create(CreateSoftwareRequest request) {
        Software software = new Software();
        BeanUtils.copyProperties(request, software);
        software.setStatus(SoftwareStatus.DRAFT);
        baseMapper.insert(software);
        return software.getId();
    }

    @Override
    public void update(Long id, UpdateSoftwareRequest request) {
        Software software = getById(id);
        if (software == null) {
            throw new RuntimeException("软件不存在");
        }
        BeanUtils.copyProperties(request, software);
        baseMapper.updateById(software);
    }

    @Override
    public SoftwareDetailVO getDetail(Long id) {
        Software software = getById(id);
        if (software == null) {
            return null;
        }
        SoftwareDetailVO vo = new SoftwareDetailVO();
        BeanUtils.copyProperties(software, vo);
        return vo;
    }

    @Override
    public IPage<SoftwareVO> queryPage(SoftwareQuery query) {
        Page<Software> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Software> wrapper = new LambdaQueryWrapper<>();
        
        if (query.getTechCategory() != null) {
            wrapper.eq(Software::getTechCategory, query.getTechCategory());
        }
        if (query.getLicenseType() != null) {
            wrapper.eq(Software::getLicenseType, query.getLicenseType());
        }
        if (query.getStatus() != null) {
            wrapper.eq(Software::getStatus, query.getStatus());
        }
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(Software::getName, query.getKeyword());
        }
        
        IPage<Software> softwarePage = baseMapper.selectPage(page, wrapper);
        return softwarePage.convert(s -> {
            SoftwareVO vo = new SoftwareVO();
            BeanUtils.copyProperties(s, vo);
            return vo;
        });
    }

    @Override
    public List<SoftwareVO> listPublished() {
        LambdaQueryWrapper<Software> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Software::getStatus, SoftwareStatus.PUBLISHED);
        
        return list(wrapper).stream().map(s -> {
            SoftwareVO vo = new SoftwareVO();
            BeanUtils.copyProperties(s, vo);
            return vo;
        }).collect(Collectors.toList());
    }
}
```

- [ ] **Step 4: 提交**

```bash
git add osm-backend/src/main/java/com/osm/domain/software/
git commit -m "feat: add software library backend

- Add Software entity with enums
- Add CRUD APIs with category and license filters
- Add list published API for dropdown selection"
```

---

## Task 3: 创建Version（版本）管理

**Files:**
- Create: `osm-backend/src/main/java/com/osm/domain/software/entity/SoftwareVersion.java`
- Create: `osm-backend/src/main/java/com/osm/domain/software/mapper/SoftwareVersionMapper.java`
- Create: `osm-backend/src/main/java/com/osm/domain/software/service/SoftwareVersionService.java`

- [ ] **Step 1: 创建Version实体和Service**

Create `osm-backend/src/main/java/com/osm/domain/software/entity/SoftwareVersion.java`:
```java
package com.osm.domain.software.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.osm.domain.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("osm_software_version")
public class SoftwareVersion extends BaseEntity {
    private Long softwareId;
    private String version;
    private String description;
    private LocalDate releaseDate;
    private Integer status; // 0-正常, 1-存在漏洞, 2-已弃用
}
```

Create `osm-backend/src/main/java/com/osm/domain/software/service/SoftwareVersionService.java`:
```java
package com.osm.domain.software.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.osm.domain.software.entity.SoftwareVersion;
import com.osm.domain.software.vo.VersionVO;

import java.util.List;

public interface SoftwareVersionService extends IService<SoftwareVersion> {
    Long create(Long softwareId, String version, String description, String releaseDate);
    List<VersionVO> listBySoftware(Long softwareId);
}
```

- [ ] **Step 2: 提交**

```bash
git add osm-backend/src/main/java/com/osm/domain/software/
git commit -m "feat: add software version management

- Add SoftwareVersion entity
- Add version CRUD operations
- Add list versions by software API"
```

---

## Task 4: 创建前端软件库页面

**Files:**
- Create: `osm-frontend/src/types/software.ts`
- Create: `osm-frontend/src/services/software.ts`
- Create: `osm-frontend/src/pages/SoftwareLibrary/SoftwareList.tsx`
- Create: `osm-frontend/src/pages/SoftwareLibrary/SoftwareForm.tsx`

- [ ] **Step 1: 创建类型定义和API服务**

Create `osm-frontend/src/types/software.ts`:
```typescript
export enum TechCategory {
  DATABASE = 'DATABASE',
  CACHE = 'CACHE',
  MESSAGE_QUEUE = 'MESSAGE_QUEUE',
  WEB_FRAMEWORK = 'WEB_FRAMEWORK',
  SEARCH_ENGINE = 'SEARCH_ENGINE',
  LOAD_BALANCER = 'LOAD_BALANCER',
  MONITORING = 'MONITORING',
  TOOL = 'TOOL',
  OTHER = 'OTHER',
}

export enum LicenseType {
  MIT = 'MIT',
  APACHE_2 = 'APACHE_2',
  GPL_2 = 'GPL_2',
  GPL_3 = 'GPL_3',
  BSD_2 = 'BSD_2',
  BSD_3 = 'BSD_3',
  LGPL = 'LGPL',
  OTHER = 'OTHER',
}

export interface Software {
  id: number
  name: string
  techCategory: TechCategory
  licenseType: LicenseType
  description?: string
  docUrl?: string
  status: number
  createdAt: string
  updatedAt: string
}

export interface CreateSoftwareRequest {
  name: string
  techCategory: TechCategory
  licenseType: LicenseType
  description?: string
  docUrl?: string
}

export const techCategoryOptions = [
  { label: '数据库', value: TechCategory.DATABASE },
  { label: '缓存', value: TechCategory.CACHE },
  { label: '消息队列', value: TechCategory.MESSAGE_QUEUE },
  { label: 'Web框架', value: TechCategory.WEB_FRAMEWORK },
  { label: '搜索引擎', value: TechCategory.SEARCH_ENGINE },
  { label: '负载均衡', value: TechCategory.LOAD_BALANCER },
  { label: '监控', value: TechCategory.MONITORING },
  { label: '工具', value: TechCategory.TOOL },
  { label: '其他', value: TechCategory.OTHER },
]

export const licenseTypeOptions = [
  { label: 'MIT', value: LicenseType.MIT },
  { label: 'Apache-2.0', value: LicenseType.APACHE_2 },
  { label: 'GPL-2.0', value: LicenseType.GPL_2 },
  { label: 'GPL-3.0', value: LicenseType.GPL_3 },
  { label: 'BSD-2-Clause', value: LicenseType.BSD_2 },
  { label: 'BSD-3-Clause', value: LicenseType.BSD_3 },
  { label: 'LGPL', value: LicenseType.LGPL },
  { label: '其他', value: LicenseType.OTHER },
]
```

Create `osm-frontend/src/services/software.ts`:
```typescript
import api from './api'
import type { Software, CreateSoftwareRequest } from '../types/software'
import type { PageResult, PageQuery } from './types'

export const getSoftwareList = (params: PageQuery & { 
  techCategory?: string
  licenseType?: string
  keyword?: string
  status?: number
}) => {
  return api.get<PageResult<Software>>('/v1/software', { params })
}

export const getPublishedSoftware = () => {
  return api.get<Software[]>('/v1/software/published')
}

export const createSoftware = (data: CreateSoftwareRequest) => {
  return api.post<number>('/v1/software', data)
}

export const updateSoftware = (id: number, data: CreateSoftwareRequest) => {
  return api.put(`/v1/software/${id}`, data)
}

export const deleteSoftware = (id: number) => {
  return api.delete(`/v1/software/${id}`)
}

export const getSoftwareDetail = (id: number) => {
  return api.get<Software>(`/v1/software/${id}`)
}
```

- [ ] **Step 2: 创建软件列表页面**

Create `osm-frontend/src/pages/SoftwareLibrary/SoftwareList.tsx`:
```typescript
import { useEffect, useState } from 'react'
import { Button, Card, Table, Space, Popconfirm, message, Select, Input, Tag } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons'
import { getSoftwareList, deleteSoftware } from '../../services/software'
import type { Software } from '../../types/software'
import { techCategoryOptions, licenseTypeOptions } from '../../types/software'
import SoftwareForm from './SoftwareForm'

export default function SoftwareList() {
  const [data, setData] = useState<Software[]>([])
  const [loading, setLoading] = useState(false)
  const [modalVisible, setModalVisible] = useState(false)
  const [editingRecord, setEditingRecord] = useState<Software | null>(null)
  const [query, setQuery] = useState({
    techCategory: undefined as string | undefined,
    licenseType: undefined as string | undefined,
    keyword: '',
  })

  const fetchData = async () => {
    setLoading(true)
    try {
      const res = await getSoftwareList({ pageNum: 1, pageSize: 100, ...query })
      setData(res.list)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchData()
  }, [query])

  const handleDelete = async (id: number) => {
    try {
      await deleteSoftware(id)
      message.success('删除成功')
      fetchData()
    } catch {
      message.error('删除失败')
    }
  }

  const getCategoryColor = (category: string) => {
    const colors: Record<string, string> = {
      DATABASE: 'blue',
      CACHE: 'cyan',
      MESSAGE_QUEUE: 'green',
      WEB_FRAMEWORK: 'purple',
    }
    return colors[category] || 'default'
  }

  const columns = [
    {
      title: '软件名称',
      dataIndex: 'name',
    },
    {
      title: '技术类型',
      dataIndex: 'techCategory',
      render: (value: string) => (
        <Tag color={getCategoryColor(value)}>
          {techCategoryOptions.find(o => o.value === value)?.label || value}
        </Tag>
      ),
    },
    {
      title: '许可证',
      dataIndex: 'licenseType',
      render: (value: string) => (
        licenseTypeOptions.find(o => o.value === value)?.label || value
      ),
    },
    {
      title: '状态',
      dataIndex: 'status',
      render: (value: number) => {
        const statusMap: Record<number, { text: string; color: string }> = {
          0: { text: '草稿', color: 'default' },
          1: { text: '已发布', color: 'success' },
          2: { text: '已下线', color: 'error' },
        }
        const status = statusMap[value]
        return <Tag color={status?.color}>{status?.text}</Tag>
      },
    },
    {
      title: '操作',
      key: 'action',
      render: (_: unknown, record: Software) => (
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
      title="开源软件库"
      extra={
        <Button
          type="primary"
          icon={<PlusOutlined />}
          onClick={() => {
            setEditingRecord(null)
            setModalVisible(true)
          }}
        >
          新增软件
        </Button>
      }
    >
      <Space style={{ marginBottom: 16 }}>
        <Select
          placeholder="技术类型"
          allowClear
          style={{ width: 150 }}
          options={techCategoryOptions}
          onChange={(value) => setQuery({ ...query, techCategory: value })}
        />
        <Select
          placeholder="许可证"
          allowClear
          style={{ width: 150 }}
          options={licenseTypeOptions}
          onChange={(value) => setQuery({ ...query, licenseType: value })}
        />
        <Input.Search
          placeholder="搜索软件名称"
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
      <SoftwareForm
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

- [ ] **Step 3: 提交**

```bash
git add osm-frontend/src/types/software.ts
git add osm-frontend/src/services/software.ts
git add osm-frontend/src/pages/SoftwareLibrary/
git commit -m "feat: add software library frontend

- Add software types and enums
- Add software API services
- Add SoftwareList page with category and license filters
- Add SoftwareForm component"
```

---

## Summary

This plan implements the complete software library module:

1. **Backend:**
   - Enum types for TechCategory, LicenseType, SoftwareStatus
   - Software CRUD with dual-dimension filtering
   - SoftwareVersion management
   - Published software list for dropdowns

2. **Frontend:**
   - Software list with category and license filters
   - Color-coded category tags
   - Status indicators
   - Full CRUD operations

**Plan saved to:** `docs/superpowers/plans/2025-04-09-03-software-library.md`
