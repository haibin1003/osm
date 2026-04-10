package com.osm.auth.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.osm.permission.entity.Permission;
import com.osm.permission.mapper.PermissionMapper;
import com.osm.role.entity.Role;
import com.osm.role.entity.RolePermission;
import com.osm.role.mapper.RoleMapper;
import com.osm.role.mapper.RolePermissionMapper;
import com.osm.user.entity.User;
import com.osm.user.entity.UserRole;
import com.osm.user.mapper.UserMapper;
import com.osm.user.mapper.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final PermissionMapper permissionMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, username)
                        .eq(User::getDeleted, 0)
        );

        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        // Get user roles
        List<UserRole> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRole>()
                        .eq(UserRole::getUserId, user.getId())
                        .eq(UserRole::getDeleted, 0)
        );

        List<String> roleCodes = new ArrayList<>();
        List<String> permissions = new ArrayList<>();

        for (UserRole ur : userRoles) {
            Role role = roleMapper.selectById(ur.getRoleId());
            if (role != null && role.getDeleted() == 0) {
                roleCodes.add(role.getCode());

                // Get role permissions
                List<RolePermission> rolePermissions = rolePermissionMapper.selectList(
                        new LambdaQueryWrapper<RolePermission>()
                                .eq(RolePermission::getRoleId, role.getId())
                                .eq(RolePermission::getDeleted, 0)
                );

                for (RolePermission rp : rolePermissions) {
                    Permission permission = permissionMapper.selectById(rp.getPermissionId());
                    if (permission != null && permission.getDeleted() == 0) {
                        permissions.add(permission.getCode());
                    }
                }
            }
        }

        List<SimpleGrantedAuthority> authorities = permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getStatus() == 1,
                true,
                true,
                true,
                authorities
        );
    }
}
