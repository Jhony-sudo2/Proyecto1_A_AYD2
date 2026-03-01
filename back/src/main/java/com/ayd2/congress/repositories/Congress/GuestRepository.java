package com.ayd2.congress.repositories.Congress;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayd2.congress.models.Congress.GuestEntity;

@Repository
public interface GuestRepository extends JpaRepository<GuestEntity,Long>{
    
}
