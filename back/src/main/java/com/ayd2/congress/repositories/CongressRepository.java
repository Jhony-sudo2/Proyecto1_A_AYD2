package com.ayd2.congress.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayd2.congress.models.Congress.CongressEntity;
@Repository
public interface CongressRepository extends JpaRepository<CongressEntity,Long>{
    List<CongressEntity> findAllByOrganizationId(Long id);
}
