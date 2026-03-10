package com.ayd2.congress.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ayd2.congress.dtos.Organization.NewOrganizationRequest;
import com.ayd2.congress.dtos.Organization.OrganizationResponse;
import com.ayd2.congress.dtos.Organization.OrganizationUpdate;
import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.mappers.OrganizationMapper;
import com.ayd2.congress.models.Organization.OrganizationEntity;
import com.ayd2.congress.repositories.OrganizationRepository;
import com.ayd2.congress.services.Organization.OrganizationServiceImpl;
import com.ayd2.congress.services.aws.S3Service;

@ExtendWith(MockitoExtension.class)
public class OrganizationServiceImplTest {

    private static final Long ORGANIZATION_ID = 1L;
    private static final String ORGANIZATION_NAME = "USAC";
    private static final String ORGANIZATION_IMAGE_BASE64 = "base64-image";
    private static final String ORGANIZATION_IMAGE_URL = "https://bucket.s3.amazonaws.com/image_OpenAI";

    private static final String UPDATED_NAME = "NUEVA";
    private static final String UPDATED_IMAGE_BASE64 = "base64-updated-image";
    private static final String UPDATED_IMAGE_URL = "https://bucket.s3.amazonaws.com/image_Google";

    @Mock
    private OrganizationRepository repository;

    @Mock
    private OrganizationMapper mapper;

    @Mock
    private S3Service s3Service;

    @InjectMocks
    private OrganizationServiceImpl organizationService;

    private NewOrganizationRequest newOrganizationRequest;
    private OrganizationEntity organizationEntity;
    private OrganizationResponse organizationResponse;

    @BeforeEach
    void setUp() {
        newOrganizationRequest = new NewOrganizationRequest(
                ORGANIZATION_NAME,
                ORGANIZATION_IMAGE_BASE64
        );

        organizationEntity = new OrganizationEntity();
        organizationEntity.setId(ORGANIZATION_ID);
        organizationEntity.setName(ORGANIZATION_NAME);
        organizationEntity.setImage(ORGANIZATION_IMAGE_URL);
        organizationEntity.setCanCreateCongress(false);

        organizationResponse = new OrganizationResponse(
                ORGANIZATION_ID,
                ORGANIZATION_NAME,
                ORGANIZATION_IMAGE_URL,
                false
        );
    }

    @Test
    void testCreateOrganization() throws Exception {
        // Arrange
        ArgumentCaptor<OrganizationEntity> organizationCaptor = ArgumentCaptor.forClass(OrganizationEntity.class);

        OrganizationEntity mappedEntity = new OrganizationEntity();
        mappedEntity.setName(ORGANIZATION_NAME);

        when(repository.existsByName(ORGANIZATION_NAME)).thenReturn(false);
        when(mapper.toEntity(newOrganizationRequest)).thenReturn(mappedEntity);
        when(s3Service.uploadBase64(ORGANIZATION_IMAGE_BASE64, "image_" + ORGANIZATION_NAME))
                .thenReturn(ORGANIZATION_IMAGE_URL);
        when(repository.save(any(OrganizationEntity.class))).thenAnswer(invocation -> {
            OrganizationEntity entity = invocation.getArgument(0);
            entity.setId(ORGANIZATION_ID);
            return entity;
        });
        when(mapper.toResponse(any(OrganizationEntity.class))).thenReturn(organizationResponse);

        // Act
        OrganizationResponse result = organizationService.create(newOrganizationRequest);

        // Assert
        assertAll(
                () -> verify(repository).save(organizationCaptor.capture()),
                () -> assertEquals(ORGANIZATION_NAME, organizationCaptor.getValue().getName()),
                () -> assertEquals(ORGANIZATION_IMAGE_URL, organizationCaptor.getValue().getImage()),
                () -> assertFalse(organizationCaptor.getValue().isCanCreateCongress()),
                () -> assertEquals(ORGANIZATION_ID, result.getId()),
                () -> assertEquals(ORGANIZATION_NAME, result.getName()),
                () -> assertEquals(ORGANIZATION_IMAGE_URL, result.getImage()),
                () -> assertFalse(result.isCanCreateCongress())
        );
    }

    @Test
    void testCreateOrganizationWhenDuplicatedName() {
        // Arrange
        when(repository.existsByName(ORGANIZATION_NAME)).thenReturn(true);

        // Assert
        assertThrows(DuplicatedEntityException.class,
                () -> organizationService.create(newOrganizationRequest));
    }

    @Test
    void testGetById() throws Exception {
        // Arrange
        when(repository.findById(ORGANIZATION_ID)).thenReturn(Optional.of(organizationEntity));

        // Act
        OrganizationEntity result = organizationService.getById(ORGANIZATION_ID);

        // Assert
        assertAll(
                () -> assertEquals(ORGANIZATION_ID, result.getId()),
                () -> assertEquals(ORGANIZATION_NAME, result.getName()),
                () -> assertEquals(ORGANIZATION_IMAGE_URL, result.getImage())
        );
    }

    @Test
    void testGetByIdWhenNotFound() {
        // Arrange
        when(repository.findById(ORGANIZATION_ID)).thenReturn(Optional.empty());

        // Assert
        assertThrows(NotFoundException.class,
                () -> organizationService.getById(ORGANIZATION_ID));
    }

    @Test
    void testGetAll() {
        // Arrange
        List<OrganizationEntity> entities = List.of(organizationEntity);
        List<OrganizationResponse> responses = List.of(organizationResponse);

        when(repository.findAll()).thenReturn(entities);
        when(mapper.toResponseList(entities)).thenReturn(responses);

        // Act
        List<OrganizationResponse> result = organizationService.getAll();

        // Assert
        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(ORGANIZATION_ID, result.get(0).getId()),
                () -> assertEquals(ORGANIZATION_NAME, result.get(0).getName())
        );
    }

    @Test
    void testUpdateOrganization() throws Exception {
        // Arrange
        OrganizationUpdate request = new OrganizationUpdate(
                UPDATED_NAME,
                UPDATED_IMAGE_BASE64,
                true
        );

        OrganizationServiceImpl spy = spy(organizationService);
        ArgumentCaptor<OrganizationEntity> organizationCaptor = ArgumentCaptor.forClass(OrganizationEntity.class);

        doReturn(organizationEntity).when(spy).getById(ORGANIZATION_ID);
        when(repository.existsByNameAndIdNot(UPDATED_NAME, ORGANIZATION_ID)).thenReturn(false);
        when(s3Service.uploadBase64(UPDATED_IMAGE_BASE64, "image_" + UPDATED_NAME)).thenReturn(UPDATED_IMAGE_URL);

        OrganizationResponse updatedResponse = new OrganizationResponse(
                ORGANIZATION_ID,
                UPDATED_NAME,
                UPDATED_IMAGE_URL,
                true
        );
        when(mapper.toResponse(any(OrganizationEntity.class))).thenReturn(updatedResponse);

        // Act
        OrganizationResponse result = spy.update(request, ORGANIZATION_ID);

        // Assert
        assertAll(
                () -> verify(repository).save(organizationCaptor.capture()),
                () -> assertEquals(UPDATED_NAME, organizationCaptor.getValue().getName()),
                () -> assertEquals(UPDATED_IMAGE_URL, organizationCaptor.getValue().getImage()),
                () -> assertEquals(true, organizationCaptor.getValue().isCanCreateCongress()),
                () -> assertEquals(ORGANIZATION_ID, result.getId()),
                () -> assertEquals(UPDATED_NAME, result.getName()),
                () -> assertEquals(UPDATED_IMAGE_URL, result.getImage()),
                () -> assertEquals(true, result.isCanCreateCongress())
        );
    }

    @Test
    void testUpdateOrganizationWhenDuplicatedName() throws Exception {
        // Arrange
        OrganizationUpdate request = new OrganizationUpdate(
                UPDATED_NAME,
                UPDATED_IMAGE_BASE64,
                true
        );

        OrganizationServiceImpl spy = spy(organizationService);
        doReturn(organizationEntity).when(spy).getById(ORGANIZATION_ID);
        when(repository.existsByNameAndIdNot(UPDATED_NAME, ORGANIZATION_ID)).thenReturn(true);

        // Assert
        assertThrows(DuplicatedEntityException.class,
                () -> spy.update(request, ORGANIZATION_ID));
    }

    @Test
    void testGetByIdResponse() throws Exception {
        // Arrange
        OrganizationServiceImpl spy = spy(organizationService);

        doReturn(organizationEntity).when(spy).getById(ORGANIZATION_ID);
        when(mapper.toResponse(organizationEntity)).thenReturn(organizationResponse);

        // Act
        OrganizationResponse result = spy.getByIdResponse(ORGANIZATION_ID);

        // Assert
        assertAll(
                () -> assertEquals(ORGANIZATION_ID, result.getId()),
                () -> assertEquals(ORGANIZATION_NAME, result.getName()),
                () -> assertEquals(ORGANIZATION_IMAGE_URL, result.getImage()),
                () -> assertFalse(result.isCanCreateCongress())
        );
    }
}
