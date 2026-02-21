package com.ayd2.congress.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayd2.congress.models.User.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long>{
    boolean existsByEmail(String email);
    boolean existsByIdentification(String identification);
    Optional<UserEntity> findByEmail(String email);
    boolean existByEmailAndIdNot(String email,Long id);
}
