package com.ayd2.congress.repositories.Congress;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayd2.congress.compositePrimaryKeys.CommiteeId;
import com.ayd2.congress.models.Congress.ScientificCommitteeEntity;

@Repository
public interface CommiteeRepository extends JpaRepository<ScientificCommitteeEntity, CommiteeId> {
    
}
