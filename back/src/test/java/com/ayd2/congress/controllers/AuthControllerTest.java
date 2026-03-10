package com.ayd2.congress.controllers;

import com.ayd2.congress.dtos.Auth.LoginRequest;
import com.ayd2.congress.dtos.Auth.LoginResponse;
import com.ayd2.congress.dtos.User.UserRegister;
import com.ayd2.congress.dtos.User.UserResponse;
import com.ayd2.congress.exceptionhandler.ControllerExceptionHandler;
import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.exceptions.NotAuthorizedException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.security.config.Security;
import com.ayd2.congress.security.jwtfilter.JwtAuthenticationFilter;
import com.ayd2.congress.services.Auth.AuthService;
import com.ayd2.congress.services.User.UserService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = AuthController.class,
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
public class AuthControllerTest extends CommonMvcTest {

    private static final String EMAIL = "juan@mail.com";
    private static final String PASSWORD = "Password123";
    private static final String TOKEN = "jwt-token";

    private static final Long USER_ID = 1L;
    private static final String IDENTIFICATION = "1234567890101";
    private static final String NAME = "Juan";
    private static final String LAST_NAME = "Perez";
    private static final String PHONE = "55555555";
    private static final String IMAGE_URL = "base64-image";
    private static final String NATIONALITY = "Guatemalan";
    private static final Long ORGANIZATION_ID = 2L;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UserService userService;

    @Test
    public void testLogin() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest(EMAIL, PASSWORD);
        LoginResponse response = new LoginResponse(TOKEN, "Bearer", 3600L);

        when(authService.authenticateAndGetToken(request)).thenReturn(response);

        // Act
        mockMvc.perform(post("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isAccepted())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Assertions.assertThat(json)
                            .isEqualTo(objectMapper.writeValueAsString(response));
                });

        verify(authService).authenticateAndGetToken(request);
    }

    @Test
    public void testLoginWhenNotAuthorized() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest(EMAIL, PASSWORD);

        doThrow(new NotAuthorizedException("invalid credentials"))
                .when(authService).authenticateAndGetToken(request);

        // Act
        mockMvc.perform(post("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isUnauthorized());

        verify(authService).authenticateAndGetToken(request);
    }

    @Test
    public void testLoginWhenUserNotFound() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest(EMAIL, PASSWORD);

        doThrow(new NotFoundException("User not found"))
                .when(authService).authenticateAndGetToken(request);

        // Act
        mockMvc.perform(post("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isNotFound());

        verify(authService).authenticateAndGetToken(request);
    }

    @Test
    public void testRegisterUser() throws Exception {
        // Arrange
        UserRegister request = new UserRegister(
                IDENTIFICATION,
                NAME,
                LAST_NAME,
                EMAIL,
                PHONE,
                IMAGE_URL,
                NATIONALITY,
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
                IMAGE_URL,
                true,
                NATIONALITY,
                "OpenAI"
        );

        when(userService.registerUserNormal(request)).thenReturn(response);

        // Act
        mockMvc.perform(post("/auth/register")
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

        verify(userService).registerUserNormal(request);
    }

    @Test
    public void testRegisterUserWhenDuplicated() throws Exception {
        // Arrange
        UserRegister request = new UserRegister(
                IDENTIFICATION,
                NAME,
                LAST_NAME,
                EMAIL,
                PHONE,
                IMAGE_URL,
                NATIONALITY,
                ORGANIZATION_ID,
                PASSWORD
        );

        doThrow(new DuplicatedEntityException("Email already exists"))
                .when(userService).registerUserNormal(request);

        // Act
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isConflict());

        verify(userService).registerUserNormal(request);
    }

    @Test
    public void testRegisterUserWhenNotFound() throws Exception {
        // Arrange
        UserRegister request = new UserRegister(
                IDENTIFICATION,
                NAME,
                LAST_NAME,
                EMAIL,
                PHONE,
                IMAGE_URL,
                NATIONALITY,
                ORGANIZATION_ID,
                PASSWORD
        );

        doThrow(new NotFoundException("Organization not found"))
                .when(userService).registerUserNormal(request);

        // Act
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isNotFound());

        verify(userService).registerUserNormal(request);
    }
}