package com.osm.domain.system.service;

import com.osm.domain.system.dto.CreateSystemRequest;
import com.osm.domain.system.entity.System;
import com.osm.domain.system.mapper.SystemMapper;
import com.osm.domain.system.service.impl.SystemServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SystemServiceTest {

    @Mock
    private SystemMapper systemMapper;

    @Spy
    private SystemServiceImpl systemService = new SystemServiceImpl();

    @BeforeEach
    void setUp() throws Exception {
        Field baseMapperField = systemService.getClass().getSuperclass().getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        baseMapperField.set(systemService, systemMapper);
    }

    @Test
    void shouldCreateSystemSuccessfully() {
        CreateSystemRequest request = new CreateSystemRequest();
        request.setName("订单系统");
        request.setDescription("处理订单业务");
        request.setDomainId(1L);
        request.setCode("ORDER_SYS");

        when(systemMapper.insert(any(System.class))).thenAnswer(invocation -> {
            System sys = invocation.getArgument(0);
            sys.setId(1L);
            return 1;
        });

        Long id = systemService.create(request);

        assertNotNull(id);
        assertEquals(1L, id);
        verify(systemMapper).insert(any(System.class));
    }

    @Test
    void shouldGetSystemById() {
        System sys = new System();
        sys.setId(1L);
        sys.setName("订单系统");
        sys.setCode("ORDER_SYS");

        when(systemMapper.selectById(1L)).thenReturn(sys);

        System result = systemService.getById(1L);

        assertNotNull(result);
        assertEquals("订单系统", result.getName());
    }
}
