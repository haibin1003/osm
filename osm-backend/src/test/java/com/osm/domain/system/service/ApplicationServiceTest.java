package com.osm.domain.system.service;

import com.osm.domain.system.dto.CreateApplicationRequest;
import com.osm.domain.system.entity.Application;
import com.osm.domain.system.mapper.ApplicationMapper;
import com.osm.domain.system.service.impl.ApplicationServiceImpl;
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
class ApplicationServiceTest {

    @Mock
    private ApplicationMapper applicationMapper;

    @Spy
    private ApplicationServiceImpl applicationService = new ApplicationServiceImpl();

    @BeforeEach
    void setUp() throws Exception {
        Field baseMapperField = applicationService.getClass().getSuperclass().getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        baseMapperField.set(applicationService, applicationMapper);
    }

    @Test
    void shouldCreateApplicationSuccessfully() {
        CreateApplicationRequest request = new CreateApplicationRequest();
        request.setName("Web门户");
        request.setDescription("用户Web端入口");
        request.setSystemId(1L);
        request.setCode("WEB_PORTAL");

        when(applicationMapper.insert(any(Application.class))).thenAnswer(invocation -> {
            Application app = invocation.getArgument(0);
            app.setId(1L);
            return 1;
        });

        Long id = applicationService.create(request);

        assertNotNull(id);
        assertEquals(1L, id);
        verify(applicationMapper).insert(any(Application.class));
    }

    @Test
    void shouldGetApplicationById() {
        Application app = new Application();
        app.setId(1L);
        app.setName("Web门户");
        app.setCode("WEB_PORTAL");

        when(applicationMapper.selectById(1L)).thenReturn(app);

        Application result = applicationService.getById(1L);

        assertNotNull(result);
        assertEquals("Web门户", result.getName());
    }
}
