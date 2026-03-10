package com.ayd2.congress.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ayd2.congress.models.Congress.CongressEntity;

@Repository
public interface CongressRepository extends JpaRepository<CongressEntity, Long> {
    List<CongressEntity> findAllByOrganizationId(Long id);
    @Query("""
                SELECT COUNT(c) > 0 FROM CongressEntity c
                WHERE c.location.id = :locationId
                AND c.startDate < :endDate
                AND c.endDate > :startDate
            """)
    boolean isLocationOccupied(
            @Param("locationId") Long locationId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
