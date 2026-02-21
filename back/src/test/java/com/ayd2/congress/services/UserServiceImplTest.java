package com.ayd2.congress.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ayd2.congress.dtos.User.NewUserRequest;
import com.ayd2.congress.dtos.User.UserResponse;
import com.ayd2.congress.dtos.User.UserUpdate;
import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.models.Organization.OrganizationEntity;
import com.ayd2.congress.models.User.RolEntity;
import com.ayd2.congress.models.User.UserEntity;
import com.ayd2.congress.repositories.UserRepository;
import com.ayd2.congress.services.Organization.OrganizationServiceImpl;
import com.ayd2.congress.services.Rol.RolServiceImpl;
import com.ayd2.congress.services.User.UserServiceImpl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    private static final Long USER_ID = 1L;
    private static final String USER_NAME = "Jhony";
    private static final String USER_LAST_NAME = "Fuentes";
    private static final String IDENTIFICATION_NUMBER = "3307535761202";
    private static final String EMAIL = "jhony.fuentes19@gmail.com";
    private static final String PHONE = "42882130";
    private static final String NACIONALITY = "42882130";
    private static final Long ROL_ID = 1L;
    private static final Long ORGANIZATION_ID = 1L;
    private static final String IMAGE_URL = "example.png";
    private static final String PASSWORD = "password123";

    @Mock
    private UserRepository userRepository;
    @Mock
    private RolServiceImpl rolService;
    @Mock
    private OrganizationServiceImpl organizationService;
    @Mock
    private PasswordEncoder encoder;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testUserCreate() throws NotFoundException, DuplicatedEntityException {
        // Arrange
        NewUserRequest newUserRequest = new NewUserRequest(IDENTIFICATION_NUMBER, USER_NAME, USER_LAST_NAME, EMAIL,
                PHONE, IMAGE_URL, NACIONALITY, ROL_ID, ORGANIZATION_ID, PASSWORD);
        ArgumentCaptor<UserEntity> userCaputre = ArgumentCaptor.forClass(UserEntity.class);
        UserEntity userEntity = new UserEntity();
        userEntity.setName(USER_NAME);
        userEntity.setLastName(USER_LAST_NAME);
        userEntity.setIdentification(IDENTIFICATION_NUMBER);
        userEntity.setEmail(EMAIL);
        userEntity.setPhone(PHONE);
        userEntity.setImageUrl(IMAGE_URL);
        userEntity.setNacionality(NACIONALITY);
        RolEntity rol = new RolEntity();
        rol.setId(ROL_ID);
        rol.setName("ADMIN");

        OrganizationEntity organization = new OrganizationEntity();
        organization.setId(ORGANIZATION_ID);
        organization.setName("USAC-CUNOC");

        userEntity.setRol(rol);
        userEntity.setOrganization(organization);

        when(rolService.getRolById(1L)).thenReturn(rol);
        when(organizationService.getById(1L)).thenReturn(organization);
        when(userRepository.save(userCaputre.capture())).thenReturn(userEntity);

        // Act
        UserResponse result = userService.create(newUserRequest);

        // Assert
        assertAll(
                () -> verify(userRepository).save(userCaputre.capture()),
                () -> assertEquals(USER_NAME, result.getName()),
                () -> assertEquals(USER_LAST_NAME, result.getLastName()),
                () -> assertEquals(IDENTIFICATION_NUMBER, result.getIdentification()),
                () -> assertEquals(EMAIL, result.getEmail()),
                () -> assertEquals(PHONE, result.getPhone()),
                () -> assertEquals(IMAGE_URL, result.getImageUrl()),
                () -> assertEquals(NACIONALITY, result.getNacionality()),
                () -> assertEquals(ROL_ID, result.getRolId()),
                () -> assertEquals(ORGANIZATION_ID, result.getOrganizationId()));
    }

    @Test
    void createWhenEmailDuplicated() {
        NewUserRequest request = new NewUserRequest(IDENTIFICATION_NUMBER, USER_LAST_NAME, IDENTIFICATION_NUMBER, EMAIL,
                PHONE, IMAGE_URL, NACIONALITY, ROL_ID, ORGANIZATION_ID, PASSWORD);
        when(userRepository.existsByEmail(EMAIL)).thenReturn(true);
        Assertions.assertThrows(DuplicatedEntityException.class,
                () -> userService.create(request));
    }

    @Test
    void createWhenIdDuplicated() {
        NewUserRequest request = new NewUserRequest(IDENTIFICATION_NUMBER, USER_LAST_NAME, IDENTIFICATION_NUMBER, EMAIL,
                PHONE, IMAGE_URL, NACIONALITY, ROL_ID, ORGANIZATION_ID, PASSWORD);
        when(userRepository.existsByIdentification(IDENTIFICATION_NUMBER)).thenReturn(true);
        Assertions.assertThrows(DuplicatedEntityException.class,
                () -> userService.create(request));
    }

    @Test
    void updateUserTest() throws NotFoundException, DuplicatedEntityException {
        //ARRANGE
        UserUpdate update = new UserUpdate(USER_NAME, EMAIL, USER_LAST_NAME, PHONE, IMAGE_URL);

        UserEntity entity = new UserEntity();
        entity.setId(USER_ID);
        entity.setName("OldName");
        entity.setLastName("OldLast");
        entity.setEmail("old@mail.com");
        entity.setPhone("00000000");
        entity.setImageUrl("old.png");
        RolEntity rol = new RolEntity();
        rol.setId(ORGANIZATION_ID);

        OrganizationEntity organization = new OrganizationEntity();
        organization.setId(ORGANIZATION_ID);

        entity.setRol(rol);
        entity.setOrganization(organization);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(entity));
        when(userRepository.existByEmailAndIdNot(EMAIL, USER_ID)).thenReturn(false);
        ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
        when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        // ACT
        UserResponse result = userService.update(update, USER_ID);

        // ASSERT
        verify(userRepository).save(userCaptor.capture());
        UserEntity saved = userCaptor.getValue();

        assertAll(
                () -> verify(userRepository).findById(USER_ID),

                () -> verify(userRepository).existByEmailAndIdNot(EMAIL, USER_ID),

                () -> assertEquals(USER_NAME, saved.getName()),
                () -> assertEquals(USER_LAST_NAME, saved.getLastName()),
                () -> assertEquals(EMAIL, saved.getEmail()),
                () -> assertEquals(PHONE, saved.getPhone()),
                () -> assertEquals(IMAGE_URL, saved.getImageUrl()),

                () -> assertEquals(USER_NAME, result.getName()),
                () -> assertEquals(USER_LAST_NAME, result.getLastName()),
                () -> assertEquals(EMAIL, result.getEmail()),
                () -> assertEquals(PHONE, result.getPhone()),
                () -> assertEquals(IMAGE_URL, result.getImageUrl()));

        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUserWhenEmailDuplicatedTest() {
        UserUpdate update = new UserUpdate(USER_NAME, EMAIL, USER_LAST_NAME, PHONE, IMAGE_URL);
        UserEntity entity = new UserEntity();
        entity.setId(USER_ID);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(entity));
        when(userRepository.existByEmailAndIdNot(EMAIL, USER_ID)).thenReturn(true);

        assertThrows(DuplicatedEntityException.class,
                () -> userService.update(update, USER_ID));
        verify(userRepository).findById(USER_ID);
        verify(userRepository).existByEmailAndIdNot(EMAIL, USER_ID);
        verify(userRepository, never()).save(any());
    }

}
