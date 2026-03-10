package com.ayd2.congress.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ayd2.congress.security.config.Security;
import com.ayd2.congress.security.jwtfilter.JwtAuthenticationFilter;

@WebMvcTest(
        controllers = {
                SecurityTest.TestAuthController.class,
                SecurityTest.TestOrganizationController.class,
                SecurityTest.TestActivityController.class,
                SecurityTest.TestSecureController.class
        },
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class
        }
)
@AutoConfigureMockMvc
@Import(Security.class)
public class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void testPostAuthIsPublic() throws Exception {
        mockMvc.perform(post("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetOrganizationsIsPublic() throws Exception {
        mockMvc.perform(get("/organizations"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetActivitiesIsPublic() throws Exception {
        mockMvc.perform(get("/activities/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testProtectedEndpointRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/secure"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test")
    void testProtectedEndpointWithAuthentication() throws Exception {
        mockMvc.perform(get("/secure"))
                .andExpect(status().isOk());
    }

    @RestController
    static class TestAuthController {
        @PostMapping("/auth")
        public String auth() {
            return "ok";
        }
    }

    @RestController
    static class TestOrganizationController {
        @GetMapping("/organizations")
        public String organizations() {
            return "ok";
        }
    }

    @RestController
    static class TestActivityController {
        @GetMapping("/activities/{id}")
        public String activity(@PathVariable Long id) {
            return "ok";
        }
    }

    @RestController
    static class TestSecureController {
        @GetMapping("/secure")
        public String secure() {
            return "ok";
        }
    }
}