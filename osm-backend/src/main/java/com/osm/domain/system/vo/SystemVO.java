package com.osm.domain.system.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SystemVO {
    private Long id;
    private String name;
    private String description;
    private Long domainId;
    private String code;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
