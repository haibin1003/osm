package com.osm.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.osm.user.dto.CreateUserRequest;
import com.osm.user.dto.UpdateUserRequest;
import com.osm.user.entity.User;
import com.osm.user.vo.UserVO;

import java.util.List;

public interface UserService extends IService<User> {
    Long create(CreateUserRequest request);
    void update(Long id, UpdateUserRequest request);
    UserVO getDetail(Long id);
    List<UserVO> listAll();
    void delete(Long id);
    void assignRoles(Long userId, List<Long> roleIds);
}
