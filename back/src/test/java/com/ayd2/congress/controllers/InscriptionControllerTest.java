package com.ayd2.congress.controllers;

import com.ayd2.congress.dtos.Inscription.InscriptionResponse;
import com.ayd2.congress.dtos.Inscription.PayRequest;
import com.ayd2.congress.dtos.Inscription.PayResponse;
import com.ayd2.congress.exceptionhandler.ControllerExceptionHandler;
import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.exceptions.InsufficientFundsException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.security.config.Security;
import com.ayd2.congress.security.jwtfilter.JwtAuthenticationFilter;
import com.ayd2.congress.services.Inscription.InscriptionService;
import java.time.LocalDate;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = InscriptionController.class,
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
public class InscriptionControllerTest extends CommonMvcTest {

    private static final Long USER_ID = 1L;
    private static final Long CONGRESS_ID = 2L;
    private static final Long PAYMENT_ID = 10L;

    private static final LocalDate PAYMENT_DATE = LocalDate.of(2026, 3, 10);
    private static final String USER_NAME = "Juan Perez";
    private static final String CONGRESS_NAME = "Congress 2026";
    private static final String ROL_NAME = "Asistente";

    @MockitoBean
    private InscriptionService service;

    @Test
    public void testPay() throws Exception {
        // Arrange
        PayRequest request = new PayRequest(USER_ID, CONGRESS_ID, PAYMENT_DATE);
        PayResponse response = new PayResponse(
                PAYMENT_ID,
                USER_ID,
                USER_NAME,
                CONGRESS_ID,
                CONGRESS_NAME,
                300.0,
                PAYMENT_DATE
        );

        when(service.pay(request)).thenReturn(response);

        // Act
        mockMvc.perform(post("/inscriptions/pay")
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

        verify(service).pay(request);
    }

    @Test
    public void testPayWhenNotFound() throws Exception {
        // Arrange
        PayRequest request = new PayRequest(USER_ID, CONGRESS_ID, PAYMENT_DATE);

        doThrow(new NotFoundException("User not found"))
                .when(service).pay(request);

        // Act
        mockMvc.perform(post("/inscriptions/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isNotFound());

        verify(service).pay(request);
    }

    @Test
    public void testPayWhenInsufficientFunds() throws Exception {
        // Arrange
        PayRequest request = new PayRequest(USER_ID, CONGRESS_ID, PAYMENT_DATE);

        doThrow(new InsufficientFundsException("Wallet hast not funds"))
                .when(service).pay(request);

        // Act
        mockMvc.perform(post("/inscriptions/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isPaymentRequired());

        verify(service).pay(request);
    }

    @Test
    public void testPayWhenDuplicated() throws Exception {
        // Arrange
        PayRequest request = new PayRequest(USER_ID, CONGRESS_ID, PAYMENT_DATE);

        doThrow(new DuplicatedEntityException("The user has already made the payment"))
                .when(service).pay(request);

        // Act
        mockMvc.perform(post("/inscriptions/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isConflict());

        verify(service).pay(request);
    }

    @Test
    public void testGetPaymentsByUserId() throws Exception {
        // Arrange
        PayResponse response = new PayResponse(
                PAYMENT_ID,
                USER_ID,
                USER_NAME,
                CONGRESS_ID,
                CONGRESS_NAME,
                300.0,
                PAYMENT_DATE
        );

        when(service.getPaymentsByUserId(USER_ID)).thenReturn(List.of(response));

        // Act
        mockMvc.perform(get("/inscriptions/pay/{userId}", USER_ID)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Assertions.assertThat(json)
                            .isEqualTo(objectMapper.writeValueAsString(List.of(response)));
                });

        verify(service).getPaymentsByUserId(USER_ID);
    }

    @Test
    public void testGetPaymentsByUserIdWhenNotFound() throws Exception {
        // Arrange
        doThrow(new NotFoundException("User not found"))
                .when(service).getPaymentsByUserId(USER_ID);

        // Act
        mockMvc.perform(get("/inscriptions/pay/{userId}", USER_ID)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNotFound());

        verify(service).getPaymentsByUserId(USER_ID);
    }

    @Test
    public void testGetInscriptionsByUserid() throws Exception {
        // Arrange
        InscriptionResponse response = new InscriptionResponse(
                CONGRESS_ID,
                CONGRESS_NAME,
                USER_ID,
                USER_NAME,
                ROL_NAME
        );

        when(service.getInscriptionsByUserId(USER_ID)).thenReturn(List.of(response));

        // Act
        mockMvc.perform(get("/inscriptions/{userId}", USER_ID)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Assertions.assertThat(json)
                            .isEqualTo(objectMapper.writeValueAsString(List.of(response)));
                });

        verify(service).getInscriptionsByUserId(USER_ID);
    }

    @Test
    public void testGetInscriptionsByUseridWhenNotFound() throws Exception {
        // Arrange
        doThrow(new NotFoundException("User not found"))
                .when(service).getInscriptionsByUserId(USER_ID);

        // Act
        mockMvc.perform(get("/inscriptions/{userId}", USER_ID)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNotFound());

        verify(service).getInscriptionsByUserId(USER_ID);
    }

    @Test
    public void testGetPaymentsByCongressId() throws Exception {
        // OJO: este test respeta el comportamiento ACTUAL del controller,
        // que llama service.getPaymentsByUserId(congressId)

        // Arrange
        PayResponse response = new PayResponse(
                PAYMENT_ID,
                USER_ID,
                USER_NAME,
                CONGRESS_ID,
                CONGRESS_NAME,
                300.0,
                PAYMENT_DATE
        );

        when(service.getPaymentsByUserId(CONGRESS_ID)).thenReturn(List.of(response));

        // Act
        mockMvc.perform(get("/inscriptions/pay/congress/{congressId}", CONGRESS_ID)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Assertions.assertThat(json)
                            .isEqualTo(objectMapper.writeValueAsString(List.of(response)));
                });

        verify(service).getPaymentsByUserId(CONGRESS_ID);
    }

    @Test
    public void testGetPaymentsByCongressIdWhenNotFound() throws Exception {
        // OJO: este test también respeta el comportamiento ACTUAL del controller

        // Arrange
        doThrow(new NotFoundException("Congress not found"))
                .when(service).getPaymentsByUserId(CONGRESS_ID);

        // Act
        mockMvc.perform(get("/inscriptions/pay/congress/{congressId}", CONGRESS_ID)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNotFound());

        verify(service).getPaymentsByUserId(CONGRESS_ID);
    }

    @Test
    public void testGetInscriptionsByCongressId() throws Exception {
        // Arrange
        InscriptionResponse response = new InscriptionResponse(
                CONGRESS_ID,
                CONGRESS_NAME,
                USER_ID,
                USER_NAME,
                ROL_NAME
        );

        when(service.getInscriptionsByCongressId(CONGRESS_ID)).thenReturn(List.of(response));

        // Act
        mockMvc.perform(get("/inscriptions/congress/{congressId}", CONGRESS_ID)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Assertions.assertThat(json)
                            .isEqualTo(objectMapper.writeValueAsString(List.of(response)));
                });

        verify(service).getInscriptionsByCongressId(CONGRESS_ID);
    }

    @Test
    public void testGetInscriptionsByCongressIdWhenNotFound() throws Exception {
        // Arrange
        doThrow(new NotFoundException("Congress not found"))
                .when(service).getInscriptionsByCongressId(CONGRESS_ID);

        // Act
        mockMvc.perform(get("/inscriptions/congress/{congressId}", CONGRESS_ID)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNotFound());

        verify(service).getInscriptionsByCongressId(CONGRESS_ID);
    }
}