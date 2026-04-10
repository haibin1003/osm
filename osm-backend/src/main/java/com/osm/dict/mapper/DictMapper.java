package com.osm.dict.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.osm.dict.entity.Dict;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DictMapper extends BaseMapper<Dict> {
}
