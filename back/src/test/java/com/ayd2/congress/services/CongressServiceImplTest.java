package com.ayd2.congress.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ayd2.congress.compositePrimaryKeys.CommiteeId;
import com.ayd2.congress.dtos.Congress.CongressResponse;
import com.ayd2.congress.dtos.Congress.NewCommitteeRequest;
import com.ayd2.congress.dtos.Congress.NewCongressRequest;
import com.ayd2.congress.dtos.Congress.UpdateCongress;
import com.ayd2.congress.dtos.User.UserResponse;
import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.exceptions.InvalidDateRangeException;
import com.ayd2.congress.exceptions.InvalidPriceException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.mappers.CongressMapper;
import com.ayd2.congress.mappers.UserMapper;
import com.ayd2.congress.models.Congress.CongressEntity;
import com.ayd2.congress.models.Congress.LocationEntity;
import com.ayd2.congress.models.Congress.ScientificCommitteeEntity;
import com.ayd2.congress.models.Organization.OrganizationEntity;
import com.ayd2.congress.models.SystemConfigEntity;
import com.ayd2.congress.models.User.UserEntity;
import com.ayd2.congress.repositories.Congress.CommiteeRepository;
import com.ayd2.congress.repositories.CongressRepository;
import com.ayd2.congress.services.Congress.CongressServiceImpl;
import com.ayd2.congress.services.Location.LocationService;
import com.ayd2.congress.services.Organization.OrganizationService;
import com.ayd2.congress.services.User.UserService;
import com.ayd2.congress.services.aws.S3Service;
import com.ayd2.congress.services.systemconfig.SystemConfigService;

@ExtendWith(MockitoExtension.class)
public class CongressServiceImplTest {

    private static final Long CONGRESS_ID = 1L;
    private static final Long ORGANIZATION_ID = 2L;
    private static final Long LOCATION_ID = 3L;
    private static final Long USER_ID = 4L;

    private static final String CONGRESS_NAME = "Tech Congress 2026";
    private static final String CONGRESS_DESCRIPTION = "Congress about technology";
    private static final String IMAGE_BASE64 = "base64-image";
    private static final String IMAGE_URL = "https://bucket.s3.amazonaws.com/congress_TechCongress";

    private static final Double CONGRESS_PRICE = 250.0;
    private static final Double MIN_PRICE = 100.0;
    private static final Double INVALID_PRICE = 50.0;

    private static final LocalDateTime END_CALL_DATE = LocalDateTime.of(2026, 3, 1, 8, 0);
    private static final LocalDateTime START_DATE = LocalDateTime.of(2026, 3, 10, 8, 0);
    private static final LocalDateTime END_DATE = LocalDateTime.of(2026, 3, 12, 18, 0);

    private static final String USER_NAME = "Juan";
    private static final String USER_LAST_NAME = "Perez";
    private static final String USER_EMAIL = "juan@mail.com";

    @Mock
    private CongressRepository congressRepository;
    @Mock
    private CongressMapper congressMapper;
    @Mock
    private OrganizationService organizationService;
    @Mock
    private LocationService locationService;
    @Mock
    private SystemConfigService systemConfigService;
    @Mock
    private CommiteeRepository commiteeRepository;
    @Mock
    private UserService userService;
    @Mock
    private S3Service s3Service;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private CongressServiceImpl congressService;

    private NewCongressRequest newCongressRequest;
    private CongressEntity congressEntity;
    private CongressResponse congressResponse;
    private OrganizationEntity organizationEntity;
    private LocationEntity locationEntity;
    private SystemConfigEntity systemConfigEntity;
    private UserEntity userEntity;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        newCongressRequest = new NewCongressRequest(
                CONGRESS_NAME,
                CONGRESS_DESCRIPTION,
                CONGRESS_PRICE,
                ORGANIZATION_ID,
                LOCATION_ID,
                END_CALL_DATE,
                START_DATE,
                END_DATE,
                IMAGE_BASE64
        );

        organizationEntity = new OrganizationEntity();
        organizationEntity.setId(ORGANIZATION_ID);
        organizationEntity.setName("OpenAI");

        locationEntity = new LocationEntity();
        locationEntity.setId(LOCATION_ID);
        locationEntity.setName("Centro de Convenciones");

        systemConfigEntity = new SystemConfigEntity();
        systemConfigEntity.setId(1L);
        systemConfigEntity.setPrice(MIN_PRICE);

        congressEntity = new CongressEntity();
        congressEntity.setId(CONGRESS_ID);
        congressEntity.setName(CONGRESS_NAME);
        congressEntity.setDescription(CONGRESS_DESCRIPTION);
        congressEntity.setPrice(CONGRESS_PRICE);
        congressEntity.setImageUrl(IMAGE_URL);
        congressEntity.setStartDate(START_DATE);
        congressEntity.setEndDate(END_DATE);
        congressEntity.setEndCallDate(END_CALL_DATE);
        congressEntity.setOrganization(organizationEntity);
        congressEntity.setLocation(locationEntity);

        congressResponse = new CongressResponse(
                CONGRESS_ID,
                CONGRESS_NAME,
                CONGRESS_DESCRIPTION,
                CONGRESS_PRICE,
                IMAGE_URL,
                START_DATE.toLocalDate(),
                END_DATE.toLocalDate(),
                END_CALL_DATE.toLocalDate(),
                "OpenAI",
                "Centro de Convenciones",
                LOCATION_ID
        );

        userEntity = new UserEntity();
        userEntity.setId(USER_ID);
        userEntity.setName(USER_NAME);
        userEntity.setLastName(USER_LAST_NAME);
        userEntity.setEmail(USER_EMAIL);

        userResponse = new UserResponse(
                USER_ID,
                "1234567890101",
                USER_NAME,
                USER_LAST_NAME,
                USER_EMAIL,
                "55555555",
                "img",
                true,
                "GT",
                "OpenAI"
        );
    }

    @Test
    void testCreateCongress() throws Exception {
        // Arrange
        ArgumentCaptor<CongressEntity> congressCaptor = ArgumentCaptor.forClass(CongressEntity.class);

        CongressEntity mappedEntity = new CongressEntity();
        mappedEntity.setName(CONGRESS_NAME);
        mappedEntity.setDescription(CONGRESS_DESCRIPTION);
        mappedEntity.setPrice(CONGRESS_PRICE);
        mappedEntity.setStartDate(START_DATE);
        mappedEntity.setEndDate(END_DATE);
        mappedEntity.setEndCallDate(END_CALL_DATE);

        when(organizationService.getById(ORGANIZATION_ID)).thenReturn(organizationEntity);
        when(locationService.getLocationById(LOCATION_ID)).thenReturn(locationEntity);
        when(systemConfigService.getConfiguration()).thenReturn(systemConfigEntity);
        when(congressRepository.isLocationOccupied(LOCATION_ID, START_DATE, END_DATE)).thenReturn(false);
        when(s3Service.uploadBase64(IMAGE_BASE64, "congress_" + CONGRESS_NAME)).thenReturn(IMAGE_URL);
        when(congressMapper.toEntity(newCongressRequest)).thenReturn(mappedEntity);
        when(congressMapper.toResponse(any(CongressEntity.class))).thenReturn(congressResponse);

        // Act
        CongressResponse result = congressService.create(newCongressRequest);

        // Assert
        assertAll(
                () -> verify(congressRepository).save(congressCaptor.capture()),
                () -> assertEquals(LOCATION_ID, congressCaptor.getValue().getLocation().getId()),
                () -> assertEquals(ORGANIZATION_ID, congressCaptor.getValue().getOrganization().getId()),
                () -> assertEquals(IMAGE_URL, congressCaptor.getValue().getImageUrl()),
                () -> assertEquals(CONGRESS_ID, result.getId()),
                () -> assertEquals(CONGRESS_NAME, result.getName()),
                () -> assertEquals(CONGRESS_PRICE, result.getPrice())
        );
    }

    @Test
    void testCreateCongressWhenStartDateIsAfterEndDate() throws Exception {
        // Arrange
        NewCongressRequest request = new NewCongressRequest(
                CONGRESS_NAME,
                CONGRESS_DESCRIPTION,
                CONGRESS_PRICE,
                ORGANIZATION_ID,
                LOCATION_ID,
                END_CALL_DATE,
                END_DATE,
                START_DATE,
                IMAGE_BASE64
        );

        when(organizationService.getById(ORGANIZATION_ID)).thenReturn(organizationEntity);
        when(locationService.getLocationById(LOCATION_ID)).thenReturn(locationEntity);
        when(systemConfigService.getConfiguration()).thenReturn(systemConfigEntity);

        // Assert
        assertThrows(InvalidDateRangeException.class,
                () -> congressService.create(request));
    }

    @Test
    void testCreateCongressWhenEndCallDateIsInvalid() throws Exception {
        // Arrange
        NewCongressRequest request = new NewCongressRequest(
                CONGRESS_NAME,
                CONGRESS_DESCRIPTION,
                CONGRESS_PRICE,
                ORGANIZATION_ID,
                LOCATION_ID,
                START_DATE,
                START_DATE,
                END_DATE,
                IMAGE_BASE64
        );

        when(organizationService.getById(ORGANIZATION_ID)).thenReturn(organizationEntity);
        when(locationService.getLocationById(LOCATION_ID)).thenReturn(locationEntity);
        when(systemConfigService.getConfiguration()).thenReturn(systemConfigEntity);

        // Assert
        assertThrows(InvalidDateRangeException.class,
                () -> congressService.create(request));
    }

    @Test
    void testCreateCongressWhenPriceIsLowerThanMinimum() throws Exception {
        // Arrange
        NewCongressRequest request = new NewCongressRequest(
                CONGRESS_NAME,
                CONGRESS_DESCRIPTION,
                INVALID_PRICE,
                ORGANIZATION_ID,
                LOCATION_ID,
                END_CALL_DATE,
                START_DATE,
                END_DATE,
                IMAGE_BASE64
        );

        when(organizationService.getById(ORGANIZATION_ID)).thenReturn(organizationEntity);
        when(locationService.getLocationById(LOCATION_ID)).thenReturn(locationEntity);
        when(systemConfigService.getConfiguration()).thenReturn(systemConfigEntity);

        // Assert
        assertThrows(InvalidPriceException.class,
                () -> congressService.create(request));
    }

    @Test
    void testCreateCongressWhenLocationIsOccupied() throws Exception {
        // Arrange
        when(organizationService.getById(ORGANIZATION_ID)).thenReturn(organizationEntity);
        when(locationService.getLocationById(LOCATION_ID)).thenReturn(locationEntity);
        when(systemConfigService.getConfiguration()).thenReturn(systemConfigEntity);
        when(congressRepository.isLocationOccupied(LOCATION_ID, START_DATE, END_DATE)).thenReturn(true);

        // Assert
        assertThrows(DuplicatedEntityException.class,
                () -> congressService.create(newCongressRequest));
    }

    @Test
    void testUpdateCongress() throws Exception {
        // Arrange
        UpdateCongress request = new UpdateCongress(
                "Updated Congress",
                "Updated Description",
                END_CALL_DATE.minusDays(1),
                "new-image",
                LOCATION_ID
        );

        CongressServiceImpl spy = spy(congressService);
        ArgumentCaptor<CongressEntity> congressCaptor = ArgumentCaptor.forClass(CongressEntity.class);

        doReturn(congressEntity).when(spy).getById(CONGRESS_ID);
        when(locationService.getLocationById(LOCATION_ID)).thenReturn(locationEntity);

        CongressResponse updatedResponse = new CongressResponse(
                CONGRESS_ID,
                "Updated Congress",
                "Updated Description",
                CONGRESS_PRICE,
                IMAGE_URL,
                START_DATE.toLocalDate(),
                END_DATE.toLocalDate(),
                END_CALL_DATE.toLocalDate(),
                "OpenAI",
                "Centro de Convenciones",
                LOCATION_ID
        );
        when(congressMapper.toResponse(any(CongressEntity.class))).thenReturn(updatedResponse);

        // Act
        CongressResponse result = spy.update(request, CONGRESS_ID);

        // Assert
        assertAll(
                () -> verify(congressRepository).save(congressCaptor.capture()),
                () -> assertEquals("Updated Congress", congressCaptor.getValue().getName()),
                () -> assertEquals("Updated Description", congressCaptor.getValue().getDescription()),
                () -> assertEquals(LOCATION_ID, congressCaptor.getValue().getLocation().getId()),
                () -> assertEquals(CONGRESS_ID, result.getId()),
                () -> assertEquals("Updated Congress", result.getName())
        );
    }

    @Test
    void testUpdateCongressWhenEndCallDateIsInvalid() throws Exception {
        // Arrange
        UpdateCongress request = new UpdateCongress(
                "Updated Congress",
                "Updated Description",
                START_DATE.plusDays(1),
                "new-image",
                LOCATION_ID
        );

        CongressServiceImpl spy = spy(congressService);
        doReturn(congressEntity).when(spy).getById(CONGRESS_ID);
        when(locationService.getLocationById(LOCATION_ID)).thenReturn(locationEntity);

        // Assert
        assertThrows(InvalidDateRangeException.class,
                () -> spy.update(request, CONGRESS_ID));
    }

    @Test
    void testGetById() throws Exception {
        // Arrange
        when(congressRepository.findById(CONGRESS_ID)).thenReturn(Optional.of(congressEntity));

        // Act
        CongressEntity result = congressService.getById(CONGRESS_ID);

        // Assert
        assertAll(
                () -> assertEquals(CONGRESS_ID, result.getId()),
                () -> assertEquals(CONGRESS_NAME, result.getName())
        );
    }

    @Test
    void testGetByIdWhenNotFound() {
        // Arrange
        when(congressRepository.findById(CONGRESS_ID)).thenReturn(Optional.empty());

        // Assert
        assertThrows(NotFoundException.class,
                () -> congressService.getById(CONGRESS_ID));
    }

    @Test
    void testGetAllByOrganizationId() throws Exception {
        // Arrange
        List<CongressEntity> entities = List.of(congressEntity);
        List<CongressResponse> responses = List.of(congressResponse);

        when(organizationService.getById(ORGANIZATION_ID)).thenReturn(organizationEntity);
        when(congressRepository.findAllByOrganizationId(ORGANIZATION_ID)).thenReturn(entities);
        when(congressMapper.toCongressResponseList(entities)).thenReturn(responses);

        // Act
        List<CongressResponse> result = congressService.getAllByOrganizationId(ORGANIZATION_ID);

        // Assert
        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(CONGRESS_ID, result.get(0).getId()),
                () -> assertEquals(CONGRESS_NAME, result.get(0).getName())
        );
    }

    @Test
    void testGetByIdResponse() throws Exception {
        // Arrange
        CongressServiceImpl spy = spy(congressService);

        doReturn(congressEntity).when(spy).getById(CONGRESS_ID);
        when(congressMapper.toResponse(congressEntity)).thenReturn(congressResponse);

        // Act
        CongressResponse result = spy.getByIdResponse(CONGRESS_ID);

        // Assert
        assertAll(
                () -> assertEquals(CONGRESS_ID, result.getId()),
                () -> assertEquals(CONGRESS_NAME, result.getName())
        );
    }

    @Test
    void testGetAllCongress() {
        // Arrange
        List<CongressEntity> entities = List.of(congressEntity);
        List<CongressResponse> responses = List.of(congressResponse);

        when(congressRepository.findAll()).thenReturn(entities);
        when(congressMapper.toCongressResponseList(entities)).thenReturn(responses);

        // Act
        List<CongressResponse> result = congressService.getAllCongress();

        // Assert
        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(CONGRESS_ID, result.get(0).getId())
        );
    }

    @Test
    void testCreateScientificCommittee() throws Exception {
        // Arrange
        NewCommitteeRequest request = new NewCommitteeRequest(USER_ID);
        ArgumentCaptor<ScientificCommitteeEntity> committeeCaptor =
                ArgumentCaptor.forClass(ScientificCommitteeEntity.class);

        CongressServiceImpl spy = spy(congressService);
        doReturn(congressEntity).when(spy).getById(CONGRESS_ID);
        when(userService.getById(USER_ID)).thenReturn(userEntity);
        when(commiteeRepository.existsByUserIdAndCongressId(USER_ID, CONGRESS_ID)).thenReturn(false);

        // Act
        spy.createScientificCommittee(CONGRESS_ID, request);

        // Assert
        assertAll(
                () -> verify(userService).updateRol(2L, USER_ID),
                () -> verify(commiteeRepository).save(committeeCaptor.capture()),
                () -> assertEquals(CONGRESS_ID, committeeCaptor.getValue().getCongress().getId()),
                () -> assertEquals(USER_ID, committeeCaptor.getValue().getUser().getId()),
                () -> assertEquals(new CommiteeId(CONGRESS_ID, USER_ID), committeeCaptor.getValue().getId())
        );
    }

    @Test
    void testCreateScientificCommitteeWhenDuplicated() throws Exception {
        // Arrange
        NewCommitteeRequest request = new NewCommitteeRequest(USER_ID);

        CongressServiceImpl spy = spy(congressService);
        doReturn(congressEntity).when(spy).getById(CONGRESS_ID);
        when(userService.getById(USER_ID)).thenReturn(userEntity);
        when(commiteeRepository.existsByUserIdAndCongressId(USER_ID, CONGRESS_ID)).thenReturn(true);

        // Assert
        assertThrows(DuplicatedEntityException.class,
                () -> spy.createScientificCommittee(CONGRESS_ID, request));
    }

    @Test
    void testGetCommitteByCongressId() throws Exception {
        // Arrange
        ScientificCommitteeEntity committeeEntity = new ScientificCommitteeEntity();
        committeeEntity.setCongress(congressEntity);
        committeeEntity.setUser(userEntity);
        committeeEntity.setId(new CommiteeId(CONGRESS_ID, USER_ID));

        List<ScientificCommitteeEntity> committeeList = List.of(committeeEntity);
        List<UserResponse> userResponses = List.of(userResponse);

        CongressServiceImpl spy = spy(congressService);
        doReturn(congressEntity).when(spy).getById(CONGRESS_ID);
        when(commiteeRepository.findAllByCongressId(CONGRESS_ID)).thenReturn(committeeList);
        when(userMapper.toResponseList(List.of(userEntity))).thenReturn(userResponses);

        // Act
        List<UserResponse> result = spy.getCommitteByCongressId(CONGRESS_ID);

        // Assert
        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(USER_ID, result.get(0).getId()),
                () -> assertEquals(USER_NAME, result.get(0).getName())
        );
    }


}