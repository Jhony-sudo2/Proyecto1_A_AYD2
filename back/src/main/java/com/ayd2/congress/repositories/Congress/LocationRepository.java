package com.ayd2.congress.repositories.Congress;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ayd2.congress.models.Congress.LocationEntity;

public interface LocationRepository extends JpaRepository<LocationEntity,Long>{
    
}
