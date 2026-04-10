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
    ApplicationVO getDetail(Long id);
    List<ApplicationVO> listAll();
    List<ApplicationVO> listBySystem(Long systemId);
}
