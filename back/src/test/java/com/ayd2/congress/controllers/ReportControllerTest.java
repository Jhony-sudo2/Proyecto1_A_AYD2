package com.ayd2.congress.controllers;

import com.ayd2.congress.dtos.Congress.CongressResponse;
import com.ayd2.congress.dtos.reports.AtteendanceReporRequest;
import com.ayd2.congress.dtos.reports.AtteendanceReport;
import com.ayd2.congress.dtos.reports.EarningCongressFilter;
import com.ayd2.congress.dtos.reports.EarningCongressReport;
import com.ayd2.congress.dtos.reports.EarningFilter;
import com.ayd2.congress.dtos.reports.EarningReport;
import com.ayd2.congress.dtos.reports.InscriptionFilter;
import com.ayd2.congress.dtos.reports.InscriptionReport;
import com.ayd2.congress.dtos.reports.WorkshopParticipant;
import com.ayd2.congress.dtos.reports.WorkshopReport;
import com.ayd2.congress.dtos.reports.WorkshopReportFilter;
import com.ayd2.congress.exceptionhandler.ControllerExceptionHandler;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.security.config.Security;
import com.ayd2.congress.security.jwtfilter.JwtAuthenticationFilter;
import com.ayd2.congress.services.reports.ReportService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
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
        controllers = ReportController.class,
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
public class ReportControllerTest extends CommonMvcTest {

    private static final Long ORGANIZATION_ID = 1L;
    private static final Long CONGRESS_ID = 2L;
    private static final Long ACTIVITY_ID = 3L;
    private static final Long ROOM_ID = 4L;

    @MockitoBean
    private ReportService service;

    @Test
    public void testGetEarningsReport() throws Exception {
        // Arrange
        EarningReport response = new EarningReport(
                "Congress 2026",
                LocalDateTime.of(2026, 3, 12, 18, 0),
                LocalDateTime.of(2026, 3, 10, 8, 0),
                "Centro",
                "OpenAI",
                1500.0,
                1200.0
        );

        EarningFilter expectedFilter = new EarningFilter(
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2026, 3, 31),
                ORGANIZATION_ID
        );

        when(service.earningsReport(expectedFilter)).thenReturn(List.of(response));

        // Act
        mockMvc.perform(get("/reports/earnings")
                .param("startDate", "2026-03-01")
                .param("endDate", "2026-03-31")
                .param("organizationId", String.valueOf(ORGANIZATION_ID))
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Assertions.assertThat(json).isEqualTo(objectMapper.writeValueAsString(List.of(response)));
                });

        verify(service).earningsReport(expectedFilter);
    }

    @Test
    public void testGetEarningsReportWhenNotFound() throws Exception {
        // Arrange
        EarningFilter expectedFilter = new EarningFilter(
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2026, 3, 31),
                ORGANIZATION_ID
        );

        doThrow(new NotFoundException("Organization not found"))
                .when(service).earningsReport(expectedFilter);

        // Act
        mockMvc.perform(get("/reports/earnings")
                .param("startDate", "2026-03-01")
                .param("endDate", "2026-03-31")
                .param("organizationId", String.valueOf(ORGANIZATION_ID))
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNotFound());

        verify(service).earningsReport(expectedFilter);
    }

    @Test
    public void testGetCongressByOrganization() throws Exception {
        // Arrange
        CongressResponse response = new CongressResponse(
                CONGRESS_ID,
                "Congress 2026",
                "Description",
                100.0,
                "image",
                LocalDate.of(2026, 3, 10),
                LocalDate.of(2026, 3, 12),
                LocalDate.of(2026, 3, 1),
                "OpenAI",
                "Centro",
                ROOM_ID
        );

        when(service.congressByOrganizationId(ORGANIZATION_ID)).thenReturn(List.of(response));

        // Act
        mockMvc.perform(get("/reports/organizations/{id}/congress", ORGANIZATION_ID)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Assertions.assertThat(json).isEqualTo(objectMapper.writeValueAsString(List.of(response)));
                });

        verify(service).congressByOrganizationId(ORGANIZATION_ID);
    }

    @Test
    public void testGetParticipantsReport() throws Exception {
        // Arrange
        InscriptionFilter request = new InscriptionFilter(CONGRESS_ID, 1L);

        InscriptionReport response = new InscriptionReport(
                "1234567890101",
                "Juan Perez",
                "OpenAI",
                "juan@mail.com",
                "55555555",
                "Asistente"
        );

        when(service.inscriptionReport(request)).thenReturn(List.of(response));

        // Act
        mockMvc.perform(post("/reports/participants")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Assertions.assertThat(json).isEqualTo(objectMapper.writeValueAsString(List.of(response)));
                });

        verify(service).inscriptionReport(request);
    }

    @Test
    public void testGetAttendanceReport() throws Exception {
        // Arrange
        AtteendanceReporRequest request = new AtteendanceReporRequest(
                ACTIVITY_ID,
                ROOM_ID,
                LocalDateTime.of(2026, 3, 10, 8, 0),
                LocalDateTime.of(2026, 3, 10, 18, 0)
        );

        AtteendanceReport response = new AtteendanceReport(
                "Workshop Spring",
                "Room A",
                LocalDateTime.of(2026, 3, 10, 10, 0),
                25L
        );

        when(service.atteendanceReport(request)).thenReturn(List.of(response));

        // Act
        mockMvc.perform(post("/reports/attendance")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Assertions.assertThat(json).isEqualTo(objectMapper.writeValueAsString(List.of(response)));
                });

        verify(service).atteendanceReport(request);
    }

    @Test
    public void testGetWorkshopReservations() throws Exception {
        // Arrange
        WorkshopReportFilter request = new WorkshopReportFilter(CONGRESS_ID, ACTIVITY_ID);
        WorkshopParticipant participant = mock(WorkshopParticipant.class);

        WorkshopReport response = new WorkshopReport(
                "Workshop Spring",
                10L,
                2L,
                8L,
                List.of(participant)
        );

        when(service.atteendanceWorkshop(request)).thenReturn(List.of(response));

        // Act
        mockMvc.perform(post("/reports/workshops")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Assertions.assertThat(json).isEqualTo(objectMapper.writeValueAsString(List.of(response)));
                });

        verify(service).atteendanceWorkshop(request);
    }

    @Test
    public void testGetCongressEarnings() throws Exception {
        // Arrange
        EarningCongressFilter request = new EarningCongressFilter(
                CONGRESS_ID,
                LocalDateTime.of(2026, 3, 1, 0, 0),
                LocalDateTime.of(2026, 3, 31, 23, 59)
        );

        CongressResponse congress = new CongressResponse(
                CONGRESS_ID,
                "Congress 2026",
                "Description",
                100.0,
                "image",
                LocalDate.of(2026, 3, 10),
                LocalDate.of(2026, 3, 12),
                LocalDate.of(2026, 3, 1),
                "OpenAI",
                "Centro",
                ROOM_ID
        );

        EarningCongressReport response = new EarningCongressReport(
                congress,
                2000.0,
                300.0,
                1700.0
        );

        when(service.earningsCongressReport(request)).thenReturn(List.of(response));

        // Act
        mockMvc.perform(post("/reports/congress/earnings")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Assertions.assertThat(json).isEqualTo(objectMapper.writeValueAsString(List.of(response)));
                });

        verify(service).earningsCongressReport(request);
    }
}