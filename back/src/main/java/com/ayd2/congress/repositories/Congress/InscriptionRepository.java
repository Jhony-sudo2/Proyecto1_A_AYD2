package com.ayd2.congress.repositories.Congress;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.ayd2.congress.dtos.reports.InscriptionReport;
import com.ayd2.congress.models.Congress.InscriptionEntity;

@Repository
public interface InscriptionRepository extends JpaRepository<InscriptionEntity, Long> {
    boolean existsByUserIdAndCongressIdAndAttendeeRolId(Long userId, Long congressId, Long attendeeRolId);

    boolean existsByUserIdAndCongressId(Long userId, Long congressId);

    List<InscriptionEntity> findAllByUserId(Long userId);

    List<InscriptionEntity> findAllByCongressId(Long congressId);

    @Query("""
                SELECT new com.ayd2.congress.dtos.reports.InscriptionReport(
                    u.identification,
                    CONCAT(u.name, ' ', u.lastName),
                    u.organization.name,
                    u.email,
                    u.phone,
                    ar.name
                )
                FROM InscriptionEntity i
                JOIN i.user u
                JOIN i.attendeeRol ar
                WHERE i.id.congressId = :congressId
                AND (:attendeeRolId IS NULL OR ar.id = :attendeeRolId)
                ORDER BY u.lastName ASC
            """)
    List<InscriptionReport> getInscriptionReport(
            @Param("congressId") Long congressId,
            @Param("attendeeRolId") Long attendeeRolId);
}
