package com.ayd2.congress.repositories.Congress;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ayd2.congress.models.Congress.CertificateEntity;

public interface CertificateRepository extends JpaRepository<CertificateEntity,Long> {
    boolean existsByUserIdAndCongressIdAndRolId(Long userId, Long congressId, Long rolId);
    Optional<CertificateEntity> findByUserIdAndCongressIdAndRolId(Long userId,Long congressId,Long rolId);
}
