package com.ayd2.congress.controllers;

import com.ayd2.congress.dtos.SysConfig.SysConfigResponse;
import com.ayd2.congress.dtos.SysConfig.SysConfigUpdate;
import com.ayd2.congress.exceptionhandler.ControllerExceptionHandler;
import com.ayd2.congress.exceptions.InvalidPriceException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.security.config.Security;
import com.ayd2.congress.security.jwtfilter.JwtAuthenticationFilter;
import com.ayd2.congress.services.systemconfig.SystemConfigService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = SystemController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class,
                UserDetailsServiceAutoConfiguration.class
        },
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = Security.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
        }
)
@AutoConfigureMockMvc(addFilters = false)
@Import({ControllerExceptionHandler.class, CommonMvcTest.TestConfig.class})
public class SystemControllerTest extends CommonMvcTest {

    @MockitoBean
    private SystemConfigService service;

    @Test
    public void testGetSystemConfig() throws Exception {
        // Arrange
        SysConfigResponse response = new SysConfigResponse(100.0, 15.0);
        when(service.getConfigResponse()).thenReturn(response);

        // Act
        mockMvc.perform(get("/systemconfigurations")
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Assertions.assertThat(json).isEqualTo(objectMapper.writeValueAsString(response));
                });

        verify(service).getConfigResponse();
    }

    @Test
    public void testGetSystemConfigWhenNotFound() throws Exception {
        // Arrange
        doThrow(new NotFoundException("CONFIGURATION NOT FOUND"))
                .when(service).getConfigResponse();

        // Act
        mockMvc.perform(get("/systemconfigurations")
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNotFound());

        verify(service).getConfigResponse();
    }

    @Test
    public void testUpdateConfig() throws Exception {
        // Arrange
        SysConfigUpdate request = new SysConfigUpdate(150.0, 20.0);
        SysConfigResponse response = new SysConfigResponse(150.0, 20.0);

        when(service.update(request)).thenReturn(response);

        // Act
        mockMvc.perform(put("/systemconfigurations")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Assertions.assertThat(json).isEqualTo(objectMapper.writeValueAsString(response));
                });

        verify(service).update(request);
    }

    @Test
    public void testUpdateConfigWhenInvalidPrice() throws Exception {
        // Arrange
        SysConfigUpdate request = new SysConfigUpdate(150.0, 20.0);

        doThrow(new InvalidPriceException("The price cannot be"))
                .when(service).update(request);

        // Act
        mockMvc.perform(put("/systemconfigurations")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isBadRequest());

        verify(service).update(request);
    }

    @Test
    public void testUpdateConfigWhenNotFound() throws Exception {
        // Arrange
        SysConfigUpdate request = new SysConfigUpdate(150.0, 20.0);

        doThrow(new NotFoundException("CONFIGURATION NOT FOUND"))
                .when(service).update(request);

        // Act
        mockMvc.perform(put("/systemconfigurations")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isNotFound());

        verify(service).update(request);
    }
}