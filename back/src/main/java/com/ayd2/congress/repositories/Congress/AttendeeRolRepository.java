package com.ayd2.congress.repositories.Congress;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayd2.congress.models.Congress.AttendeeRol;

@Repository
public interface AttendeeRolRepository extends JpaRepository<AttendeeRol,Long>{
    
}
