package com.osm.domain.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateSystemRequest {
    @NotBlank(message = "系统名称不能为空")
    @Size(max = 128, message = "系统名称不能超过128个字符")
    private String name;

    @Size(max = 500, message = "描述不能超过500个字符")
    private String description;

    private Long domainId;

    @NotBlank(message = "系统编码不能为空")
    @Size(max = 64, message = "系统编码不能超过64个字符")
    private String code;
}
