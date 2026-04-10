package com.osm.domain.system.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DomainVO {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
