package com.ayd2.congress.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayd2.congress.models.User.RolEntity;

@Repository
public interface RolRepository extends JpaRepository<RolEntity, Long> {
    boolean existByName(String name);
}
