package com.osm.domain.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.osm.domain.system.entity.Application;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ApplicationMapper extends BaseMapper<Application> {
}
