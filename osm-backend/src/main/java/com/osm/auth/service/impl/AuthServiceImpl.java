package com.osm.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.osm.auth.dto.LoginRequest;
import com.osm.auth.dto.LoginResponse;
import com.osm.auth.security.JwtTokenProvider;
import com.osm.auth.service.AuthService;
import com.osm.role.entity.Role;
import com.osm.role.entity.RolePermission;
import com.osm.role.mapper.RoleMapper;
import com.osm.role.mapper.RolePermissionMapper;
import com.osm.user.entity.User;
import com.osm.user.entity.UserRole;
import com.osm.user.mapper.UserMapper;
import com.osm.user.mapper.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;
    private final RolePermissionMapper rolePermissionMapper;

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        String token = tokenProvider.generateToken(authentication);

        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, request.getUsername())
                        .eq(User::getDeleted, 0)
        );

        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setEmail(user.getEmail());

        // Get roles
        List<String> roles = new ArrayList<>();
        List<UserRole> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRole>()
                        .eq(UserRole::getUserId, user.getId())
                        .eq(UserRole::getDeleted, 0)
        );

        for (UserRole ur : userRoles) {
            Role role = roleMapper.selectById(ur.getRoleId());
            if (role != null && role.getDeleted() == 0) {
                roles.add(role.getCode());
            }
        }
        userInfo.setRoles(roles);

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUser(userInfo);

        return response;
    }

    @Override
    public LoginResponse.UserInfo getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, username)
                        .eq(User::getDeleted, 0)
        );

        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setEmail(user.getEmail());

        // Get roles
        List<String> roles = new ArrayList<>();
        List<UserRole> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRole>()
                        .eq(UserRole::getUserId, user.getId())
                        .eq(UserRole::getDeleted, 0)
        );

        for (UserRole ur : userRoles) {
            Role role = roleMapper.selectById(ur.getRoleId());
            if (role != null && role.getDeleted() == 0) {
                roles.add(role.getCode());
            }
        }
        userInfo.setRoles(roles);

        return userInfo;
    }
}
