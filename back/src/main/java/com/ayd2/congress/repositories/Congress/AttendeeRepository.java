package com.ayd2.congress.repositories.Congress;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ayd2.congress.models.Congress.AttendeeEntity;

public interface AttendeeRepository extends JpaRepository<AttendeeEntity,Long>{
    
}
