package com.osm.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.osm.role.entity.Role;
import com.osm.role.mapper.RoleMapper;
import com.osm.user.dto.CreateUserRequest;
import com.osm.user.dto.UpdateUserRequest;
import com.osm.user.entity.User;
import com.osm.user.entity.UserRole;
import com.osm.user.mapper.UserMapper;
import com.osm.user.mapper.UserRoleMapper;
import com.osm.user.service.UserService;
import com.osm.user.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Long create(CreateUserRequest request) {
        // Check if username exists
        User existing = baseMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, request.getUsername())
                        .eq(User::getDeleted, 0)
        );
        if (existing != null) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        BeanUtils.copyProperties(request, user);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        baseMapper.insert(user);
        return user.getId();
    }

    @Override
    public void update(Long id, UpdateUserRequest request) {
        User user = baseMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (request.getUsername() != null) {
            // Check if new username conflicts
            User existing = baseMapper.selectOne(
                    new LambdaQueryWrapper<User>()
                            .eq(User::getUsername, request.getUsername())
                            .ne(User::getId, id)
                            .eq(User::getDeleted, 0)
            );
            if (existing != null) {
                throw new RuntimeException("用户名已存在");
            }
            user.setUsername(request.getUsername());
        }

        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }

        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }

        baseMapper.updateById(user);
    }

    @Override
    public UserVO getDetail(Long id) {
        User user = baseMapper.selectById(id);
        if (user == null) {
            return null;
        }

        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);

        // Get roles
        List<String> roles = getUserRoles(id);
        vo.setRoles(roles);

        return vo;
    }

    @Override
    public List<UserVO> listAll() {
        List<User> users = baseMapper.selectList(
                new LambdaQueryWrapper<User>()
                        .eq(User::getDeleted, 0)
                        .orderByDesc(User::getCreatedAt)
        );

        return users.stream().map(user -> {
            UserVO vo = new UserVO();
            BeanUtils.copyProperties(user, vo);
            vo.setRoles(getUserRoles(user.getId()));
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        User user = baseMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        baseMapper.deleteById(id);

        // Delete user-role relations
        userRoleMapper.delete(
                new LambdaQueryWrapper<UserRole>()
                        .eq(UserRole::getUserId, id)
        );
    }

    @Override
    @Transactional
    public void assignRoles(Long userId, List<Long> roleIds) {
        // Delete existing relations
        userRoleMapper.delete(
                new LambdaQueryWrapper<UserRole>()
                        .eq(UserRole::getUserId, userId)
        );

        // Add new relations
        for (Long roleId : roleIds) {
            UserRole ur = new UserRole();
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            userRoleMapper.insert(ur);
        }
    }

    private List<String> getUserRoles(Long userId) {
        List<String> roles = new ArrayList<>();
        List<UserRole> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRole>()
                        .eq(UserRole::getUserId, userId)
                        .eq(UserRole::getDeleted, 0)
        );

        for (UserRole ur : userRoles) {
            Role role = roleMapper.selectById(ur.getRoleId());
            if (role != null && role.getDeleted() == 0) {
                roles.add(role.getCode());
            }
        }
        return roles;
    }
}
