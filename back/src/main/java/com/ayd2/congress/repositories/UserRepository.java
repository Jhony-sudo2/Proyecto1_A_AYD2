package com.ayd2.congress.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ayd2.congress.models.User.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity,Long>{
    
}
