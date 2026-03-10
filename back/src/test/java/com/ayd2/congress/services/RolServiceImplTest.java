package com.ayd2.congress.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

import com.ayd2.congress.dtos.Rol.NewRolRequest;
import com.ayd2.congress.dtos.Rol.RolResponse;
import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.mappers.RolMapper;
import com.ayd2.congress.models.User.RolEntity;
import com.ayd2.congress.repositories.RolRepository;
import com.ayd2.congress.services.Rol.RolServiceImpl;

@ExtendWith(MockitoExtension.class)
public class RolServiceImplTest {

    private static final Long ROL_ID = 1L;
    private static final String ROL_NAME = "ADMIN";
    private static final String UPDATED_ROL_NAME = "USER";

    @Mock
    private RolRepository repository;

    @Mock
    private RolMapper mapper;

    @InjectMocks
    private RolServiceImpl rolService;

    private NewRolRequest newRolRequest;
    private RolEntity rolEntity;
    private RolResponse rolResponse;

    @BeforeEach
    void setUp() {
        newRolRequest = new NewRolRequest(ROL_NAME);

        rolEntity = new RolEntity();
        rolEntity.setId(ROL_ID);
        rolEntity.setName(ROL_NAME);

        rolResponse = new RolResponse(ROL_ID, ROL_NAME);
    }

    @Test
    void testCreateRol() throws Exception {
        // Arrange
        ArgumentCaptor<RolEntity> rolCaptor = ArgumentCaptor.forClass(RolEntity.class);

        when(repository.existsByName(ROL_NAME)).thenReturn(false);
        when(repository.save(any(RolEntity.class))).thenAnswer(invocation -> {
            RolEntity entity = invocation.getArgument(0);
            entity.setId(ROL_ID);
            return entity;
        });
        when(mapper.toResponseRol(any(RolEntity.class))).thenReturn(rolResponse);

        // Act
        RolResponse result = rolService.createRol(newRolRequest);

        // Assert
        assertAll(
                () -> verify(repository).save(rolCaptor.capture()),
                () -> assertEquals(ROL_NAME, rolCaptor.getValue().getName()),
                () -> assertEquals(ROL_ID, result.getId()),
                () -> assertEquals(ROL_NAME, result.getName())
        );
    }

    @Test
    void testCreateRolWhenDuplicated() {
        // Arrange
        when(repository.existsByName(ROL_NAME)).thenReturn(true);

        // Assert
        assertThrows(DuplicatedEntityException.class,
                () -> rolService.createRol(newRolRequest));
    }

    @Test
    void testGetRolById() throws Exception {
        // Arrange
        when(repository.findById(ROL_ID)).thenReturn(Optional.of(rolEntity));

        // Act
        RolEntity result = rolService.getRolById(ROL_ID);

        // Assert
        assertAll(
                () -> assertEquals(ROL_ID, result.getId()),
                () -> assertEquals(ROL_NAME, result.getName())
        );
    }

    @Test
    void testGetRolByIdWhenNotFound() {
        // Arrange
        when(repository.findById(ROL_ID)).thenReturn(Optional.empty());

        // Assert
        assertThrows(NotFoundException.class,
                () -> rolService.getRolById(ROL_ID));
    }

    @Test
    void testGetRolResponseById() throws Exception {
        // Arrange
        RolServiceImpl spy = spy(rolService);

        doReturn(rolEntity).when(spy).getRolById(ROL_ID);
        when(mapper.toResponseRol(rolEntity)).thenReturn(rolResponse);

        // Act
        RolResponse result = spy.getRolResponseById(ROL_ID);

        // Assert
        assertAll(
                () -> assertEquals(ROL_ID, result.getId()),
                () -> assertEquals(ROL_NAME, result.getName())
        );
    }

    @Test
    void testGetALLResponses() {
        // Arrange
        RolServiceImpl spy = spy(rolService);

        RolEntity secondRol = new RolEntity();
        secondRol.setId(2L);
        secondRol.setName(UPDATED_ROL_NAME);

        List<RolEntity> roles = List.of(rolEntity, secondRol);
        List<RolResponse> responses = List.of(
                rolResponse,
                new RolResponse(2L, UPDATED_ROL_NAME)
        );

        doReturn(roles).when(spy).getAllRols();
        when(mapper.toListResponseRol(roles)).thenReturn(responses);

        // Act
        List<RolResponse> result = spy.getALLResponses();

        // Assert
        assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertEquals(ROL_ID, result.get(0).getId()),
                () -> assertEquals(ROL_NAME, result.get(0).getName()),
                () -> assertEquals(2L, result.get(1).getId()),
                () -> assertEquals(UPDATED_ROL_NAME, result.get(1).getName())
        );
    }

    @Test
    void testGetAllRols() {
        // Arrange
        RolEntity secondRol = new RolEntity();
        secondRol.setId(2L);
        secondRol.setName(UPDATED_ROL_NAME);

        List<RolEntity> roles = List.of(rolEntity, secondRol);

        when(repository.findAll()).thenReturn(roles);

        // Act
        List<RolEntity> result = rolService.getAllRols();

        // Assert
        assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertEquals(ROL_ID, result.get(0).getId()),
                () -> assertEquals(ROL_NAME, result.get(0).getName()),
                () -> assertEquals(2L, result.get(1).getId()),
                () -> assertEquals(UPDATED_ROL_NAME, result.get(1).getName())
        );
    }
}