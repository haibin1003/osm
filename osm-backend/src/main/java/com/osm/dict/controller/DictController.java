package com.osm.dict.controller;

import com.osm.common.result.Result;
import com.osm.dict.service.DictService;
import com.osm.dict.vo.DictVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dicts")
@RequiredArgsConstructor
public class DictController {

    private final DictService dictService;

    @GetMapping("/{type}")
    public Result<List<DictVO>> getByType(@PathVariable String type) {
        return Result.success(dictService.getByType(type));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('dict:write')")
    public Result<List<DictVO>> getAll() {
        return Result.success(dictService.getAllTypes());
    }
}
