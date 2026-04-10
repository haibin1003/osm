package com.osm.domain.system.controller;

import com.osm.common.result.Result;
import com.osm.domain.system.dto.CreateDomainRequest;
import com.osm.domain.system.dto.UpdateDomainRequest;
import com.osm.domain.system.service.DomainService;
import com.osm.domain.system.vo.DomainVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/domains")
@RequiredArgsConstructor
public class DomainController {

    private final DomainService domainService;

    @PostMapping
    public Result<Long> create(@Valid @RequestBody CreateDomainRequest request) {
        Long id = domainService.create(request);
        return Result.success(id);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody UpdateDomainRequest request) {
        domainService.update(id, request);
        return Result.success(null);
    }

    @GetMapping("/{id}")
    public Result<DomainVO> getDetail(@PathVariable Long id) {
        DomainVO vo = domainService.getDetail(id);
        if (vo == null) {
            return Result.error("域不存在");
        }
        return Result.success(vo);
    }

    @GetMapping
    public Result<List<DomainVO>> listAll() {
        return Result.success(domainService.listAll());
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        domainService.removeById(id);
        return Result.success(null);
    }
}
