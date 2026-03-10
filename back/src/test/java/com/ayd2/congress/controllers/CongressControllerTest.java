package com.ayd2.congress.controllers;

import com.ayd2.congress.dtos.Congress.CongressResponse;
import com.ayd2.congress.dtos.Congress.NewCommitteeRequest;
import com.ayd2.congress.dtos.Congress.NewCongressRequest;
import com.ayd2.congress.dtos.Congress.UpdateCongress;
import com.ayd2.congress.dtos.User.UserResponse;
import com.ayd2.congress.exceptionhandler.ControllerExceptionHandler;
import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.exceptions.InvalidDateRangeException;
import com.ayd2.congress.exceptions.InvalidPriceException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.security.config.Security;
import com.ayd2.congress.security.jwtfilter.JwtAuthenticationFilter;
import com.ayd2.congress.services.Congress.CongressService;
import java.time.LocalDate;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = CongressController.class,
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
public class CongressControllerTest extends CommonMvcTest {

    private static final Long CONGRESS_ID = 1L;
    private static final Long ORGANIZATION_ID = 2L;
    private static final Long LOCATION_ID = 3L;
    private static final Long USER_ID = 4L;

    private static final String CONGRESS_NAME = "Tech Congress 2026";
    private static final String DESCRIPTION = "Congress description";
    private static final String IMAGE_URL = "base64-image";
    private static final Double PRICE = 250.0;

    private static final LocalDateTime END_CALL_DATE = LocalDateTime.of(2026, 3, 1, 8, 0);
    private static final LocalDateTime START_DATE = LocalDateTime.of(2026, 3, 10, 8, 0);
    private static final LocalDateTime END_DATE = LocalDateTime.of(2026, 3, 12, 18, 0);

    @MockitoBean
    private CongressService service;

    @Test
    public void testCreateCongress() throws Exception {
        // Arrange
        NewCongressRequest request = new NewCongressRequest(
                CONGRESS_NAME,
                DESCRIPTION,
                PRICE,
                ORGANIZATION_ID,
                LOCATION_ID,
                END_CALL_DATE,
                START_DATE,
                END_DATE,
                IMAGE_URL
        );

        CongressResponse response = new CongressResponse(
                CONGRESS_ID,
                CONGRESS_NAME,
                DESCRIPTION,
                PRICE,
                IMAGE_URL,
                START_DATE.toLocalDate(),
                END_DATE.toLocalDate(),
                END_CALL_DATE.toLocalDate(),
                "OpenAI",
                "Centro",
                LOCATION_ID
        );

        when(service.create(request)).thenReturn(response);

        // Act
        mockMvc.perform(post("/congresses")
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

        verify(service).create(request);
    }

    @Test
    public void testCreateCongressWhenNotFound() throws Exception {
        // Arrange
        NewCongressRequest request = new NewCongressRequest(
                CONGRESS_NAME,
                DESCRIPTION,
                PRICE,
                ORGANIZATION_ID,
                LOCATION_ID,
                END_CALL_DATE,
                START_DATE,
                END_DATE,
                IMAGE_URL
        );

        doThrow(new NotFoundException("Organization not found"))
                .when(service).create(request);

        // Act
        mockMvc.perform(post("/congresses")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isNotFound());

        verify(service).create(request);
    }

    @Test
    public void testCreateCongressWhenInvalidDateRange() throws Exception {
        // Arrange
        NewCongressRequest request = new NewCongressRequest(
                CONGRESS_NAME,
                DESCRIPTION,
                PRICE,
                ORGANIZATION_ID,
                LOCATION_ID,
                END_CALL_DATE,
                START_DATE,
                END_DATE,
                IMAGE_URL
        );

        doThrow(new InvalidDateRangeException("START DATE MUST BE BEFORE END DATE"))
                .when(service).create(request);

        // Act
        mockMvc.perform(post("/congresses")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isBadRequest());

        verify(service).create(request);
    }

    @Test
    public void testCreateCongressWhenInvalidPrice() throws Exception {
        // Arrange
        NewCongressRequest request = new NewCongressRequest(
                CONGRESS_NAME,
                DESCRIPTION,
                PRICE,
                ORGANIZATION_ID,
                LOCATION_ID,
                END_CALL_DATE,
                START_DATE,
                END_DATE,
                IMAGE_URL
        );

        doThrow(new InvalidPriceException("Invalid price"))
                .when(service).create(request);

        // Act
        mockMvc.perform(post("/congresses")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isBadRequest());

        verify(service).create(request);
    }

    @Test
    public void testCreateCongressWhenDuplicated() throws Exception {
        // Arrange
        NewCongressRequest request = new NewCongressRequest(
                CONGRESS_NAME,
                DESCRIPTION,
                PRICE,
                ORGANIZATION_ID,
                LOCATION_ID,
                END_CALL_DATE,
                START_DATE,
                END_DATE,
                IMAGE_URL
        );

        doThrow(new DuplicatedEntityException("Location occupied"))
                .when(service).create(request);

        // Act
        mockMvc.perform(post("/congresses")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isConflict());

        verify(service).create(request);
    }

    @Test
    public void testGetAllCongresses() throws Exception {
        // Arrange
        CongressResponse response = new CongressResponse(
                CONGRESS_ID,
                CONGRESS_NAME,
                DESCRIPTION,
                PRICE,
                IMAGE_URL,
                LocalDate.of(2026, 3, 10),
                LocalDate.of(2026, 3, 12),
                LocalDate.of(2026, 3, 1),
                "OpenAI",
                "Centro",
                LOCATION_ID
        );

        when(service.getAllCongress()).thenReturn(List.of(response));

        // Act
        mockMvc.perform(get("/congresses")
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
    public void testGetById() throws Exception {
        // Arrange
        CongressResponse response = new CongressResponse(
                CONGRESS_ID,
                CONGRESS_NAME,
                DESCRIPTION,
                PRICE,
                IMAGE_URL,
                LocalDate.of(2026, 3, 10),
                LocalDate.of(2026, 3, 12),
                LocalDate.of(2026, 3, 1),
                "OpenAI",
                "Centro",
                LOCATION_ID
        );

        when(service.getByIdResponse(CONGRESS_ID)).thenReturn(response);

        // Act
        mockMvc.perform(get("/congresses/{id}", CONGRESS_ID)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Assertions.assertThat(json)
                            .isEqualTo(objectMapper.writeValueAsString(response));
                });

        verify(service).getByIdResponse(CONGRESS_ID);
    }

    @Test
    public void testGetByIdWhenNotFound() throws Exception {
        // Arrange
        doThrow(new NotFoundException("Congress not found"))
                .when(service).getByIdResponse(CONGRESS_ID);

        // Act
        mockMvc.perform(get("/congresses/{id}", CONGRESS_ID)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNotFound());

        verify(service).getByIdResponse(CONGRESS_ID);
    }

    @Test
    public void testUpdateCongress() throws Exception {
        // Arrange
        UpdateCongress request = new UpdateCongress(
                "Updated Congress",
                "Updated Description",
                END_CALL_DATE.minusDays(1),
                "new-image",
                LOCATION_ID
        );

        CongressResponse response = new CongressResponse(
                CONGRESS_ID,
                "Updated Congress",
                "Updated Description",
                PRICE,
                IMAGE_URL,
                LocalDate.of(2026, 3, 10),
                LocalDate.of(2026, 3, 12),
                LocalDate.of(2026, 3, 1),
                "OpenAI",
                "Centro",
                LOCATION_ID
        );

        when(service.update(request, CONGRESS_ID)).thenReturn(response);

        // Act
        mockMvc.perform(put("/congresses/{id}", CONGRESS_ID)
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

        verify(service).update(request, CONGRESS_ID);
    }

    @Test
    public void testUpdateCongressWhenNotFound() throws Exception {
        // Arrange
        UpdateCongress request = new UpdateCongress(
                "Updated Congress",
                "Updated Description",
                END_CALL_DATE.minusDays(1),
                "new-image",
                LOCATION_ID
        );

        doThrow(new NotFoundException("Congress not found"))
                .when(service).update(request, CONGRESS_ID);

        // Act
        mockMvc.perform(put("/congresses/{id}", CONGRESS_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isNotFound());

        verify(service).update(request, CONGRESS_ID);
    }

    @Test
    public void testUpdateCongressWhenInvalidDateRange() throws Exception {
        // Arrange
        UpdateCongress request = new UpdateCongress(
                "Updated Congress",
                "Updated Description",
                END_CALL_DATE.minusDays(1),
                "new-image",
                LOCATION_ID
        );

        doThrow(new InvalidDateRangeException("DATE INVALID TO CALL TO APPLICATION"))
                .when(service).update(request, CONGRESS_ID);

        // Act
        mockMvc.perform(put("/congresses/{id}", CONGRESS_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isBadRequest());

        verify(service).update(request, CONGRESS_ID);
    }

    @Test
    public void testAddCommitteeMember() throws Exception {
        // Arrange
        NewCommitteeRequest request = new NewCommitteeRequest(USER_ID);

        // Act
        mockMvc.perform(post("/congresses/{congressId}/committee", CONGRESS_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isCreated());

        verify(service).createScientificCommittee(CONGRESS_ID, request);
    }

    @Test
    public void testAddCommitteeMemberWhenNotFound() throws Exception {
        // Arrange
        NewCommitteeRequest request = new NewCommitteeRequest(USER_ID);

        doThrow(new NotFoundException("User not found"))
                .when(service).createScientificCommittee(CONGRESS_ID, request);

        // Act
        mockMvc.perform(post("/congresses/{congressId}/committee", CONGRESS_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isNotFound());

        verify(service).createScientificCommittee(CONGRESS_ID, request);
    }

    @Test
    public void testAddCommitteeMemberWhenDuplicated() throws Exception {
        // Arrange
        NewCommitteeRequest request = new NewCommitteeRequest(USER_ID);

        doThrow(new DuplicatedEntityException("Duplicated committee member"))
                .when(service).createScientificCommittee(CONGRESS_ID, request);

        // Act
        mockMvc.perform(post("/congresses/{congressId}/committee", CONGRESS_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isConflict());

        verify(service).createScientificCommittee(CONGRESS_ID, request);
    }

    @Test
    public void testGetCommittee() throws Exception {
        // Arrange
        UserResponse response = new UserResponse(
                USER_ID,
                "1234567890101",
                "Juan",
                "Perez",
                "juan@mail.com",
                "55555555",
                "img",
                true,
                "Guatemalan",
                "OpenAI"
        );

        when(service.getCommitteByCongressId(CONGRESS_ID)).thenReturn(List.of(response));

        // Act
        mockMvc.perform(get("/congresses/{congressId}/committee", CONGRESS_ID)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Assertions.assertThat(json)
                            .isEqualTo(objectMapper.writeValueAsString(List.of(response)));
                });

        verify(service).getCommitteByCongressId(CONGRESS_ID);
    }

    @Test
    public void testGetCommitteeWhenNotFound() throws Exception {
        // Arrange
        doThrow(new NotFoundException("Congress not found"))
                .when(service).getCommitteByCongressId(CONGRESS_ID);

        // Act
        mockMvc.perform(get("/congresses/{congressId}/committee", CONGRESS_ID)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNotFound());

        verify(service).getCommitteByCongressId(CONGRESS_ID);
    }

    @Test
    public void testRemoveCommitteeMember() throws Exception {
        // Act
        mockMvc.perform(delete("/congresses/{congressId}/committee/{userId}", CONGRESS_ID, USER_ID)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNoContent());

        verify(service).removeCommitteeMember(CONGRESS_ID, USER_ID);
    }

    @Test
    public void testRemoveCommitteeMemberWhenNotFound() throws Exception {
        // Arrange
        doThrow(new NotFoundException("Registro no encontrado"))
                .when(service).removeCommitteeMember(CONGRESS_ID, USER_ID);

        // Act
        mockMvc.perform(delete("/congresses/{congressId}/committee/{userId}", CONGRESS_ID, USER_ID)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNotFound());

        verify(service).removeCommitteeMember(CONGRESS_ID, USER_ID);
    }
}