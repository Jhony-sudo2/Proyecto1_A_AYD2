package com.ayd2.congress.services.Wallet;

import java.util.List;

import com.ayd2.congress.dtos.Wallet.RechargeHistory;
import com.ayd2.congress.dtos.Wallet.RechargeRequest;
import com.ayd2.congress.dtos.Wallet.WalletResponse;
import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.models.Pay.PaymentEntity;
import com.ayd2.congress.models.User.UserEntity;
import com.ayd2.congress.models.Wallet.WalletEntity;

public interface WalletService {
    WalletEntity create(UserEntity user) throws DuplicatedEntityException;
    WalletResponse recharge(RechargeRequest request,Long userId) throws NotFoundException;
    WalletEntity getById(Long walletId) throws NotFoundException;
    WalletEntity getByUserId(Long walletId) throws NotFoundException;
    List<RechargeHistory> getHistoryRechargeByWalletId(Long walletId);
    WalletResponse getByUserIdResponse(Long userId) throws NotFoundException;
    void createTransaction(Long walletId,Double amount,PaymentEntity paymentId) throws NotFoundException;

}   
