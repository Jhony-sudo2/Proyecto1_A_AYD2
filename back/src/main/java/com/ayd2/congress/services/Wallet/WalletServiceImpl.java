package com.ayd2.congress.services.Wallet;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ayd2.congress.dtos.Wallet.RechargeHistory;
import com.ayd2.congress.dtos.Wallet.RechargeRequest;
import com.ayd2.congress.dtos.Wallet.WalletResponse;
import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.models.User.UserEntity;
import com.ayd2.congress.models.Wallet.RechargeWalletEntity;
import com.ayd2.congress.models.Wallet.WalletEntity;
import com.ayd2.congress.repositories.Wallet.RechargeWalletRepository;
import com.ayd2.congress.repositories.Wallet.WalletRepository;

import jakarta.transaction.Transactional;

@Service
public class WalletServiceImpl implements WalletService{
    private final WalletRepository walletRepository;
    private final RechargeWalletRepository rechargeRepository;

    
    public WalletServiceImpl(WalletRepository walletRepository, RechargeWalletRepository rechargeRepository) {
        this.walletRepository = walletRepository;
        this.rechargeRepository = rechargeRepository;
    }

    @Override
    public WalletEntity create(UserEntity user) throws DuplicatedEntityException{
        if (walletRepository.existByUserId(user.getId())) 
            throw new DuplicatedEntityException("User already has a wallet ");
        
        WalletEntity newEntity =  new WalletEntity();
        newEntity.setUser(user);
        return walletRepository.save(newEntity);
    }

    @Override
    @Transactional
    public WalletResponse recharge(RechargeRequest request) throws NotFoundException {
        WalletEntity walletToUpdate = getById(request.getWalletId());
        walletToUpdate.setValue(walletToUpdate.getValue() + request.getAmount());

        RechargeWalletEntity newHistory = request.createEntity(walletToUpdate);
        rechargeRepository.save(newHistory);
        walletRepository.save(walletToUpdate);
        return new WalletResponse(walletToUpdate);
    }

    @Override
    public WalletEntity getById(Long walletId) throws NotFoundException {
        return walletRepository.findById(walletId)
            .orElseThrow(()-> new NotFoundException("Wallet not found"));
    }

    @Override
    public List<RechargeHistory> getHistoryRechargeByWalletId(Long walletId) {
        return rechargeRepository.findAllByWalletId(walletId)
            .stream()
            .map(e -> new RechargeHistory(
                e.getAmount(), e.getDate()))
            .toList();
    }
    
}
