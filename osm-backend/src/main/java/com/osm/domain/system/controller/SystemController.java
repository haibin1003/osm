package com.osm.domain.system.controller;

import com.osm.common.result.Result;
import com.osm.domain.system.dto.CreateSystemRequest;
import com.osm.domain.system.dto.UpdateSystemRequest;
import com.osm.domain.system.service.SystemService;
import com.osm.domain.system.vo.SystemVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/systems")
@RequiredArgsConstructor
public class SystemController {

    private final SystemService systemService;

    @PostMapping
    public Result<Long> create(@Valid @RequestBody CreateSystemRequest request) {
        Long id = systemService.create(request);
        return Result.success(id);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody UpdateSystemRequest request) {
        systemService.update(id, request);
        return Result.success(null);
    }

    @GetMapping("/{id}")
    public Result<SystemVO> getDetail(@PathVariable Long id) {
        SystemVO vo = systemService.getDetail(id);
        if (vo == null) {
            return Result.error("系统不存在");
        }
        return Result.success(vo);
    }

    @GetMapping
    public Result<List<SystemVO>> listAll() {
        return Result.success(systemService.listAll());
    }

    @GetMapping("/domain/{domainId}")
    public Result<List<SystemVO>> listByDomain(@PathVariable Long domainId) {
        return Result.success(systemService.listByDomain(domainId));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        systemService.removeById(id);
        return Result.success(null);
    }
}
