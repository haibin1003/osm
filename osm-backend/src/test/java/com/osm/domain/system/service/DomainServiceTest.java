package com.osm.domain.system.service;

import com.osm.domain.system.dto.CreateDomainRequest;
import com.osm.domain.system.entity.Domain;
import com.osm.domain.system.mapper.DomainMapper;
import com.osm.domain.system.service.impl.DomainServiceImpl;
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
class DomainServiceTest {

    @Mock
    private DomainMapper domainMapper;

    @Spy
    private DomainServiceImpl domainService = new DomainServiceImpl();

    @BeforeEach
    void setUp() throws Exception {
        // Use reflection to set the baseMapper since ServiceImpl protects it
        Field baseMapperField = domainService.getClass().getSuperclass().getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        baseMapperField.set(domainService, domainMapper);
    }

    @Test
    void shouldCreateDomainSuccessfully() {
        CreateDomainRequest request = new CreateDomainRequest();
        request.setName("金融域");
        request.setDescription("金融业务系统");

        when(domainMapper.insert(any(Domain.class))).thenAnswer(invocation -> {
            Domain domain = invocation.getArgument(0);
            domain.setId(1L);
            return 1;
        });

        Long id = domainService.create(request);

        assertNotNull(id);
        assertEquals(1L, id);
        verify(domainMapper).insert(any(Domain.class));
    }

    @Test
    void shouldGetDomainById() {
        Domain domain = new Domain();
        domain.setId(1L);
        domain.setName("金融域");

        when(domainMapper.selectById(1L)).thenReturn(domain);

        Domain result = domainService.getById(1L);

        assertNotNull(result);
        assertEquals("金融域", result.getName());
    }
}
