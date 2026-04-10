package com.osm.role.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.osm.role.dto.CreateRoleRequest;
import com.osm.role.dto.UpdateRoleRequest;
import com.osm.role.entity.Role;
import com.osm.role.vo.RoleVO;

import java.util.List;

public interface RoleService extends IService<Role> {
    Long create(CreateRoleRequest request);
    void update(Long id, UpdateRoleRequest request);
    RoleVO getDetail(Long id);
    List<RoleVO> listAll();
    void delete(Long id);
    void assignPermissions(Long roleId, List<Long> permissionIds);
}
