package com.ayd2.congress.repositories.Pay;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ayd2.congress.models.Pay.PaymentEntity;

public interface PaymentRepository extends JpaRepository<PaymentEntity,Long> {
    
}
