package com.ayd2.congress.controllers;

import com.ayd2.congress.dtos.Rol.RolResponse;
import com.ayd2.congress.dtos.User.NewUserRequest;
import com.ayd2.congress.dtos.User.UserResponse;
import com.ayd2.congress.dtos.User.UserUpdate;
import com.ayd2.congress.dtos.certificate.CertificateResponse;
import com.ayd2.congress.exceptionhandler.ControllerExceptionHandler;
import com.ayd2.congress.exceptions.CodeAlreadyExpiredException;
import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.exceptions.NotAuthorizedException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.security.config.Security;
import com.ayd2.congress.security.jwtfilter.JwtAuthenticationFilter;
import com.ayd2.congress.services.User.UserService;
import com.ayd2.congress.services.certificate.CertificateService;
import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = UserController.class,
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
public class UserControllerTest extends CommonMvcTest {

    private static final Long USER_ID = 1L;
    private static final Long CONGRESS_ID = 2L;
    private static final Long ROL_ID = 3L;
    private static final Long ORGANIZATION_ID = 4L;

    private static final String IDENTIFICATION = "1234567890101";
    private static final String NAME = "Juan";
    private static final String LAST_NAME = "Perez";
    private static final String EMAIL = "juan@mail.com";
    private static final String PHONE = "55555555";
    private static final String IMAGE = "base64-image";
    private static final String NATIONALITY = "Guatemalan";
    private static final String PASSWORD = "Password123";

    @MockitoBean
    private UserService service;

    @MockitoBean
    private CertificateService certificateService;

    @Test
    public void testSaveUser() throws Exception {
        // Arrange
        NewUserRequest request = new NewUserRequest(
                IDENTIFICATION,
                NAME,
                LAST_NAME,
                EMAIL,
                PHONE,
                IMAGE,
                NATIONALITY,
                ROL_ID,
                ORGANIZATION_ID,
                PASSWORD
        );

        UserResponse response = new UserResponse(
                USER_ID,
                IDENTIFICATION,
                NAME,
                LAST_NAME,
                EMAIL,
                PHONE,
                IMAGE,
                true,
                NATIONALITY,
                "OpenAI"
        );

        when(service.create(request)).thenReturn(response);

        // Act
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Assertions.assertThat(json).isEqualTo(objectMapper.writeValueAsString(response));
                });

        verify(service).create(request);
    }

    @Test
    public void testSaveUserWhenDuplicated() throws Exception {
        // Arrange
        NewUserRequest request = new NewUserRequest(
                IDENTIFICATION, NAME, LAST_NAME, EMAIL, PHONE, IMAGE, NATIONALITY, ROL_ID, ORGANIZATION_ID, PASSWORD
        );

        doThrow(new DuplicatedEntityException("Email already exists"))
                .when(service).create(request);

        // Act
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isConflict());

        verify(service).create(request);
    }

    @Test
    public void testGetAllUsers() throws Exception {
        // Arrange
        UserResponse response = new UserResponse(
                USER_ID,
                IDENTIFICATION,
                NAME,
                LAST_NAME,
                EMAIL,
                PHONE,
                IMAGE,
                true,
                NATIONALITY,
                "OpenAI"
        );

        when(service.getAllUsers()).thenReturn(List.of(response));

        // Act
        mockMvc.perform(get("/users")
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Assertions.assertThat(json).isEqualTo(objectMapper.writeValueAsString(List.of(response)));
                });

        verify(service).getAllUsers();
    }

    @Test
    public void testGetById() throws Exception {
        // Arrange
        UserResponse response = new UserResponse(
                USER_ID,
                IDENTIFICATION,
                NAME,
                LAST_NAME,
                EMAIL,
                PHONE,
                IMAGE,
                true,
                NATIONALITY,
                "OpenAI"
        );

        when(service.getByIdResponse(USER_ID)).thenReturn(response);

        // Act
        mockMvc.perform(get("/users/{id}", USER_ID)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Assertions.assertThat(json).isEqualTo(objectMapper.writeValueAsString(response));
                });

        verify(service).getByIdResponse(USER_ID);
    }

    @Test
    public void testGetByIdWhenNotFound() throws Exception {
        // Arrange
        doThrow(new NotFoundException("User not found"))
                .when(service).getByIdResponse(USER_ID);

        // Act
        mockMvc.perform(get("/users/{id}", USER_ID)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNotFound());

        verify(service).getByIdResponse(USER_ID);
    }

    @Test
    public void testGetAllRols() throws Exception {
        // Arrange
        RolResponse response = new RolResponse(ROL_ID, "ADMIN");

        when(service.getAllRols()).thenReturn(List.of(response));

        // Act
        mockMvc.perform(get("/users/rols")
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Assertions.assertThat(json).isEqualTo(objectMapper.writeValueAsString(List.of(response)));
                });

        verify(service).getAllRols();
    }

    @Test
    public void testUpdateUser() throws Exception {
        // Arrange
        UserUpdate request = new UserUpdate(
                "Carlos",
                "carlos@mail.com",
                "Lopez",
                "44444444",
                "new-image"
        );

        UserResponse response = new UserResponse(
                USER_ID,
                IDENTIFICATION,
                "Carlos",
                "Lopez",
                "carlos@mail.com",
                "44444444",
                "new-image",
                true,
                NATIONALITY,
                "OpenAI"
        );

        when(service.update(request, USER_ID)).thenReturn(response);

        // Act
        mockMvc.perform(put("/users/{id}", USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Assertions.assertThat(json).isEqualTo(objectMapper.writeValueAsString(response));
                });

        verify(service).update(request, USER_ID);
    }

    @Test
    public void testChangeState() throws Exception {
        // Arrange
        UserResponse response = new UserResponse(
                USER_ID,
                IDENTIFICATION,
                NAME,
                LAST_NAME,
                EMAIL,
                PHONE,
                IMAGE,
                false,
                NATIONALITY,
                "OpenAI"
        );

        when(service.changeState(USER_ID)).thenReturn(response);

        // Act
        mockMvc.perform(patch("/users/{userId}", USER_ID)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Assertions.assertThat(json).isEqualTo(objectMapper.writeValueAsString(response));
                });

        verify(service).changeState(USER_ID);
    }

    @Test
    public void testUpdatePassword() throws Exception {
        // Arrange
        String requestJson = """
                {
                  "currentPassword": "Password123",
                  "newPassword": "NewPassword123"
                }
                """;

        // Act
        mockMvc.perform(put("/users/{id}/password", USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(requestJson))
                // Assert
                .andExpect(status().isOk());

        verify(service).updatePassword(any(), eq(USER_ID));
    }

    @Test
    public void testUpdatePasswordWhenNotAuthorized() throws Exception {
        // Arrange
        String requestJson = """
                {
                  "currentPassword": "bad-password",
                  "newPassword": "NewPassword123"
                }
                """;

        doThrow(new NotAuthorizedException("Credentials errors"))
                .when(service).updatePassword(any(), eq(USER_ID));

        // Act
        mockMvc.perform(put("/users/{id}/password", USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(requestJson))
                // Assert
                .andExpect(status().isUnauthorized());

        verify(service).updatePassword(any(), eq(USER_ID));
    }

    @Test
    public void testRecoverPassword() throws Exception {
        // Arrange
        String requestJson = """
                {
                  "email": "juan@mail.com"
                }
                """;

        // Act
        mockMvc.perform(post("/users/password/recovery")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(requestJson))
                // Assert
                .andExpect(status().isOk());

        verify(service).recoverPassword(any());
    }

    @Test
    public void testConfirmCode() throws Exception {
        // Arrange
        String requestJson = """
                {
                  "code": "123456",
                  "email": "juan@mail.com",
                  "newPassword": "NewPassword123"
                }
                """;

        // Act
        mockMvc.perform(post("/users/password/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(requestJson))
                // Assert
                .andExpect(status().isOk());

        verify(service).confirmCode(any());
    }

    @Test
    public void testConfirmCodeWhenExpired() throws Exception {
        // Arrange
        String requestJson = """
                {
                  "code": "123456",
                  "email": "juan@mail.com",
                  "newPassword": "NewPassword123"
                }
                """;

        doThrow(new CodeAlreadyExpiredException("El codigo ya vencio"))
                .when(service).confirmCode(any());

        // Act
        mockMvc.perform(post("/users/password/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(requestJson))
                // Assert
                .andExpect(status().isBadRequest());

        verify(service).confirmCode(any());
    }

    @Test
    public void testGetCertificatesByUserIdAndCongressId() throws Exception {
        // Arrange
        CertificateResponse response = new CertificateResponse(
                "Congress 2026",
                LocalDateTime.of(2026, 3, 10, 8, 0),
                LocalDateTime.of(2026, 3, 12, 18, 0),
                "Centro",
                NAME,
                LAST_NAME,
                LocalDateTime.of(2026, 3, 12, 18, 0),
                "OpenAI",
                "Asistente"
        );

        when(certificateService.getCertificatesByUserId(USER_ID, CONGRESS_ID)).thenReturn(List.of(response));

        // Act
        mockMvc.perform(get("/users/{userId}/congresses/{congressId}/certificates", USER_ID, CONGRESS_ID)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Assertions.assertThat(json).isEqualTo(objectMapper.writeValueAsString(List.of(response)));
                });

        verify(certificateService).getCertificatesByUserId(USER_ID, CONGRESS_ID);
    }

    @Test
    public void testGetCertificatesByUserIdAndCongressIdWhenNotFound() throws Exception {
        // Arrange
        doThrow(new NotFoundException("User not found"))
                .when(certificateService).getCertificatesByUserId(USER_ID, CONGRESS_ID);

        // Act
        mockMvc.perform(get("/users/{userId}/congresses/{congressId}/certificates", USER_ID, CONGRESS_ID)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNotFound());

        verify(certificateService).getCertificatesByUserId(USER_ID, CONGRESS_ID);
    }
}