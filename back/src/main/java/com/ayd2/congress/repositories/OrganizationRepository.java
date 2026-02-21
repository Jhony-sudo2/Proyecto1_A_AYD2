package com.ayd2.congress.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayd2.congress.models.Organization.OrganizationEntity;

@Repository
public interface OrganizationRepository extends JpaRepository<OrganizationEntity,Long>{
    boolean existByName(String name);
    //Check if the name exists but ignore the organization with the given id
    boolean existByNameAndIdNot(String name,Long id);
}
