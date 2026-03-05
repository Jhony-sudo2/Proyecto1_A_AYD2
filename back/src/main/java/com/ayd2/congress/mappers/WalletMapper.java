package com.ayd2.congress.mappers;

import org.mapstruct.Mapper;

import com.ayd2.congress.dtos.Wallet.WalletResponse;
import com.ayd2.congress.models.Wallet.WalletEntity;

@Mapper(componentModel = "spring")
public interface WalletMapper {
    WalletResponse toResponse(WalletEntity entity);
}