package com.ayd2.congress.repositories.Wallet;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ayd2.congress.dtos.reports.EarningCongressRaw;
import com.ayd2.congress.dtos.reports.EarningReport;
import com.ayd2.congress.models.Wallet.WalletTransactionEntity;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransactionEntity, Long> {
    @Query("""
                SELECT new com.ayd2.congress.dtos.reports.EarningReport(
                    c.name,
                    c.endDate,
                    c.startDate,
                    c.location.name,
                    c.organization.name,
                    CAST(SUM(CASE WHEN w.transactionType = com.ayd2.congress.models.Enums.TransactionType.USER_PAYMENT
                                  THEN w.amount ELSE 0 END) AS double),
                    CAST(SUM(CASE WHEN w.transactionType = com.ayd2.congress.models.Enums.TransactionType.PAYMENT_TO_SYSTEM
                                  THEN w.amount ELSE 0 END) AS double)
                )
                FROM WalletTransactionEntity w
                JOIN w.payment p
                JOIN p.congress c
                WHERE w.transactionType IN (
                    com.ayd2.congress.models.Enums.TransactionType.USER_PAYMENT,
                    com.ayd2.congress.models.Enums.TransactionType.PAYMENT_TO_SYSTEM
                )
                AND (:organizationId IS NULL OR c.organization.id = :organizationId)
                AND (w.date BETWEEN :startDate AND :endDate)
                GROUP BY c.id, c.name, c.endDate, c.startDate, c.location.name, c.organization.name, c.organization.id
                ORDER BY c.organization.name ASC
            """)
    List<EarningReport> getEarningsReport(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("organizationId") Long organizationId);

    @Query("""
                SELECT new com.ayd2.congress.dtos.reports.EarningCongressRaw(
                    c.id,
                    c.name,
                    CAST(SUM(CASE WHEN w.transactionType = com.ayd2.congress.models.Enums.TransactionType.USER_PAYMENT
                                  THEN w.amount ELSE 0 END) AS double),
                    CAST(SUM(CASE WHEN w.transactionType = com.ayd2.congress.models.Enums.TransactionType.PAYMENT_TO_SYSTEM
                                  THEN w.amount ELSE 0 END) AS double),
                    CAST(SUM(CASE WHEN w.transactionType = com.ayd2.congress.models.Enums.TransactionType.PAYMENT_TO_CONGRESS
                                  THEN w.amount ELSE 0 END) AS double)
                )
                FROM WalletTransactionEntity w
                JOIN w.payment p
                JOIN p.congress c
                WHERE (:congressId IS NULL OR c.id = :congressId)
                AND (:startDate IS NULL OR c.startDate >= :startDate)
                AND (:endDate IS NULL OR c.endDate <= :endDate)
                GROUP BY c.id, c.name
                ORDER BY c.name ASC
            """)
    List<EarningCongressRaw> getEarningsCongressReport(
            @Param("congressId") Long congressId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
