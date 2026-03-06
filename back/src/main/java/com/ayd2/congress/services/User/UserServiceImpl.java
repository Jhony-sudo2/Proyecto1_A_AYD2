package com.ayd2.congress.services.User;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
import com.ayd2.congress.services.Wallet.WalletService;
import com.ayd2.congress.services.aws.S3Service;

import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final RolServiceImpl rolService;
    private final OrganizationServiceImpl organizationService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final WalletService walletService;
    private final S3Service s3Service;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RolServiceImpl rolService,
            OrganizationServiceImpl organizationService, PasswordEncoder passwordEncoder, UserMapper userMapper,
            WalletService walletService, S3Service s3Service) {
        this.repository = userRepository;
        this.rolService = rolService;
        this.organizationService = organizationService;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.walletService = walletService;
        this.s3Service = s3Service;
    }

    @Transactional
    @Override
    public UserResponse create(NewUserRequest newUserRequest)
            throws NotFoundException, DuplicatedEntityException, IOException {
        if (repository.existsByEmail(newUserRequest.getEmail())) {
            throw new DuplicatedEntityException("Email: " + newUserRequest.getEmail() + " already exists");
        }
        if (repository.existsByIdentification(newUserRequest.getIdentification())) {
            throw new DuplicatedEntityException(
                    "Identification: " + newUserRequest.getIdentification() + " already exist");
        }
        RolEntity rol = rolService.getRolById(newUserRequest.getRol());
        OrganizationEntity organizationEntity = organizationService.getById(newUserRequest.getOrganization());

        String hashPassword = passwordEncoder.encode(newUserRequest.getPassword());
        UserEntity userEntity = userMapper.toEntity(newUserRequest);
        String imageUrl = s3Service.uploadBase64(newUserRequest.getImageUrl(),
                "user" + newUserRequest.getIdentification());
        userEntity.setPassword(hashPassword);
        userEntity.setRol(rol);
        userEntity.setOrganization(organizationEntity);
        userEntity.setImageUrl(imageUrl);
        userEntity = repository.save(userEntity);
        walletService.create(userEntity);
        return userMapper.toResponse(userEntity);
    }

    @Override
    public UserEntity getById(Long id) throws NotFoundException {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public UserEntity getByEmail(String email) throws NotFoundException {
        return repository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public UserResponse update(UserUpdate userUpdateRequest, Long id)
            throws NotFoundException, DuplicatedEntityException, IOException {
        UserEntity userToUpdate = getById(id);
        if (repository.existsByEmailAndIdNot(userUpdateRequest.getEmail(), id)) {
            throw new DuplicatedEntityException("Email already exist");
        }
        userUpdateRequest.updateUser(userToUpdate);
        if (userUpdateRequest.getImage() == "") {
            String image = s3Service.uploadBase64(userUpdateRequest.getImage(), "USER_" + userToUpdate.getId());
            userToUpdate.setImageUrl(image);
        }
        repository.save(userToUpdate);
        return userMapper.toResponse(userToUpdate);
    }

    @Override
    public UserResponse updateRol() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateRol'");
    }

    @Override
    public UserResponse getByIdResponse(Long id) throws NotFoundException {
        return userMapper.toResponse(getById(id));
    }

    @Override
    public void updatePassword(UpdatePassword request, Long id) throws NotFoundException, NotAuthorizedException {
        UserEntity userToUpdate = getById(id);

        if (!passwordEncoder.matches(request.getCurrentPassword(), userToUpdate.getPassword())) {
            throw new NotAuthorizedException("Credentials errors");
        }

        String newPassword = passwordEncoder.encode(request.getNewPassword());
        userToUpdate.setPassword(newPassword);
        repository.save(userToUpdate);
    }

    @Override
    public UserResponse changeState(Long id) throws NotFoundException {
        UserEntity userToUpdate = getById(id);
        boolean currentState = userToUpdate.isActive();
        userToUpdate.setActive(!currentState);
        repository.save(userToUpdate);
        return userMapper.toResponse(userToUpdate);
    }

    @Override
    public UserEntity getByIdentification(String identification) throws NotFoundException {
        return repository.findByIdentification(identification)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<UserEntity> entities = repository.findAll();
        return userMapper.toResponseList(entities);
    }

}
