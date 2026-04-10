package com.osm.dict.service;

import com.osm.dict.vo.DictVO;

import java.util.List;

public interface DictService {
    List<DictVO> getByType(String type);
    List<DictVO> getAllTypes();
}
