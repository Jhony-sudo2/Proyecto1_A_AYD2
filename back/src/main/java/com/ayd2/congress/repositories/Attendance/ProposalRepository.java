package com.ayd2.congress.repositories.Attendance;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayd2.congress.models.Activities.ProposalEntity;
import com.ayd2.congress.models.Enums.ProposalState;

@Repository
public interface ProposalRepository extends JpaRepository<ProposalEntity,Long>{
    boolean existsByUserIdAndCongressIdAndState(Long userId, Long congressId, ProposalState state);
    List<ProposalEntity> findByCongressId(Long congressId);
    List<ProposalEntity> findByUserId(Long userId);
}
