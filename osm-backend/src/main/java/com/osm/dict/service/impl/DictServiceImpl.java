package com.osm.dict.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.osm.dict.entity.Dict;
import com.osm.dict.mapper.DictMapper;
import com.osm.dict.service.DictService;
import com.osm.dict.vo.DictVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DictServiceImpl implements DictService {

    private final DictMapper dictMapper;

    @Override
    public List<DictVO> getByType(String type) {
        List<Dict> dicts = dictMapper.selectList(
                new LambdaQueryWrapper<Dict>()
                        .eq(Dict::getType, type)
                        .eq(Dict::getStatus, 1)
                        .eq(Dict::getDeleted, 0)
                        .orderByAsc(Dict::getSort)
        );

        return dicts.stream().map(dict -> {
            DictVO vo = new DictVO();
            BeanUtils.copyProperties(dict, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<DictVO> getAllTypes() {
        List<Dict> dicts = dictMapper.selectList(
                new LambdaQueryWrapper<Dict>()
                        .eq(Dict::getStatus, 1)
                        .eq(Dict::getDeleted, 0)
                        .orderByAsc(Dict::getType, Dict::getSort)
        );

        return dicts.stream().map(dict -> {
            DictVO vo = new DictVO();
            BeanUtils.copyProperties(dict, vo);
            return vo;
        }).collect(Collectors.toList());
    }
}
