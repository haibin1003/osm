package com.osm.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.osm.domain.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("osm_user")
public class User extends BaseEntity {
    private String username;
    private String password;
    private String email;
    private Integer status;
}
