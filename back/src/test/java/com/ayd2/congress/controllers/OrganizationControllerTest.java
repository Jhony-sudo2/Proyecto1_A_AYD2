package com.ayd2.congress.controllers;

import com.ayd2.congress.dtos.Organization.NewOrganizationRequest;
import com.ayd2.congress.dtos.Organization.OrganizationResponse;
import com.ayd2.congress.dtos.Organization.OrganizationUpdate;
import com.ayd2.congress.exceptionhandler.ControllerExceptionHandler;
import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.security.config.Security;
import com.ayd2.congress.security.jwtfilter.JwtAuthenticationFilter;
import com.ayd2.congress.services.Organization.OrganizationService;
import java.util.List;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = OrganizationController.class,
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
public class OrganizationControllerTest extends CommonMvcTest {

    private static final Long ORGANIZATION_ID = 1L;
    private static final String ORGANIZATION_NAME = "OpenAI";
    private static final String IMAGE = "base64-image";
    private static final String UPDATED_NAME = "Google";
    private static final String UPDATED_IMAGE = "updated-base64-image";

    @MockitoBean
    private OrganizationService organizationService;

    @Test
    public void testCreateOrganization() throws Exception {
        // Arrange
        NewOrganizationRequest request = new NewOrganizationRequest(
                ORGANIZATION_NAME,
                IMAGE
        );

        OrganizationResponse response = new OrganizationResponse(
                ORGANIZATION_ID,
                ORGANIZATION_NAME,
                IMAGE,
                false
        );

        when(organizationService.create(request)).thenReturn(response);

        // Act
        mockMvc.perform(post("/organizations")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Assertions.assertThat(json)
                            .isEqualTo(objectMapper.writeValueAsString(response));
                });

        verify(organizationService).create(request);
    }

    @Test
    public void testCreateOrganizationWhenDuplicated() throws Exception {
        // Arrange
        NewOrganizationRequest request = new NewOrganizationRequest(
                ORGANIZATION_NAME,
                IMAGE
        );

        doThrow(new DuplicatedEntityException("Organization with name " + ORGANIZATION_NAME + " already exists"))
                .when(organizationService).create(request);

        // Act
        mockMvc.perform(post("/organizations")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isConflict());

        verify(organizationService).create(request);
    }

    @Test
    public void testGetAllOrganizations() throws Exception {
        // Arrange
        OrganizationResponse response = new OrganizationResponse(
                ORGANIZATION_ID,
                ORGANIZATION_NAME,
                IMAGE,
                false
        );

        when(organizationService.getAll()).thenReturn(List.of(response));

        // Act
        mockMvc.perform(get("/organizations")
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Assertions.assertThat(json)
                            .isEqualTo(objectMapper.writeValueAsString(List.of(response)));
                });

        verify(organizationService).getAll();
    }

    @Test
    public void testGetOrganizationById() throws Exception {
        // Arrange
        OrganizationResponse response = new OrganizationResponse(
                ORGANIZATION_ID,
                ORGANIZATION_NAME,
                IMAGE,
                false
        );

        when(organizationService.getByIdResponse(ORGANIZATION_ID)).thenReturn(response);

        // Act
        mockMvc.perform(get("/organizations/{id}", ORGANIZATION_ID)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Assertions.assertThat(json)
                            .isEqualTo(objectMapper.writeValueAsString(response));
                });

        verify(organizationService).getByIdResponse(ORGANIZATION_ID);
    }

    @Test
    public void testGetOrganizationByIdWhenNotFound() throws Exception {
        // Arrange
        doThrow(new NotFoundException("Organization with id " + ORGANIZATION_ID + " not found"))
                .when(organizationService).getByIdResponse(ORGANIZATION_ID);

        // Act
        mockMvc.perform(get("/organizations/{id}", ORGANIZATION_ID)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNotFound());

        verify(organizationService).getByIdResponse(ORGANIZATION_ID);
    }

    @Test
    public void testUpdateOrganization() throws Exception {
        // Arrange
        OrganizationUpdate request = new OrganizationUpdate(
                UPDATED_NAME,
                UPDATED_IMAGE,
                true
        );

        OrganizationResponse response = new OrganizationResponse(
                ORGANIZATION_ID,
                UPDATED_NAME,
                UPDATED_IMAGE,
                true
        );

        when(organizationService.update(request, ORGANIZATION_ID)).thenReturn(response);

        // Act
        mockMvc.perform(put("/organizations/{id}", ORGANIZATION_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Assertions.assertThat(json)
                            .isEqualTo(objectMapper.writeValueAsString(response));
                });

        verify(organizationService).update(request, ORGANIZATION_ID);
    }

    @Test
    public void testUpdateOrganizationWhenNotFound() throws Exception {
        // Arrange
        OrganizationUpdate request = new OrganizationUpdate(
                UPDATED_NAME,
                UPDATED_IMAGE,
                true
        );

        doThrow(new NotFoundException("Organization with id " + ORGANIZATION_ID + " not found"))
                .when(organizationService).update(request, ORGANIZATION_ID);

        // Act
        mockMvc.perform(put("/organizations/{id}", ORGANIZATION_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isNotFound());

        verify(organizationService).update(request, ORGANIZATION_ID);
    }

    @Test
    public void testUpdateOrganizationWhenDuplicated() throws Exception {
        // Arrange
        OrganizationUpdate request = new OrganizationUpdate(
                UPDATED_NAME,
                UPDATED_IMAGE,
                true
        );

        doThrow(new DuplicatedEntityException("Organization with name " + UPDATED_NAME + " already exists"))
                .when(organizationService).update(request, ORGANIZATION_ID);

        // Act
        mockMvc.perform(put("/organizations/{id}", ORGANIZATION_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isConflict());

        verify(organizationService).update(request, ORGANIZATION_ID);
    }
}