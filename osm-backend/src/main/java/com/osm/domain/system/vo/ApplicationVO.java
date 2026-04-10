package com.osm.domain.system.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ApplicationVO {
    private Long id;
    private String name;
    private String description;
    private Long systemId;
    private String code;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
