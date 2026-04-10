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
