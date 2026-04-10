package com.osm.permission.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.osm.domain.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("osm_permission")
public class Permission extends BaseEntity {
    private String code;
    private String name;
    private String description;
}
