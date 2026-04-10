package com.osm.domain.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.osm.domain.system.dto.CreateSystemRequest;
import com.osm.domain.system.dto.UpdateSystemRequest;
import com.osm.domain.system.entity.System;
import com.osm.domain.system.vo.SystemVO;

import java.util.List;

public interface SystemService extends IService<System> {
    Long create(CreateSystemRequest request);
    void update(Long id, UpdateSystemRequest request);
    SystemVO getDetail(Long id);
    List<SystemVO> listAll();
    List<SystemVO> listByDomain(Long domainId);
}
