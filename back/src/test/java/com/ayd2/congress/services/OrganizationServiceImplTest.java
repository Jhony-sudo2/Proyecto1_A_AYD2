package com.ayd2.congress.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
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
import com.ayd2.congress.models.Organization.OrganizationEntity;
import com.ayd2.congress.repositories.OrganizationRepository;
import com.ayd2.congress.services.Organization.OrganizationServiceImpl;

@ExtendWith(MockitoExtension.class)
public class OrganizationServiceImplTest {
    private final static String ORGANIZATION_NAME = "EXAMPLE";
    private final static String ORGANIZATION_IMAGEN = "EXAMPLE.PNG";
    private final static Long ORGANIZATION_ID = 1L;
    @Mock
    private OrganizationRepository repository;
    @InjectMocks
    private OrganizationServiceImpl service;

    @Test
    void createOrganizationTest(){
        NewOrganizationRequest newOrganizationRequest = new NewOrganizationRequest(ORGANIZATION_NAME,ORGANIZATION_NAME);
        ArgumentCaptor<OrganizationEntity> organizationCapture = ArgumentCaptor.forClass(OrganizationEntity.class);
        OrganizationEntity newOrganizationEntity = new OrganizationEntity();
        newOrganizationEntity.setImage(ORGANIZATION_IMAGEN);
        newOrganizationEntity.setName(ORGANIZATION_NAME);
        when(repository.save(eq(newOrganizationEntity))).thenReturn(newOrganizationEntity);
        OrganizationResponse result = service.create(newOrganizationRequest);
        assertAll(
            ()-> verify(repository).save(organizationCapture.capture()),
            ()-> assertEquals(ORGANIZATION_NAME, organizationCapture.getValue().getName()),
            ()-> assertEquals(ORGANIZATION_IMAGEN, organizationCapture.getValue().getImage()),
            ()-> assertEquals(ORGANIZATION_NAME, result.getName()),
            ()-> assertEquals(ORGANIZATION_IMAGEN, result.getImage())
        );
    }

    @Test
    void createWhenDuplicatedNameTest(){
        NewOrganizationRequest newOrganizationRequest = new NewOrganizationRequest(ORGANIZATION_NAME,ORGANIZATION_NAME);

        when(repository.existByName(ORGANIZATION_NAME)).thenReturn(true);

        Assertions.assertThrows(DuplicatedEntityException.class, 
            ()-> service.create(newOrganizationRequest));
    }   

    @Test
    void getByIdTest(){
        OrganizationEntity entity = new OrganizationEntity();
        entity.setId(ORGANIZATION_ID); 
        entity.setImage(ORGANIZATION_IMAGEN);
        entity.setName(ORGANIZATION_NAME);
        entity.setCanCreateCongress(true);
        
        when(repository.findById(ORGANIZATION_ID)).thenReturn(Optional.of(entity));

        OrganizationResponse result = service.getById(ORGANIZATION_ID);

        assertAll(
            ()-> verify(repository.findById(ORGANIZATION_ID)),
            ()-> assertEquals(ORGANIZATION_ID, result.getId()),
            ()-> assertEquals(ORGANIZATION_NAME, result.getName()),
            ()-> assertEquals(ORGANIZATION_IMAGEN, result.getImage()),
            ()-> assertEquals(true, result.isCanCreateCongress())
        );
    }

    @Test
    void getByIdNotFoundTest(){
        when(repository.existsById(ORGANIZATION_ID)).thenReturn(false);

        Assertions.assertThrows(RuntimeException.class, 
            ()-> service.getById(ORGANIZATION_ID));
    }

    @Test
    void getAllTest(){
        OrganizationEntity entity = new OrganizationEntity();
        entity.setId(ORGANIZATION_ID); 
        entity.setImage(ORGANIZATION_IMAGEN);
        entity.setName(ORGANIZATION_NAME);
        entity.setCanCreateCongress(true);

        when(repository.findAll()).thenReturn(List.of(entity));

        List<OrganizationResponse> result = service.getAll();

        assertAll(
            ()-> verify(repository).findAll(),
            ()-> assertEquals(1, result.size()),
            ()-> assertEquals(ORGANIZATION_ID, result.get(0).getId()),
            ()-> assertEquals(ORGANIZATION_NAME, result.get(0).getName()),
            ()-> assertEquals(ORGANIZATION_IMAGEN, result.get(0).getImage()),
            ()-> assertEquals(true, result.get(0).isCanCreateCongress())
        );
    }

    @Test
    void updateTest() throws NotFoundException, DuplicatedEntityException{
        OrganizationEntity entity = new OrganizationEntity();
        entity.setId(ORGANIZATION_ID); 
        entity.setImage(ORGANIZATION_IMAGEN);
        entity.setName(ORGANIZATION_NAME);
        entity.setCanCreateCongress(true);

        when(repository.findById(ORGANIZATION_ID)).thenReturn(Optional.of(entity));

        OrganizationResponse result = service.update(new OrganizationUpdate(ORGANIZATION_NAME, ORGANIZATION_IMAGEN, true), ORGANIZATION_ID);

        assertAll(
            ()-> verify(repository).findById(ORGANIZATION_ID),
            ()-> assertEquals(ORGANIZATION_ID, result.getId()),
            ()-> assertEquals(ORGANIZATION_NAME, result.getName()),
            ()-> assertEquals(ORGANIZATION_IMAGEN, result.getImage()),
            ()-> assertEquals(true, result.isCanCreateCongress())
        );
    }

    @Test
    void updateWhenNameDuplicatedTest() throws NotFoundException{
        OrganizationEntity entity = new OrganizationEntity();
        entity.setId(ORGANIZATION_ID); 
        entity.setImage(ORGANIZATION_IMAGEN);
        entity.setName(ORGANIZATION_NAME);
        entity.setCanCreateCongress(true);

        when(repository.findById(ORGANIZATION_ID)).thenReturn(Optional.of(entity));
        when(repository.existByNameAndIdNot(ORGANIZATION_NAME, ORGANIZATION_ID)).thenReturn(true);

        Assertions.assertThrows(DuplicatedEntityException.class, 
            ()-> service.update(new OrganizationUpdate(ORGANIZATION_NAME, ORGANIZATION_IMAGEN, true), ORGANIZATION_ID));
    }

}
