package com.osm.role.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.osm.permission.entity.Permission;
import com.osm.permission.mapper.PermissionMapper;
import com.osm.role.dto.CreateRoleRequest;
import com.osm.role.dto.UpdateRoleRequest;
import com.osm.role.entity.Role;
import com.osm.role.entity.RolePermission;
import com.osm.role.mapper.RoleMapper;
import com.osm.role.mapper.RolePermissionMapper;
import com.osm.role.service.RoleService;
import com.osm.role.vo.RoleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    private final RolePermissionMapper rolePermissionMapper;
    private final PermissionMapper permissionMapper;

    @Override
    public Long create(CreateRoleRequest request) {
        // Check if code exists
        Role existing = baseMapper.selectOne(
                new LambdaQueryWrapper<Role>()
                        .eq(Role::getCode, request.getCode())
                        .eq(Role::getDeleted, 0)
        );
        if (existing != null) {
            throw new RuntimeException("角色代码已存在");
        }

        Role role = new Role();
        BeanUtils.copyProperties(request, role);
        baseMapper.insert(role);
        return role.getId();
    }

    @Override
    public void update(Long id, UpdateRoleRequest request) {
        Role role = baseMapper.selectById(id);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }

        if (request.getName() != null) {
            role.setName(request.getName());
        }
        if (request.getDescription() != null) {
            role.setDescription(request.getDescription());
        }

        baseMapper.updateById(role);
    }

    @Override
    public RoleVO getDetail(Long id) {
        Role role = baseMapper.selectById(id);
        if (role == null) {
            return null;
        }

        RoleVO vo = new RoleVO();
        BeanUtils.copyProperties(role, vo);

        // Get permissions
        vo.setPermissions(getRolePermissions(id));

        return vo;
    }

    @Override
    public List<RoleVO> listAll() {
        List<Role> roles = baseMapper.selectList(
                new LambdaQueryWrapper<Role>()
                        .eq(Role::getDeleted, 0)
                        .orderByDesc(Role::getCreatedAt)
        );

        return roles.stream().map(role -> {
            RoleVO vo = new RoleVO();
            BeanUtils.copyProperties(role, vo);
            vo.setPermissions(getRolePermissions(role.getId()));
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        Role role = baseMapper.selectById(id);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }
        baseMapper.deleteById(id);

        // Delete role-permission relations
        rolePermissionMapper.delete(
                new LambdaQueryWrapper<RolePermission>()
                        .eq(RolePermission::getRoleId, id)
        );
    }

    @Override
    @Transactional
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        // Delete existing relations
        rolePermissionMapper.delete(
                new LambdaQueryWrapper<RolePermission>()
                        .eq(RolePermission::getRoleId, roleId)
        );

        // Add new relations
        for (Long permissionId : permissionIds) {
            RolePermission rp = new RolePermission();
            rp.setRoleId(roleId);
            rp.setPermissionId(permissionId);
            rolePermissionMapper.insert(rp);
        }
    }

    private List<String> getRolePermissions(Long roleId) {
        List<String> permissions = new ArrayList<>();
        List<RolePermission> rolePermissions = rolePermissionMapper.selectList(
                new LambdaQueryWrapper<RolePermission>()
                        .eq(RolePermission::getRoleId, roleId)
                        .eq(RolePermission::getDeleted, 0)
        );

        for (RolePermission rp : rolePermissions) {
            Permission permission = permissionMapper.selectById(rp.getPermissionId());
            if (permission != null && permission.getDeleted() == 0) {
                permissions.add(permission.getCode());
            }
        }
        return permissions;
    }
}
