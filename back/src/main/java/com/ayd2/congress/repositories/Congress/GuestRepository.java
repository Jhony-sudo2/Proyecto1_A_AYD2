package com.ayd2.congress.repositories.Congress;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ayd2.congress.models.Congress.GuestEntity;

public interface GuestRepository extends JpaRepository<GuestEntity,Long>{
    
}
