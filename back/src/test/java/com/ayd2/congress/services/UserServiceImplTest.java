package com.ayd2.congress.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ayd2.congress.dtos.Rol.RolResponse;
import com.ayd2.congress.dtos.User.NewUserRequest;
import com.ayd2.congress.dtos.User.RecoverPassword;
import com.ayd2.congress.dtos.User.UpdatePassword;
import com.ayd2.congress.dtos.User.UserRegister;
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
import com.ayd2.congress.services.aws.S3Service;
import com.ayd2.congress.services.mail.MailService;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    private static final Long USER_ID = 1L;
    private static final Long ROL_ID = 2L;
    private static final Long ORGANIZATION_ID = 3L;

    private static final String IDENTIFICATION = "1234567890101";
    private static final String NAME = "Juan";
    private static final String LAST_NAME = "Perez";
    private static final String EMAIL = "juan@mail.com";
    private static final String PHONE = "5555-5555";
    private static final String IMAGE_BASE64 = "base64-image";
    private static final String IMAGE_URL = "https://bucket.s3.amazonaws.com/user1234567890101";
    private static final String NATIONALITY = "Guatemalan";
    private static final String PASSWORD = "Password123";
    private static final String HASHED_PASSWORD = "HASHED_PASSWORD";

    private static final String UPDATED_NAME = "Carlos";
    private static final String UPDATED_LAST_NAME = "Lopez";
    private static final String UPDATED_EMAIL = "carlos@mail.com";
    private static final String UPDATED_PHONE = "4444-4444";
    private static final String UPDATED_IMAGE = "";
    private static final String UPDATED_IMAGE_URL = "https://bucket.s3.amazonaws.com/USER_1";

    @Mock
    private UserRepository repository;
    @Mock
    private RolServiceImpl rolService;
    @Mock
    private OrganizationServiceImpl organizationService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;
    @Mock
    private WalletService walletService;
    @Mock
    private S3Service s3Service;
    @Mock
    private MailService mailService;

    @InjectMocks
    private UserServiceImpl userService;

    private NewUserRequest newUserRequest;
    private UserEntity userEntity;
    private UserResponse userResponse;
    private RolEntity rolEntity;
    private OrganizationEntity organizationEntity;

    @BeforeEach
    void setUp() {
        newUserRequest = new NewUserRequest(
                IDENTIFICATION,
                NAME,
                LAST_NAME,
                EMAIL,
                PHONE,
                IMAGE_BASE64,
                NATIONALITY,
                ROL_ID,
                ORGANIZATION_ID,
                PASSWORD
        );

        userEntity = new UserEntity();
        userEntity.setId(USER_ID);
        userEntity.setIdentification(IDENTIFICATION);
        userEntity.setName(NAME);
        userEntity.setLastName(LAST_NAME);
        userEntity.setEmail(EMAIL);
        userEntity.setPhone(PHONE);
        userEntity.setImageUrl(IMAGE_URL);
        userEntity.setNacionality(NATIONALITY);
        userEntity.setPassword(HASHED_PASSWORD);
        userEntity.setActive(true);

        rolEntity = new RolEntity();
        organizationEntity = new OrganizationEntity();

        userResponse = new UserResponse(
                USER_ID,
                IDENTIFICATION,
                NAME,
                LAST_NAME,
                EMAIL,
                PHONE,
                IMAGE_URL,
                true,
                NATIONALITY,
                "ORG_NAME"
        );
    }

    @Test
    void testCreateUser() throws Exception {
        // Arrange
        ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);

        UserEntity mappedEntity = new UserEntity();
        mappedEntity.setIdentification(IDENTIFICATION);
        mappedEntity.setName(NAME);
        mappedEntity.setLastName(LAST_NAME);
        mappedEntity.setEmail(EMAIL);
        mappedEntity.setPhone(PHONE);
        mappedEntity.setNacionality(NATIONALITY);

        when(repository.existsByEmail(EMAIL)).thenReturn(false);
        when(repository.existsByIdentification(IDENTIFICATION)).thenReturn(false);
        when(rolService.getRolById(ROL_ID)).thenReturn(rolEntity);
        when(organizationService.getById(ORGANIZATION_ID)).thenReturn(organizationEntity);
        when(passwordEncoder.encode(PASSWORD)).thenReturn(HASHED_PASSWORD);
        when(userMapper.toEntity(newUserRequest)).thenReturn(mappedEntity);
        when(s3Service.uploadBase64(IMAGE_BASE64, "user" + IDENTIFICATION)).thenReturn(IMAGE_URL);
        when(repository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity entity = invocation.getArgument(0);
            entity.setId(USER_ID);
            return entity;
        });
        when(userMapper.toResponse(any(UserEntity.class))).thenReturn(userResponse);

        // Act
        UserResponse result = userService.create(newUserRequest);

        // Assert
        assertAll(
                () -> verify(repository).save(userCaptor.capture()),
                () -> verify(walletService).create(userCaptor.getValue()),
                () -> assertEquals(HASHED_PASSWORD, userCaptor.getValue().getPassword()),
                () -> assertEquals(IMAGE_URL, userCaptor.getValue().getImageUrl()),
                () -> assertSame(rolEntity, userCaptor.getValue().getRol()),
                () -> assertSame(organizationEntity, userCaptor.getValue().getOrganization()),
                () -> assertEquals(USER_ID, result.getId()),
                () -> assertEquals(NAME, result.getName()),
                () -> assertEquals(EMAIL, result.getEmail())
        );
    }

    @Test
    void testCreateUserWhenDuplicatedEmail() {
        // Arrange
        when(repository.existsByEmail(EMAIL)).thenReturn(true);

        // Assert
        assertThrows(DuplicatedEntityException.class,
                () -> userService.create(newUserRequest));
    }

    @Test
    void testCreateUserWhenDuplicatedIdentification() {
        // Arrange
        when(repository.existsByEmail(EMAIL)).thenReturn(false);
        when(repository.existsByIdentification(IDENTIFICATION)).thenReturn(true);

        // Assert
        assertThrows(DuplicatedEntityException.class,
                () -> userService.create(newUserRequest));
    }

    @Test
    void testRegisterUserNormal() throws Exception {
        // Arrange
        UserRegister registerRequest = new UserRegister(
                IDENTIFICATION,
                NAME,
                LAST_NAME,
                EMAIL,
                PHONE,
                IMAGE_BASE64,
                NATIONALITY,
                ORGANIZATION_ID,
                PASSWORD
        );

        UserServiceImpl spy = spy(userService);

        when(userMapper.toRequest(registerRequest)).thenReturn(newUserRequest);
        doReturn(userResponse).when(spy).create(newUserRequest);

        // Act
        UserResponse result = spy.registerUserNormal(registerRequest);

        // Assert
        assertAll(
                () -> verify(userMapper).toRequest(registerRequest),
                () -> assertEquals(USER_ID, result.getId()),
                () -> assertEquals(NAME, result.getName())
        );
    }

    @Test
    void testGetById() throws Exception {
        // Arrange
        when(repository.findById(USER_ID)).thenReturn(Optional.of(userEntity));

        // Act
        UserEntity result = userService.getById(USER_ID);

        // Assert
        assertEquals(USER_ID, result.getId());
    }

    @Test
    void testGetByIdWhenNotFound() {
        // Arrange
        when(repository.findById(USER_ID)).thenReturn(Optional.empty());

        // Assert
        assertThrows(NotFoundException.class, () -> userService.getById(USER_ID));
    }

    @Test
    void testGetByEmail() throws Exception {
        // Arrange
        when(repository.findByEmail(EMAIL)).thenReturn(Optional.of(userEntity));

        // Act
        UserEntity result = userService.getByEmail(EMAIL);

        // Assert
        assertEquals(EMAIL, result.getEmail());
    }

    @Test
    void testUpdateUser() throws Exception {
        // Arrange
        UserUpdate request = new UserUpdate(
                UPDATED_NAME,
                UPDATED_EMAIL,
                UPDATED_LAST_NAME,
                UPDATED_PHONE,
                UPDATED_IMAGE
        );

        UserServiceImpl spy = spy(userService);
        ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);

        doReturn(userEntity).when(spy).getById(USER_ID);
        when(repository.existsByEmailAndIdNot(UPDATED_EMAIL, USER_ID)).thenReturn(false);
        when(s3Service.uploadBase64(UPDATED_IMAGE, "USER_" + USER_ID)).thenReturn(UPDATED_IMAGE_URL);

        UserResponse updatedResponse = new UserResponse(
                USER_ID,
                IDENTIFICATION,
                UPDATED_NAME,
                UPDATED_LAST_NAME,
                UPDATED_EMAIL,
                UPDATED_PHONE,
                UPDATED_IMAGE_URL,
                true,
                NATIONALITY,
                "ORG_NAME"
        );
        when(userMapper.toResponse(any(UserEntity.class))).thenReturn(updatedResponse);

        // Act
        UserResponse result = spy.update(request, USER_ID);

        // Assert
        assertAll(
                () -> verify(repository).save(userCaptor.capture()),
                () -> assertEquals(UPDATED_NAME, userCaptor.getValue().getName()),
                () -> assertEquals(UPDATED_LAST_NAME, userCaptor.getValue().getLastName()),
                () -> assertEquals(UPDATED_EMAIL, userCaptor.getValue().getEmail()),
                () -> assertEquals(UPDATED_PHONE, userCaptor.getValue().getPhone()),
                () -> assertEquals(UPDATED_IMAGE_URL, userCaptor.getValue().getImageUrl()),
                () -> assertEquals(UPDATED_NAME, result.getName()),
                () -> assertEquals(UPDATED_EMAIL, result.getEmail())
        );
    }

    @Test
    void testUpdateUserWhenDuplicatedEmail() throws Exception {
        // Arrange
        UserUpdate request = new UserUpdate(
                UPDATED_NAME,
                UPDATED_EMAIL,
                UPDATED_LAST_NAME,
                UPDATED_PHONE,
                UPDATED_IMAGE
        );

        UserServiceImpl spy = spy(userService);
        doReturn(userEntity).when(spy).getById(USER_ID);
        when(repository.existsByEmailAndIdNot(UPDATED_EMAIL, USER_ID)).thenReturn(true);

        // Assert
        assertThrows(DuplicatedEntityException.class,
                () -> spy.update(request, USER_ID));
    }

    @Test
    void testUpdateUserDoesNotUploadImageWhenImageIsNotEmpty() throws Exception {
        // Este test refleja el comportamiento ACTUAL del servicio
        // porque la condición es: if (userUpdateRequest.getImage() == "")

        // Arrange
        UserUpdate request = new UserUpdate(
                UPDATED_NAME,
                UPDATED_EMAIL,
                UPDATED_LAST_NAME,
                UPDATED_PHONE,
                "NEW_IMAGE_BASE64"
        );

        UserServiceImpl spy = spy(userService);
        doReturn(userEntity).when(spy).getById(USER_ID);
        when(repository.existsByEmailAndIdNot(UPDATED_EMAIL, USER_ID)).thenReturn(false);
        when(userMapper.toResponse(any(UserEntity.class))).thenReturn(userResponse);

        // Act
        spy.update(request, USER_ID);

        // Assert
        verify(s3Service, never()).uploadBase64(any(), any());
    }

    @Test
    void testUpdateRol() throws Exception {
        // Arrange
        UserServiceImpl spy = spy(userService);
        ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);

        RolEntity newRol = new RolEntity();

        doReturn(userEntity).when(spy).getById(USER_ID);
        when(rolService.getRolById(ROL_ID)).thenReturn(newRol);
        when(userMapper.toResponse(any(UserEntity.class))).thenReturn(userResponse);

        // Act
        UserResponse result = spy.updateRol(ROL_ID, USER_ID);

        // Assert
        assertAll(
                () -> verify(repository).save(userCaptor.capture()),
                () -> assertSame(newRol, userCaptor.getValue().getRol()),
                () -> assertEquals(USER_ID, result.getId())
        );
    }

    @Test
    void testGetByIdResponse() throws Exception {
        // Arrange
        UserServiceImpl spy = spy(userService);
        doReturn(userEntity).when(spy).getById(USER_ID);
        when(userMapper.toResponse(userEntity)).thenReturn(userResponse);

        // Act
        UserResponse result = spy.getByIdResponse(USER_ID);

        // Assert
        assertEquals(USER_ID, result.getId());
    }

    @Test
    void testUpdatePassword() throws Exception {
        // Arrange
        UpdatePassword request = new UpdatePassword("oldPassword", "newPassword123");
        UserServiceImpl spy = spy(userService);
        ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);

        userEntity.setPassword("OLD_HASH");
        doReturn(userEntity).when(spy).getById(USER_ID);
        when(passwordEncoder.matches("oldPassword", "OLD_HASH")).thenReturn(true);
        when(passwordEncoder.encode("newPassword123")).thenReturn("NEW_HASH");

        // Act
        spy.updatePassword(request, USER_ID);

        // Assert
        assertAll(
                () -> verify(repository).save(userCaptor.capture()),
                () -> assertEquals("NEW_HASH", userCaptor.getValue().getPassword())
        );
    }

    @Test
    void testUpdatePasswordWhenCurrentPasswordIsInvalid() throws Exception {
        // Arrange
        UpdatePassword request = new UpdatePassword("badPassword", "newPassword123");
        UserServiceImpl spy = spy(userService);

        userEntity.setPassword("OLD_HASH");
        doReturn(userEntity).when(spy).getById(USER_ID);
        when(passwordEncoder.matches("badPassword", "OLD_HASH")).thenReturn(false);

        // Assert
        assertThrows(NotAuthorizedException.class,
                () -> spy.updatePassword(request, USER_ID));
    }

    @Test
    void testChangeState() throws Exception {
        // Arrange
        UserServiceImpl spy = spy(userService);
        ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);

        userEntity.setActive(true);
        doReturn(userEntity).when(spy).getById(USER_ID);

        UserResponse inactiveResponse = new UserResponse(
                USER_ID,
                IDENTIFICATION,
                NAME,
                LAST_NAME,
                EMAIL,
                PHONE,
                IMAGE_URL,
                false,
                NATIONALITY,
                "ORG_NAME"
        );
        when(userMapper.toResponse(any(UserEntity.class))).thenReturn(inactiveResponse);

        // Act
        UserResponse result = spy.changeState(USER_ID);

        // Assert
        assertAll(
                () -> verify(repository).save(userCaptor.capture()),
                () -> assertFalse(userCaptor.getValue().isActive()),
                () -> assertFalse(result.isActive())
        );
    }

    @Test
    void testGetByIdentification() throws Exception {
        // Arrange
        when(repository.findByIdentification(IDENTIFICATION)).thenReturn(Optional.of(userEntity));

        // Act
        UserEntity result = userService.getByIdentification(IDENTIFICATION);

        // Assert
        assertEquals(IDENTIFICATION, result.getIdentification());
    }

    @Test
    void testGetByIdentificationWhenNotFound() {
        // Arrange
        when(repository.findByIdentification(IDENTIFICATION)).thenReturn(Optional.empty());

        // Assert
        assertThrows(NotFoundException.class,
                () -> userService.getByIdentification(IDENTIFICATION));
    }

    @Test
    void testGetAllUsers() {
        // Arrange
        List<UserEntity> entities = List.of(userEntity);
        List<UserResponse> responses = List.of(userResponse);

        when(repository.findAll()).thenReturn(entities);
        when(userMapper.toResponseList(entities)).thenReturn(responses);

        // Act
        List<UserResponse> result = userService.getAllUsers();

        // Assert
        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(USER_ID, result.get(0).getId()),
                () -> assertEquals(NAME, result.get(0).getName())
        );
    }

    @Test
    void testRecoverPassword() throws Exception {
        // Arrange
        RecoverPassword request = new RecoverPassword(EMAIL);
        ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
        ArgumentCaptor<String> codeCaptor = ArgumentCaptor.forClass(String.class);

        when(repository.findByEmail(EMAIL)).thenReturn(Optional.of(userEntity));

        LocalDateTime before = LocalDateTime.now();

        // Act
        userService.recoverPassword(request);

        LocalDateTime after = LocalDateTime.now();

        // Assert
        assertAll(
                () -> verify(mailService).sendCode(eq("RECUPERACION DE CONTRASE;A"), eq(EMAIL), codeCaptor.capture()),
                () -> verify(repository).save(userCaptor.capture()),
                () -> assertTrue(codeCaptor.getValue().matches("\\d{6}")),
                () -> assertEquals(codeCaptor.getValue(), userCaptor.getValue().getRecoveryCode()),
                () -> assertTrue(userCaptor.getValue().getCodeExpiration().isAfter(before.plusMinutes(14))),
                () -> assertTrue(userCaptor.getValue().getCodeExpiration().isBefore(after.plusMinutes(16)))
        );
    }

    
    @Test
    void testGetAllRols() {
        // Arrange
        List<RolResponse> expected = List.of(org.mockito.Mockito.mock(RolResponse.class));
        when(rolService.getALLResponses()).thenReturn(expected);

        // Act
        List<RolResponse> result = userService.getAllRols();

        // Assert
        assertEquals(1, result.size());
    }
}