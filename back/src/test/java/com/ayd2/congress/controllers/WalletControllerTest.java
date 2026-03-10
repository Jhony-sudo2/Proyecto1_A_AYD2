package com.ayd2.congress.controllers;

import com.ayd2.congress.dtos.Wallet.RechargeHistory;
import com.ayd2.congress.dtos.Wallet.RechargeRequest;
import com.ayd2.congress.dtos.Wallet.WalletResponse;
import com.ayd2.congress.exceptionhandler.ControllerExceptionHandler;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.security.config.Security;
import com.ayd2.congress.security.jwtfilter.JwtAuthenticationFilter;
import com.ayd2.congress.services.Wallet.WalletService;
import java.time.LocalDateTime;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = WalletController.class,
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
public class WalletControllerTest extends CommonMvcTest {

    private static final Long USER_ID = 1L;
    private static final Long WALLET_ID = 10L;

    @MockitoBean
    private WalletService service;

    @Test
    public void testGetById() throws Exception {
        // Arrange
        WalletResponse response = new WalletResponse(WALLET_ID, 500.0);
        when(service.getByUserIdResponse(USER_ID)).thenReturn(response);

        // Act
        mockMvc.perform(get("/users/{userId}/wallet", USER_ID)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Assertions.assertThat(json)
                            .isEqualTo(objectMapper.writeValueAsString(response));
                });

        verify(service).getByUserIdResponse(USER_ID);
    }

    @Test
    public void testGetByIdWhenNotFound() throws Exception {
        // Arrange
        doThrow(new NotFoundException("WALLET NOT FOUND"))
                .when(service).getByUserIdResponse(USER_ID);

        // Act
        mockMvc.perform(get("/users/{userId}/wallet", USER_ID)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNotFound());

        verify(service).getByUserIdResponse(USER_ID);
    }

    @Test
    public void testRechargeWallet() throws Exception {
        // Arrange
        RechargeRequest request = new RechargeRequest(
                150.0,
                LocalDateTime.of(2026, 3, 10, 10, 30)
        );

        WalletResponse response = new WalletResponse(WALLET_ID, 650.0);

        when(service.recharge(request, USER_ID)).thenReturn(response);

        // Act
        mockMvc.perform(put("/users/{userId}/wallet", USER_ID)
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

        verify(service).recharge(request, USER_ID);
    }

    @Test
    public void testRechargeWalletWhenNotFound() throws Exception {
        // Arrange
        RechargeRequest request = new RechargeRequest(
                150.0,
                LocalDateTime.of(2026, 3, 10, 10, 30)
        );

        doThrow(new NotFoundException("WALLET NOT FOUND"))
                .when(service).recharge(request, USER_ID);

        // Act
        mockMvc.perform(put("/users/{userId}/wallet", USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isNotFound());

        verify(service).recharge(request, USER_ID);
    }

    @Test
    public void testGetHistoryByWalletId() throws Exception {
        // OJO: este test respeta el comportamiento ACTUAL del controller,
        // que llama service.getHistoryRechargeByWalletId(userId)

        // Arrange
        RechargeHistory response = new RechargeHistory(
                100.0,
                LocalDateTime.of(2026, 3, 1, 8, 0)
        );

        when(service.getHistoryRechargeByWalletId(USER_ID)).thenReturn(List.of(response));

        // Act
        mockMvc.perform(get("/users/{userId}/wallet/recharges", USER_ID)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Assertions.assertThat(json)
                            .isEqualTo(objectMapper.writeValueAsString(List.of(response)));
                });

        verify(service).getHistoryRechargeByWalletId(USER_ID);
    }
}