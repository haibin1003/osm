package com.osm.role.controller;

import com.osm.common.result.Result;
import com.osm.role.dto.CreateRoleRequest;
import com.osm.role.dto.UpdateRoleRequest;
import com.osm.role.service.RoleService;
import com.osm.role.vo.RoleVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    @PreAuthorize("hasAuthority('role:write')")
    public Result<Long> create(@Valid @RequestBody CreateRoleRequest request) {
        Long id = roleService.create(request);
        return Result.success(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('role:write')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody UpdateRoleRequest request) {
        roleService.update(id, request);
        return Result.success(null);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('role:read', 'role:write')")
    public Result<RoleVO> getDetail(@PathVariable Long id) {
        RoleVO vo = roleService.getDetail(id);
        if (vo == null) {
            return Result.error("角色不存在");
        }
        return Result.success(vo);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('role:read', 'role:write')")
    public Result<List<RoleVO>> listAll() {
        return Result.success(roleService.listAll());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('role:write')")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return Result.success(null);
    }

    @PutMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('role:write')")
    public Result<Void> assignPermissions(@PathVariable Long id, @RequestBody List<Long> permissionIds) {
        roleService.assignPermissions(id, permissionIds);
        return Result.success(null);
    }
}
