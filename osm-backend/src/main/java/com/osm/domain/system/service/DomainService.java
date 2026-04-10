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
