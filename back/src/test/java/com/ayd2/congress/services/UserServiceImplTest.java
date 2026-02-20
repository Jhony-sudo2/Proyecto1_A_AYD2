package com.ayd2.congress.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ayd2.congress.dtos.User.NewUserRequest;
import com.ayd2.congress.dtos.User.UserResponse;
import com.ayd2.congress.models.Organization.OrganizationEntity;
import com.ayd2.congress.models.User.RolEntity;
import com.ayd2.congress.models.User.UserEntity;
import com.ayd2.congress.repositories.UserRepository;
import com.ayd2.congress.services.User.UserServiceImpl;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
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
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testUserCreate(){
        //Arrange
        NewUserRequest newUserRequest =  new NewUserRequest(IDENTIFICATION_NUMBER, USER_NAME, USER_LAST_NAME, EMAIL,PHONE, IMAGE_URL, NACIONALITY, ROL_ID, ORGANIZATION_ID,PASSWORD);
        ArgumentCaptor<UserEntity> userCaputre = ArgumentCaptor.forClass(UserEntity.class);
        UserEntity userEntity = new UserEntity();
        userEntity.setName(USER_NAME);
        userEntity.setLastName(USER_LAST_NAME);
        userEntity.setIdentification(IDENTIFICATION_NUMBER);
        userEntity.setEmail(EMAIL);
        userEntity.setPhone(PHONE);
        userEntity.setImageUrl(IMAGE_URL);
        userEntity.setNacionality(NACIONALITY);
        userEntity.setRol(new RolEntity());
        userEntity.setOrganization(new OrganizationEntity());

        when(userRepository.save(userCaputre.capture())).thenReturn(userEntity);

        //Act
        UserResponse userResponse = userService.create(newUserRequest);

        //Assert
        assertAll(
            () -> verify(userRepository).save(userCaputre.capture()),
            () -> assertEquals(USER_NAME, userResponse.getName()),
            () -> assertEquals(USER_LAST_NAME, userResponse.getLastName()),
            () -> assertEquals(IDENTIFICATION_NUMBER, userResponse.getIdentification()),
            () -> assertEquals(EMAIL, userResponse.getEmail()),
            () -> assertEquals(PHONE, userResponse.getPhone()),
            () -> assertEquals(IMAGE_URL, userResponse.getImageUrl()),
            () -> assertEquals(NACIONALITY, userResponse.getNacionality()),
            () -> assertEquals(ROL_ID, userResponse.getRolId()),
            () -> assertEquals(ORGANIZATION_ID, userResponse.getOrganizationId())
        );
    }
}
