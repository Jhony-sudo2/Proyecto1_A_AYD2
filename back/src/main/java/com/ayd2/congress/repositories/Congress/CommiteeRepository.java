package com.ayd2.congress.repositories.Congress;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayd2.congress.compositePrimaryKeys.CommiteeId;
import com.ayd2.congress.models.Congress.ScientificCommitteeEntity;

@Repository
public interface CommiteeRepository extends JpaRepository<ScientificCommitteeEntity, CommiteeId> {
    boolean existsByUserIdAndCongressId(Long userId,Long congressId);
    List<ScientificCommitteeEntity> findAllByCongressId(Long congressId);
    Optional<ScientificCommitteeEntity> findByUserIdAndCongressId(Long userId,Long congressId);
}
