package com.ayd2.congress.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

import java.io.IOException;
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
import com.ayd2.congress.dtos.User.UpdatePassword;
import com.ayd2.congress.dtos.User.UserResponse;
import com.ayd2.congress.dtos.User.UserUpdate;
import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.exceptions.NotAuthorizedException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.mappers.UserMapper;
import com.ayd2.congress.models.Organization.OrganizationEntity;
import com.ayd2.congress.models.User.RolEntity;
import com.ayd2.congress.models.User.UserEntity;
import com.ayd2.congress.repositories.UserRepository;
import com.ayd2.congress.services.Organization.OrganizationServiceImpl;
import com.ayd2.congress.services.Rol.RolServiceImpl;
import com.ayd2.congress.services.User.UserServiceImpl;
import com.ayd2.congress.services.Wallet.WalletService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
        private static final String ORGANIZATION_NAME = "USAC";
        private static final String IMAGE_URL = "example.png";
        private static final String PASSWORD = "password123";
        private static final String NEW_PASSWORD = "newPassword123";
        private static final String HASH = "HASHED";

        @Mock
        private UserRepository userRepository;
        @Mock
        private RolServiceImpl rolService;
        @Mock
        private OrganizationServiceImpl organizationService;
        @Mock
        private PasswordEncoder encoder;
        @Mock
        private UserMapper userMapper;
        @Mock
        private WalletService walletService;
        @InjectMocks
        private UserServiceImpl userService;

        @Test
        void createUserTest() throws NotFoundException, DuplicatedEntityException, IOException {
                // ARRANGE
                NewUserRequest req = new NewUserRequest(
                                IDENTIFICATION_NUMBER, USER_NAME, USER_LAST_NAME, EMAIL, PHONE, IMAGE_URL, NACIONALITY,
                                ROL_ID, ORGANIZATION_ID,
                                PASSWORD);

                when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
                when(userRepository.existsByIdentification(IDENTIFICATION_NUMBER)).thenReturn(false);

                RolEntity rol = new RolEntity();
                rol.setId(ROL_ID);
                when(rolService.getRolById(ROL_ID)).thenReturn(rol);

                OrganizationEntity org = new OrganizationEntity();
                org.setId(ORGANIZATION_ID);
                when(organizationService.getById(ORGANIZATION_ID)).thenReturn(org);

                when(encoder.encode(PASSWORD)).thenReturn(HASH);

                UserEntity mapped = new UserEntity();
                mapped.setIdentification(IDENTIFICATION_NUMBER);
                mapped.setName(USER_NAME);
                mapped.setLastName(USER_LAST_NAME);
                mapped.setEmail(EMAIL);
                mapped.setPhone(PHONE);
                mapped.setImageUrl(IMAGE_URL);
                mapped.setNacionality(NACIONALITY);
                when(userMapper.toEntity(req)).thenReturn(mapped);

                ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
                UserEntity saved = new UserEntity();
                saved.setId(USER_ID);
                saved.setIdentification(IDENTIFICATION_NUMBER);
                saved.setName(USER_NAME);
                saved.setLastName(USER_LAST_NAME);
                saved.setEmail(EMAIL);
                saved.setPhone(PHONE);
                saved.setImageUrl(IMAGE_URL);
                saved.setNacionality(NACIONALITY);
                saved.setPassword(HASH);
                saved.setRol(rol);
                saved.setOrganization(org);

                when(userRepository.save(any(UserEntity.class))).thenReturn(saved);

                UserResponse response = new UserResponse(
                                USER_ID, IDENTIFICATION_NUMBER, USER_NAME, USER_LAST_NAME, EMAIL, PHONE, IMAGE_URL,
                                true, NACIONALITY,
                                 ORGANIZATION_NAME);
                when(userMapper.toResponse(saved)).thenReturn(response);

                // ACT
                UserResponse result = userService.create(req);

                // ASSERT
                verify(userRepository).existsByEmail(EMAIL);
                verify(userRepository).existsByIdentification(IDENTIFICATION_NUMBER);
                verify(rolService).getRolById(ROL_ID);
                verify(organizationService).getById(ORGANIZATION_ID);
                verify(encoder).encode(PASSWORD);
                verify(userMapper).toEntity(req);

                verify(userRepository).save(userCaptor.capture());
                UserEntity arg = userCaptor.getValue();

                assertAll(
                                () -> assertEquals(HASH, arg.getPassword()),
                                () -> assertEquals(ROL_ID, arg.getRol().getId()),
                                () -> assertEquals(ORGANIZATION_ID, arg.getOrganization().getId()),
                                () -> assertEquals(USER_ID, result.getId()),
                                () -> assertEquals(EMAIL, result.getEmail()),
                                () -> assertEquals(ORGANIZATION_NAME, result.getOrganizationName()));
        }

        @Test
        void createWhenEmailDuplicated() {
                NewUserRequest request = new NewUserRequest(IDENTIFICATION_NUMBER, USER_LAST_NAME,
                                IDENTIFICATION_NUMBER, EMAIL,
                                PHONE, IMAGE_URL, NACIONALITY, ROL_ID, ORGANIZATION_ID, PASSWORD);
                when(userRepository.existsByEmail(EMAIL)).thenReturn(true);
                Assertions.assertThrows(DuplicatedEntityException.class,
                                () -> userService.create(request));
        }

        @Test
        void createWhenIdDuplicated() {
                NewUserRequest request = new NewUserRequest(IDENTIFICATION_NUMBER, USER_LAST_NAME,
                                IDENTIFICATION_NUMBER, EMAIL,
                                PHONE, IMAGE_URL, NACIONALITY, ROL_ID, ORGANIZATION_ID, PASSWORD);
                when(userRepository.existsByIdentification(IDENTIFICATION_NUMBER)).thenReturn(true);
                Assertions.assertThrows(DuplicatedEntityException.class,
                                () -> userService.create(request));
        }

        @Test
        void updateUserTest() throws NotFoundException, DuplicatedEntityException, IOException {
                // ARRANGE
                UserUpdate update = new UserUpdate(USER_NAME, EMAIL, USER_LAST_NAME, PHONE, IMAGE_URL);
                UserEntity entity = new UserEntity();
                entity.setId(USER_ID);
                entity.setName("OldName");
                entity.setLastName("OldLast");
                entity.setEmail("old@mail.com");
                entity.setPhone("00000000");
                entity.setImageUrl("old.png");

                RolEntity rol = new RolEntity();
                rol.setId(ROL_ID);

                OrganizationEntity organization = new OrganizationEntity();
                organization.setId(ORGANIZATION_ID);
                organization.setName(ORGANIZATION_NAME);
                entity.setRol(rol);
                entity.setOrganization(organization);

                when(userRepository.findById(USER_ID)).thenReturn(Optional.of(entity));
                when(userRepository.existsByEmailAndIdNot(EMAIL, USER_ID)).thenReturn(false);

                ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
                when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> inv.getArgument(0));

                UserResponse mappedResponse = new UserResponse(
                                USER_ID,
                                entity.getIdentification(),
                                USER_NAME,
                                USER_LAST_NAME,
                                EMAIL,
                                PHONE,
                                IMAGE_URL,
                                entity.isActive(),
                                entity.getNacionality(),
                                organization.getName());
                when(userMapper.toResponse(any(UserEntity.class))).thenReturn(mappedResponse);

                // ACT
                UserResponse result = userService.update(update, USER_ID);

                // ASSERT
                verify(userRepository).save(userCaptor.capture());
                UserEntity saved = userCaptor.getValue();

                assertAll(
                                () -> verify(userRepository).findById(USER_ID),
                                () -> verify(userRepository).existsByEmailAndIdNot(EMAIL, USER_ID),
                                () -> verify(userMapper).toResponse(saved),

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

                verifyNoMoreInteractions(userRepository, userMapper);
        }

        @Test
        void updateUserWhenEmailDuplicatedTest() {
                UserUpdate update = new UserUpdate(USER_NAME, EMAIL, USER_LAST_NAME, PHONE, IMAGE_URL);
                UserEntity entity = new UserEntity();
                entity.setId(USER_ID);

                when(userRepository.findById(USER_ID)).thenReturn(Optional.of(entity));
                when(userRepository.existsByEmailAndIdNot(EMAIL, USER_ID)).thenReturn(true);

                assertThrows(DuplicatedEntityException.class,
                                () -> userService.update(update, USER_ID));
                verify(userRepository).findById(USER_ID);
                verify(userRepository).existsByEmailAndIdNot(EMAIL, USER_ID);
                verify(userRepository, never()).save(any());
        }

        @Test
        void updatePasswordTest() throws NotFoundException, NotAuthorizedException {
                UserEntity user = new UserEntity();
                user.setId(USER_ID);
                user.setPassword(HASH); 
                UpdatePassword request = new UpdatePassword(PASSWORD, NEW_PASSWORD);

                when(userRepository.findById(USER_ID)).thenReturn(java.util.Optional.of(user));
                when(encoder.matches(PASSWORD, HASH)).thenReturn(true); 
                when(encoder.encode(NEW_PASSWORD)).thenReturn("NEW_HASH");

                // Act
                userService.updatePassword(request, USER_ID);

                // Assert
                ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
                verify(userRepository).save(captor.capture());

                UserEntity saved = captor.getValue();
                assertEquals(USER_ID, saved.getId());
                assertEquals("NEW_HASH", saved.getPassword());

                verify(encoder).matches(PASSWORD, HASH);
                verify(encoder).encode(NEW_PASSWORD);
                verifyNoMoreInteractions(userRepository, encoder);
        }

        @Test
        void updatePassword_when_IncorrectCredentialsTest() {
                // Arrange
                UserEntity user = new UserEntity();
                user.setId(USER_ID);
                user.setPassword(HASH);

                UpdatePassword request = new UpdatePassword("bad-password", NEW_PASSWORD);

                when(userRepository.findById(USER_ID)).thenReturn(java.util.Optional.of(user));
                when(encoder.matches("bad-password", HASH)).thenReturn(false);

                // Act + Assert
                assertThrows(NotAuthorizedException.class, () -> userService.updatePassword(request, USER_ID));

                verify(userRepository, never()).save(any());
                verify(encoder, never()).encode(anyString());
        }

}
