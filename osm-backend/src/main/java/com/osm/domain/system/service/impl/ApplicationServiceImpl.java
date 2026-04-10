package com.osm.domain.system.service.impl;

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
    public ApplicationVO getDetail(Long id) {
        Application app = getById(id);
        if (app == null) {
            return null;
        }
        ApplicationVO vo = new ApplicationVO();
        BeanUtils.copyProperties(app, vo);
        return vo;
    }

    @Override
    public List<ApplicationVO> listAll() {
        return list().stream().map(app -> {
            ApplicationVO vo = new ApplicationVO();
            BeanUtils.copyProperties(app, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ApplicationVO> listBySystem(Long systemId) {
        return list().stream()
            .filter(app -> systemId.equals(app.getSystemId()))
            .map(app -> {
                ApplicationVO vo = new ApplicationVO();
                BeanUtils.copyProperties(app, vo);
                return vo;
            }).collect(Collectors.toList());
    }
}
