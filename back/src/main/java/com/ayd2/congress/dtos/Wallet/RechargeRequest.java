package com.ayd2.congress.dtos.Wallet;

import java.time.LocalDateTime;

import com.ayd2.congress.models.Wallet.RechargeWalletEntity;
import com.ayd2.congress.models.Wallet.WalletEntity;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Value;

@Value
public class RechargeRequest {
    @NotNull(message = "amount is required")
    @Positive(message = "amount must be positive")
    @DecimalMin(value = "10.0", inclusive = true, message = "amount must be at least 10")
    Double amount;
    @NotNull(message = "date is required")
    LocalDateTime date;

    public RechargeWalletEntity createEntity(WalletEntity walletEntity){
        RechargeWalletEntity entity = new RechargeWalletEntity();
        entity.setAmount(amount);
        entity.setDate(date);
        entity.setWallet(walletEntity);
        return entity;
    }

}
