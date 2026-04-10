package com.osm.domain.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.osm.domain.system.dto.CreateSystemRequest;
import com.osm.domain.system.dto.UpdateSystemRequest;
import com.osm.domain.system.entity.System;
import com.osm.domain.system.mapper.SystemMapper;
import com.osm.domain.system.service.SystemService;
import com.osm.domain.system.vo.SystemVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SystemServiceImpl extends ServiceImpl<SystemMapper, System> implements SystemService {

    @Override
    public Long create(CreateSystemRequest request) {
        System sys = new System();
        BeanUtils.copyProperties(request, sys);
        baseMapper.insert(sys);
        return sys.getId();
    }

    @Override
    public void update(Long id, UpdateSystemRequest request) {
        System sys = getById(id);
        if (sys == null) {
            throw new RuntimeException("系统不存在");
        }
        BeanUtils.copyProperties(request, sys);
        baseMapper.updateById(sys);
    }

    @Override
    public SystemVO getDetail(Long id) {
        System sys = getById(id);
        if (sys == null) {
            return null;
        }
        SystemVO vo = new SystemVO();
        BeanUtils.copyProperties(sys, vo);
        return vo;
    }

    @Override
    public List<SystemVO> listAll() {
        return list().stream().map(sys -> {
            SystemVO vo = new SystemVO();
            BeanUtils.copyProperties(sys, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<SystemVO> listByDomain(Long domainId) {
        return list().stream()
            .filter(sys -> domainId.equals(sys.getDomainId()))
            .map(sys -> {
                SystemVO vo = new SystemVO();
                BeanUtils.copyProperties(sys, vo);
                return vo;
            }).collect(Collectors.toList());
    }
}
