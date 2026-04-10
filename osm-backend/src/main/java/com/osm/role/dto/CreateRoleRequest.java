package com.osm.role.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateRoleRequest {
    @NotBlank(message = "角色代码不能为空")
    @Size(max = 32, message = "角色代码不能超过32个字符")
    private String code;

    @NotBlank(message = "角色名称不能为空")
    @Size(max = 64, message = "角色名称不能超过64个字符")
    private String name;

    @Size(max = 255, message = "角色描述不能超过255个字符")
    private String description;
}
