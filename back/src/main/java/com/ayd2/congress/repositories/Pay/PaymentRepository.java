package com.ayd2.congress.repositories.Pay;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayd2.congress.models.Pay.PaymentEntity;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity,Long> {
    boolean existsByUserIdAndCongressId(Long userId,Long congressId);
    List<PaymentEntity> findAllByUserId(Long userId);
    List<PaymentEntity> findAllByCongressId(Long congressId);
}
