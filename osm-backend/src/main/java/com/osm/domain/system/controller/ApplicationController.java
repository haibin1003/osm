package com.osm.domain.system.controller;

import com.osm.common.result.Result;
import com.osm.domain.system.dto.CreateApplicationRequest;
import com.osm.domain.system.dto.UpdateApplicationRequest;
import com.osm.domain.system.service.ApplicationService;
import com.osm.domain.system.vo.ApplicationVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    public Result<Long> create(@Valid @RequestBody CreateApplicationRequest request) {
        Long id = applicationService.create(request);
        return Result.success(id);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody UpdateApplicationRequest request) {
        applicationService.update(id, request);
        return Result.success(null);
    }

    @GetMapping("/{id}")
    public Result<ApplicationVO> getDetail(@PathVariable Long id) {
        ApplicationVO vo = applicationService.getDetail(id);
        if (vo == null) {
            return Result.error("应用不存在");
        }
        return Result.success(vo);
    }

    @GetMapping
    public Result<List<ApplicationVO>> listAll() {
        return Result.success(applicationService.listAll());
    }

    @GetMapping("/system/{systemId}")
    public Result<List<ApplicationVO>> listBySystem(@PathVariable Long systemId) {
        return Result.success(applicationService.listBySystem(systemId));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        applicationService.removeById(id);
        return Result.success(null);
    }
}
