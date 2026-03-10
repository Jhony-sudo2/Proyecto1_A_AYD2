package com.ayd2.congress.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayd2.congress.models.SystemConfigEntity;

@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfigEntity,Long>{
    
}
