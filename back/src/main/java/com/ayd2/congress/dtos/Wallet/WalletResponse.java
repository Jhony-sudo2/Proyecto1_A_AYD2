package com.ayd2.congress.dtos.Wallet;

import com.ayd2.congress.models.Wallet.WalletEntity;

import lombok.Value;

@Value
public class WalletResponse {
    private Long id;
    private Double value;

    public WalletResponse(WalletEntity entity){
        this.id = entity.getId();
        this.value = entity.getValue();
    }

}
