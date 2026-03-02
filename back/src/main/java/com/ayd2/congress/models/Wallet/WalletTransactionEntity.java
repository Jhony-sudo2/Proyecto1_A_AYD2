package com.ayd2.congress.models.Wallet;

import java.time.LocalDate;

import com.ayd2.congress.models.Enums.TransactionType;
import com.ayd2.congress.models.Pay.PaymentEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "wallet_transactions")
@Data
@NoArgsConstructor
public class WalletTransactionEntity {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "payment_id")
    private PaymentEntity payment;
    @Column(nullable = false)
    private Double amount;
    @Column(nullable = false)
    private LocalDate date;
    @Column(nullable = false)
    private TransactionType transactionType;
}
