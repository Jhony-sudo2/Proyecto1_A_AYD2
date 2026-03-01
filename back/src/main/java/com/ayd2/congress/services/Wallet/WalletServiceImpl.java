package com.ayd2.congress.services.Wallet;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ayd2.congress.dtos.Wallet.RechargeHistory;
import com.ayd2.congress.dtos.Wallet.RechargeRequest;
import com.ayd2.congress.dtos.Wallet.WalletResponse;
import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.mappers.WalletMapper;
import com.ayd2.congress.models.User.UserEntity;
import com.ayd2.congress.models.Wallet.RechargeWalletEntity;
import com.ayd2.congress.models.Wallet.WalletEntity;
import com.ayd2.congress.repositories.UserRepository;
import com.ayd2.congress.repositories.Wallet.RechargeWalletRepository;
import com.ayd2.congress.repositories.Wallet.WalletRepository;

import jakarta.transaction.Transactional;

@Service
public class WalletServiceImpl implements WalletService{
    private final WalletRepository walletRepository;
    private final RechargeWalletRepository rechargeRepository;
    private final WalletMapper walletMapper;
    private final UserRepository userRepository;
    
    public WalletServiceImpl(WalletRepository walletRepository, RechargeWalletRepository rechargeRepository,WalletMapper walletMapper
        ,UserRepository userRepository
    ) {
        this.walletRepository = walletRepository;
        this.rechargeRepository = rechargeRepository;
        this.walletMapper = walletMapper;
        this.userRepository = userRepository;
    }

    @Override
    public WalletEntity create(UserEntity user) throws DuplicatedEntityException{
        if (walletRepository.existsByUserId(user.getId())) 
            throw new DuplicatedEntityException("User already has a wallet ");
        
        WalletEntity newEntity =  new WalletEntity();
        newEntity.setUser(user);
        return walletRepository.save(newEntity);
    }

    @Override
    @Transactional
    public WalletResponse recharge(RechargeRequest request,Long userId) throws NotFoundException {
        WalletEntity walletToUpdate = getByUserId(userId);
        walletToUpdate.setValue(walletToUpdate.getValue() + request.getAmount());

        RechargeWalletEntity newHistory = request.createEntity(walletToUpdate);
        rechargeRepository.save(newHistory);
        return walletMapper.toResponse(walletRepository.save(walletToUpdate))  ;
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

    @Override
    public WalletEntity getByUserId(Long userId) throws NotFoundException {
        userRepository.findById(userId)
            .orElseThrow(()-> new NotFoundException("USER NOT FOUND"));
        return walletRepository.findByUserId(userId)
            .orElseThrow(()-> new NotFoundException("WALLET NOT FOUND"));
    }

    @Override
    public WalletResponse getByUserIdResponse(Long userId) throws NotFoundException {
        return walletMapper.toResponse(getByUserId(userId));
    }
    
}
