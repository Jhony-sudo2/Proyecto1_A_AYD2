package com.ayd2.congress.repositories.Wallet;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ayd2.congress.models.Wallet.RechargeWalletEntity;

public interface RechargeWalletRepository extends JpaRepository<RechargeWalletEntity,Long>{
    List<RechargeWalletEntity> findAllByWalletId(Long id);
}
