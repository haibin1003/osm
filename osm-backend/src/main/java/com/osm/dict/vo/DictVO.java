package com.osm.dict.vo;

import lombok.Data;

@Data
public class DictVO {
    private Long id;
    private String type;
    private String label;
    private String value;
    private Integer sort;
    private Integer status;
}
