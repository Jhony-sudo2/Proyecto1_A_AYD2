package com.ayd2.congress.repositories.Congress;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayd2.congress.models.Congress.InscriptionEntity;

@Repository
public interface InscriptionRepository extends JpaRepository<InscriptionEntity,Long>{
    boolean existsByUserIdAndCongressIdAndAttendeeRolId(Long userId,Long congressId,Long attendeeRolId);
    List<InscriptionEntity> findAllByUserId(Long userId);
    List<InscriptionEntity> findAllByCongressId(Long congressId);
}
