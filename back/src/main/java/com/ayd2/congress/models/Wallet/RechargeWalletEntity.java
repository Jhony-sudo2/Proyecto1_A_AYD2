package com.ayd2.congress.models.Wallet;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rechargeWallet")
@Data
@NoArgsConstructor
public class RechargeWalletEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private WalletEntity wallet;
    @Column(nullable = false)
    private LocalDateTime date;
    @Column
    private Double amount;
}
