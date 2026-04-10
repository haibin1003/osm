package com.osm.domain.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.osm.domain.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("osm_system")
public class System extends BaseEntity {
    private String name;
    private String description;
    private Long domainId;
    private String code;
}
