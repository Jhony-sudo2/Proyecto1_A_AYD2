package com.ayd2.congress.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

import com.ayd2.congress.dtos.Rol.NewRolRequest;
import com.ayd2.congress.dtos.Rol.RolResponse;
import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.models.User.RolEntity;
import com.ayd2.congress.repositories.RolRepository;
import com.ayd2.congress.services.Rol.RolServiceImpl;

@ExtendWith(MockitoExtension.class)
public class RolServiceImplTest {
    private final static String NAME_ROL = "ADMIN";
    private final static Long ROL_ID = 1L;
    @Mock
    private RolRepository repository;
    @InjectMocks
    private RolServiceImpl service;

    @Test
    void CreateRolTest() throws DuplicatedEntityException {
        NewRolRequest newRolRequest = new NewRolRequest(NAME_ROL);
        ArgumentCaptor<RolEntity> rolCapute = ArgumentCaptor.forClass(RolEntity.class);
        RolEntity entity = new RolEntity();
        entity.setName(NAME_ROL);
        when(repository.save(eq(entity))).thenReturn(entity);
        // Act
        RolResponse result = service.createRol(newRolRequest);
        assertAll(
                () -> verify(repository).save(rolCapute.capture()),
                () -> assertEquals(NAME_ROL, result.getName()),
                () -> assertEquals(NAME_ROL, rolCapute.getValue().getName()));
    }

    @Test
    void createRolWhenDuplicatedNameTest() {
        // Arrange
        NewRolRequest newRol = new NewRolRequest(NAME_ROL);
        when(repository.existByName(NAME_ROL))
                .thenReturn(true);

        Assertions.assertThrows(DuplicatedEntityException.class,
                () -> service.createRol(newRol));
    }

    @Test
    void getRolByIdTest() throws NotFoundException {
        // Arrange
        RolEntity entity = new RolEntity();
        entity.setId(ROL_ID);
        entity.setName(NAME_ROL);
        when(repository.existsById(ROL_ID))
            .thenReturn(true);
        when(repository.findById(ROL_ID))
            .thenReturn(Optional.of(entity));
        // Act
        RolResponse result = service.getRolById(ROL_ID);
        // Assert
        assertAll(
                () -> verify(repository).findById(ROL_ID),
                () -> assertNotNull(result),
                () -> assertEquals(NAME_ROL, result.getName()),
                () -> assertEquals(ROL_ID, result.getId()) 
        );
    }

    @Test
    void getRolByIdNotFounTest(){
        when(repository.existsById(ROL_ID))
            .thenReturn(false);
        
        Assertions.assertThrows(NotFoundException.class, 
            ()->service.getRolById(ROL_ID));
    }

    @Test
    void getAllRolsTest(){
        RolEntity entity = new RolEntity();
        entity.setId(ROL_ID);
        entity.setName(NAME_ROL);
        List<RolEntity> list = List.of(entity);
        when(repository.findAll()).thenReturn(list);
        List<RolEntity> result = service.getAllRols();
        assertAll(
            ()-> assertEquals(list, result)
        );
    }
}
