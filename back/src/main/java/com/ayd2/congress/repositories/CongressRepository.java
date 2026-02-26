package com.ayd2.congress.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ayd2.congress.models.Congress.CongressEntity;

public interface CongressRepository extends JpaRepository<CongressEntity,Long>{
    
}
