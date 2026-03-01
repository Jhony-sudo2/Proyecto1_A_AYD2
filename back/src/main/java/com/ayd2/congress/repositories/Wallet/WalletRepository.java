package com.ayd2.congress.repositories.Wallet;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayd2.congress.models.Wallet.WalletEntity;
@Repository
public interface WalletRepository extends JpaRepository<WalletEntity,Long>{
    boolean existsByUserId(Long userId);
    Optional<WalletEntity> findByUserId(Long userId);
}
