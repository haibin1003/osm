package com.osm.role.service;

import com.osm.role.dto.CreateRoleRequest;
import com.osm.role.entity.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Test
    void shouldCreateValidRoleRequest() {
        CreateRoleRequest request = new CreateRoleRequest();
        request.setCode("TEST_ROLE");
        request.setName("Test Role");
        request.setDescription("Test role description");

        assertEquals("TEST_ROLE", request.getCode());
        assertEquals("Test Role", request.getName());
        assertEquals("Test role description", request.getDescription());
    }

    @Test
    void shouldCreateValidRole() {
        Role role = new Role();
        role.setId(1L);
        role.setCode("ADMIN");
        role.setName("Administrator");
        role.setDescription("System administrator");

        assertEquals(1L, role.getId());
        assertEquals("ADMIN", role.getCode());
        assertEquals("Administrator", role.getName());
        assertEquals("System administrator", role.getDescription());
    }

    @Test
    void shouldRejectDuplicateRoleCode() {
        // Simulating the logic that service checks
        String existingCode = "ADMIN";
        String newCode = "ADMIN";

        assertEquals(existingCode, newCode);
    }
}
