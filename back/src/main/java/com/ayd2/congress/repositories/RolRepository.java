package com.ayd2.congress.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ayd2.congress.models.User.RolEntity;

public interface RolRepository extends JpaRepository<RolEntity, Long> {
    boolean existByName(String name);
    boolean existById(Long id);
}
