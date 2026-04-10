package com.osm.domain.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.osm.domain.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("osm_application")
public class Application extends BaseEntity {
    private String name;
    private String description;
    private Long systemId;
    private String code;
}
