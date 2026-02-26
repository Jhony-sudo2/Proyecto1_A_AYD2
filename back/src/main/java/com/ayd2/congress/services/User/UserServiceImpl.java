package com.ayd2.congress.services.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ayd2.congress.dtos.User.NewUserRequest;
import com.ayd2.congress.dtos.User.UserResponse;
import com.ayd2.congress.dtos.User.UserUpdate;
import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.mappers.UserMapper;
import com.ayd2.congress.models.Organization.OrganizationEntity;
import com.ayd2.congress.models.User.RolEntity;
import com.ayd2.congress.models.User.UserEntity;
import com.ayd2.congress.repositories.UserRepository;
import com.ayd2.congress.services.Organization.OrganizationServiceImpl;
import com.ayd2.congress.services.Rol.RolServiceImpl;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final RolServiceImpl rolService;
    private final OrganizationServiceImpl organizationService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    @Autowired
    public UserServiceImpl(UserRepository userRepository, RolServiceImpl rolService, OrganizationServiceImpl organizationService,PasswordEncoder passwordEncoder,UserMapper userMapper) {
        this.repository = userRepository;
        this.rolService = rolService;
        this.organizationService = organizationService;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }
    @Override
    public UserResponse create(NewUserRequest newUserRequest) throws NotFoundException, DuplicatedEntityException {
        if(repository.existsByEmail(newUserRequest.getEmail())){
            throw new DuplicatedEntityException("Email: " + newUserRequest.getEmail() + " already exists");
        }
        if(repository.existsByIdentification(newUserRequest.getIdentification())){
            throw new DuplicatedEntityException("Identification: " + newUserRequest.getIdentification() + " already exist");
        }
        RolEntity rol = rolService.getRolById(newUserRequest.getRol());
        OrganizationEntity organizationEntity = organizationService.getById(newUserRequest.getOrganization());

        String hashPassword = passwordEncoder.encode(newUserRequest.getPassword());
        UserEntity userEntity = userMapper.toEntity(newUserRequest);

        userEntity.setPassword(hashPassword);
        userEntity.setRol(rol);
        userEntity.setOrganization(organizationEntity);

        return userMapper.toResponse(repository.save(userEntity));
    }

    @Override
    public UserEntity getById(Long id) throws NotFoundException {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public UserEntity getByEmail(String email) throws NotFoundException {
        return repository.findByEmail(email).orElseThrow(()-> new NotFoundException("User not found"));
    }

    @Override
    public UserResponse update(UserUpdate userUpdateRequest, Long id) throws NotFoundException,DuplicatedEntityException {
        UserEntity userToUpdate = getById(id);
        if (repository.existByEmailAndIdNot(userUpdateRequest.getEmail(), id)) {
            throw new DuplicatedEntityException("Email already exist");
        }
        userUpdateRequest.updateUser(userToUpdate);
        repository.save(userToUpdate);
        return userMapper.toResponse(userToUpdate);
    }
    @Override
    public UserResponse updateRol() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateRol'");
    }


}
