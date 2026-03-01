package com.ayd2.congress.repositories.Pay;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayd2.congress.models.Pay.PaymentEntity;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity,Long> {
    
}
