package com.ayd2.congress.controllers;

import com.ayd2.congress.dtos.acitivty.ActivityResponse;
import com.ayd2.congress.dtos.acitivty.NewActivityGuest;
import com.ayd2.congress.dtos.acitivty.NewActivityRequest;
import com.ayd2.congress.dtos.acitivty.NewProposalRequest;
import com.ayd2.congress.dtos.acitivty.ProposalResponse;
import com.ayd2.congress.dtos.acitivty.UpdateActivity;
import com.ayd2.congress.exceptionhandler.ControllerExceptionHandler;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.models.Enums.ActivityType;
import com.ayd2.congress.models.Enums.ProposalState;
import com.ayd2.congress.security.config.Security;
import com.ayd2.congress.security.jwtfilter.JwtAuthenticationFilter;
import com.ayd2.congress.services.activity.ActivityService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = ActivityController.class,
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
public class ActivityControllerTest extends CommonMvcTest {

    private static final Long ACTIVITY_ID = 1L;
    private static final Long PROPOSAL_ID = 2L;
    private static final Long ROOM_ID = 3L;
    private static final Long CONGRESS_ID = 4L;
    private static final Long USER_ID = 5L;

    private static final String ACTIVITY_NAME = "Spring Boot Workshop";
    private static final String DESCRIPTION = "Workshop description";
    private static final String CONGRESS_NAME = "Congress 2026";
    private static final String USER_NAME = "Juan Perez";
    private static final String ROOM_NAME = "Room A";

    private static final LocalDateTime START_DATE = LocalDateTime.of(2026, 3, 10, 10, 0);
    private static final LocalDateTime END_DATE = LocalDateTime.of(2026, 3, 10, 12, 0);

    @MockitoBean
    private ActivityService activityService;

    @Test
    public void testCreateActivity() throws Exception {
        // Arrange
        NewActivityRequest request = new NewActivityRequest(
                ACTIVITY_NAME,
                ROOM_ID,
                PROPOSAL_ID,
                START_DATE,
                END_DATE,
                100
        );

        ActivityResponse response = new ActivityResponse(
                ACTIVITY_ID,
                ACTIVITY_NAME,
                DESCRIPTION,
                START_DATE,
                END_DATE,
                ActivityType.CONFERENCE,
                100,
                ROOM_ID,
                ROOM_NAME,
                new String[]{USER_NAME}
        );

        when(activityService.createActivity(request)).thenReturn(response);

        // Act
        mockMvc.perform(post("/activities")
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
    }

    @Test
    public void testCreateActivityWithGuest() throws Exception {
        // Arrange
        NewActivityGuest request = new NewActivityGuest(
                ACTIVITY_NAME,
                ROOM_ID,
                START_DATE,
                END_DATE,
                50,
                new Long[]{USER_ID},
                CONGRESS_ID,
                DESCRIPTION,
                ActivityType.WORKSHOP
        );

        ActivityResponse response = new ActivityResponse(
                ACTIVITY_ID,
                ACTIVITY_NAME,
                DESCRIPTION,
                START_DATE,
                END_DATE,
                ActivityType.WORKSHOP,
                50,
                ROOM_ID,
                ROOM_NAME,
                new String[]{USER_NAME}
        );

        when(activityService.createActivityWithGuest(request)).thenReturn(response);

        // Act
        mockMvc.perform(post("/activities/guest")
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
    }

    @Test
    public void testUpdateActivity() throws Exception {
        // Arrange
        UpdateActivity request = new UpdateActivity(
                START_DATE.plusHours(1),
                END_DATE.plusHours(1),
                ROOM_ID,
                120L,
                "Updated Activity",
                "Updated Description"
        );

        ActivityResponse response = new ActivityResponse(
                ACTIVITY_ID,
                "Updated Activity",
                "Updated Description",
                START_DATE.plusHours(1),
                END_DATE.plusHours(1),
                ActivityType.CONFERENCE,
                120,
                ROOM_ID,
                ROOM_NAME,
                new String[]{USER_NAME}
        );

        when(activityService.updateActivity(ACTIVITY_ID, request)).thenReturn(response);

        // Act
        mockMvc.perform(put("/activities/{id}", ACTIVITY_ID)
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
    }

    @Test
    public void testDeleteActivity() throws Exception {
        // Act
        mockMvc.perform(delete("/activities/{id}", ACTIVITY_ID)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk());

        verify(activityService).deleteAcivity(ACTIVITY_ID);
    }

    @Test
    public void testDeleteActivityWhenNotFound() throws Exception {
        // Arrange
        doThrow(NotFoundException.class).when(activityService).deleteAcivity(ACTIVITY_ID);

        // Act
        mockMvc.perform(delete("/activities/{id}", ACTIVITY_ID)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNotFound());

        verify(activityService).deleteAcivity(ACTIVITY_ID);
    }

    @Test
    public void testCreateProposal() throws Exception {
        // Arrange
        NewProposalRequest request = new NewProposalRequest(
                CONGRESS_ID,
                USER_ID,
                "Proposal 1",
                DESCRIPTION,
                ActivityType.CONFERENCE
        );

        ProposalResponse response = new ProposalResponse(
                PROPOSAL_ID,
                "Proposal 1",
                CONGRESS_NAME,
                USER_NAME,
                DESCRIPTION,
                ActivityType.CONFERENCE,
                ProposalState.PENDING
        );

        when(activityService.createProposal(request)).thenReturn(response);

        // Act
        mockMvc.perform(post("/activities/proposal")
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
    }

    @Test
    public void testGetProposalById() throws Exception {
        // Arrange
        ProposalResponse response = new ProposalResponse(
                PROPOSAL_ID,
                "Proposal 1",
                CONGRESS_NAME,
                USER_NAME,
                DESCRIPTION,
                ActivityType.CONFERENCE,
                ProposalState.PENDING
        );

        when(activityService.getProposalResponseById(PROPOSAL_ID)).thenReturn(response);

        // Act
        mockMvc.perform(get("/activities/proposal/{id}", PROPOSAL_ID)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Assertions.assertThat(json)
                            .isEqualTo(objectMapper.writeValueAsString(response));
                });
    }

    @Test
    public void testGetProposalByUserId() throws Exception {
        // Arrange
        ProposalResponse response = new ProposalResponse(
                PROPOSAL_ID,
                "Proposal 1",
                CONGRESS_NAME,
                USER_NAME,
                DESCRIPTION,
                ActivityType.CONFERENCE,
                ProposalState.PENDING
        );

        when(activityService.getProposalByUserId(USER_ID)).thenReturn(List.of(response));

        // Act
        mockMvc.perform(get("/activities/proposal/user/{userId}", USER_ID)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Assertions.assertThat(json)
                            .isEqualTo(objectMapper.writeValueAsString(List.of(response)));
                });
    }

    @Test
    public void testGetProposalByCongressId() throws Exception {
        // Arrange
        ProposalResponse response = new ProposalResponse(
                PROPOSAL_ID,
                "Proposal 1",
                CONGRESS_NAME,
                USER_NAME,
                DESCRIPTION,
                ActivityType.CONFERENCE,
                ProposalState.PENDING
        );

        when(activityService.getProposalsByCongressId(CONGRESS_ID)).thenReturn(List.of(response));

        // Act
        mockMvc.perform(get("/activities/proposal/congress/{congressId}", CONGRESS_ID)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Assertions.assertThat(json)
                            .isEqualTo(objectMapper.writeValueAsString(List.of(response)));
                });
    }

    @Test
    public void testGetProposalByCongressIdAndState() throws Exception {
        // Arrange
        ProposalResponse response = new ProposalResponse(
                PROPOSAL_ID,
                "Proposal 1",
                CONGRESS_NAME,
                USER_NAME,
                DESCRIPTION,
                ActivityType.CONFERENCE,
                ProposalState.APPROVED
        );

        when(activityService.getProposalsByStateAndCongressId(ProposalState.APPROVED, CONGRESS_ID))
                .thenReturn(List.of(response));

        // Act
        mockMvc.perform(get("/activities/proposal/congress/{congressId}/{state}", CONGRESS_ID, ProposalState.APPROVED)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Assertions.assertThat(json)
                            .isEqualTo(objectMapper.writeValueAsString(List.of(response)));
                });
    }

    @Test
    public void testUpdateProposal() throws Exception {
        // Arrange
        String requestJson = """
                {
                  "state": "APPROVED"
                }
                """;

        ProposalResponse response = new ProposalResponse(
                PROPOSAL_ID,
                "Proposal 1",
                CONGRESS_NAME,
                USER_NAME,
                DESCRIPTION,
                ActivityType.CONFERENCE,
                ProposalState.APPROVED
        );

        when(activityService.updateProposal(eq(PROPOSAL_ID), any()))
                .thenReturn(response);

        // Act
        mockMvc.perform(put("/activities/proposal/{id}", PROPOSAL_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(requestJson))
                // Assert
                .andExpect(status().isAlreadyReported())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Assertions.assertThat(json)
                            .isEqualTo(objectMapper.writeValueAsString(response));
                });
    }

    @Test
    public void testGetAllActivities() throws Exception {
        // Arrange
        ActivityResponse response = new ActivityResponse(
                ACTIVITY_ID,
                ACTIVITY_NAME,
                DESCRIPTION,
                START_DATE,
                END_DATE,
                ActivityType.CONFERENCE,
                100,
                ROOM_ID,
                ROOM_NAME,
                new String[]{USER_NAME}
        );

        when(activityService.getActivitiesByCongressId(CONGRESS_ID)).thenReturn(List.of(response));

        // Act
        mockMvc.perform(get("/activities/{id}", CONGRESS_ID)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Assertions.assertThat(json)
                            .isEqualTo(objectMapper.writeValueAsString(List.of(response)));
                });
    }
}