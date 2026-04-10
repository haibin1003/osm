package com.osm.user.controller;

import com.osm.common.result.Result;
import com.osm.user.dto.CreateUserRequest;
import com.osm.user.dto.UpdateUserRequest;
import com.osm.user.service.UserService;
import com.osm.user.vo.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasAuthority('user:write')")
    public Result<Long> create(@Valid @RequestBody CreateUserRequest request) {
        Long id = userService.create(request);
        return Result.success(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('user:write')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        userService.update(id, request);
        return Result.success(null);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('user:read')")
    public Result<UserVO> getDetail(@PathVariable Long id) {
        UserVO vo = userService.getDetail(id);
        if (vo == null) {
            return Result.error("用户不存在");
        }
        return Result.success(vo);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('user:read')")
    public Result<List<UserVO>> listAll() {
        return Result.success(userService.listAll());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('user:write')")
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.success(null);
    }

    @PutMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('user:write')")
    public Result<Void> assignRoles(@PathVariable Long id, @RequestBody List<Long> roleIds) {
        userService.assignRoles(id, roleIds);
        return Result.success(null);
    }
}
