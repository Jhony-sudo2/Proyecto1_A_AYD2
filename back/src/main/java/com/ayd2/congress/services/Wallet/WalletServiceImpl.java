package com.ayd2.congress.services.Wallet;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ayd2.congress.dtos.Wallet.RechargeHistory;
import com.ayd2.congress.dtos.Wallet.RechargeRequest;
import com.ayd2.congress.dtos.Wallet.WalletResponse;
import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.mappers.WalletMapper;
import com.ayd2.congress.models.SystemConfigEntity;
import com.ayd2.congress.models.Enums.TransactionType;
import com.ayd2.congress.models.Pay.PaymentEntity;
import com.ayd2.congress.models.User.UserEntity;
import com.ayd2.congress.models.Wallet.RechargeWalletEntity;
import com.ayd2.congress.models.Wallet.WalletEntity;
import com.ayd2.congress.models.Wallet.WalletTransactionEntity;
import com.ayd2.congress.repositories.UserRepository;
import com.ayd2.congress.repositories.Wallet.RechargeWalletRepository;
import com.ayd2.congress.repositories.Wallet.WalletRepository;
import com.ayd2.congress.repositories.Wallet.WalletTransactionRepository;
import com.ayd2.congress.services.systemconfig.SystemConfigService;

import jakarta.transaction.Transactional;

@Service
public class WalletServiceImpl implements WalletService{
    private final WalletRepository walletRepository;
    private final RechargeWalletRepository rechargeRepository;
    private final WalletMapper walletMapper;
    private final UserRepository userRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final SystemConfigService systemConfigService;

    public WalletServiceImpl(WalletRepository walletRepository, RechargeWalletRepository rechargeRepository,WalletMapper walletMapper
        ,UserRepository userRepository, WalletTransactionRepository walletTransactionRepository, SystemConfigService systemConfigService
    ) {
        this.walletRepository = walletRepository;
        this.rechargeRepository = rechargeRepository;
        this.walletMapper = walletMapper;
        this.userRepository = userRepository;
        this.walletTransactionRepository = walletTransactionRepository;
        this.systemConfigService = systemConfigService;
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

    @Transactional
    @Override
    public void createTransaction(Long walletId, Double amount,PaymentEntity paymentId) throws NotFoundException {
        WalletEntity wallet = getById(walletId);
        SystemConfigEntity systemConfig = systemConfigService.getConfiguration();
        
        Double percentage = systemConfig.getPercentage();
        Double systemAmount = amount * (percentage / 100);
        Double congressAmount = amount - systemAmount;
        Double newValue = wallet.getValue() - amount;
        
        createWalletTransaction(systemAmount, TransactionType.PAYMENT_TO_SYSTEM,paymentId);
        createWalletTransaction(congressAmount, TransactionType.PAYMENT_TO_CONGRESS,paymentId);
        createWalletTransaction(amount, TransactionType.USER_PAYMENT,paymentId);
        wallet.setValue(newValue);
        
        walletRepository.save(wallet);
    }
    
    public void createWalletTransaction(Double amount, TransactionType type,PaymentEntity paymentEntity) {
        WalletTransactionEntity transaction = new WalletTransactionEntity();
        transaction.setAmount(amount);
        transaction.setDate(LocalDate.now());
        transaction.setTransactionType(type);
        transaction.setPayment(paymentEntity);
        walletTransactionRepository.save(transaction);
    }
}
