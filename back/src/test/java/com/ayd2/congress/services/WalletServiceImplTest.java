package com.ayd2.congress.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
import com.ayd2.congress.services.Wallet.WalletServiceImpl;
import com.ayd2.congress.services.systemconfig.SystemConfigService;

@ExtendWith(MockitoExtension.class)
public class WalletServiceImplTest {

    private static final Long USER_ID = 1L;
    private static final Long WALLET_ID = 10L;
    private static final Long PAYMENT_ID = 99L;

    private static final Double INITIAL_WALLET_VALUE = 500.0;
    private static final Double RECHARGE_AMOUNT = 150.0;
    private static final Double PAYMENT_AMOUNT = 200.0;
    private static final Double SYSTEM_PERCENTAGE = 10.0;

    @Mock
    private WalletRepository walletRepository;
    @Mock
    private RechargeWalletRepository rechargeRepository;
    @Mock
    private WalletMapper walletMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private WalletTransactionRepository walletTransactionRepository;
    @Mock
    private SystemConfigService systemConfigService;

    @InjectMocks
    private WalletServiceImpl walletService;

    private UserEntity userEntity;
    private WalletEntity walletEntity;
    private WalletResponse walletResponse;
    private PaymentEntity paymentEntity;

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity();
        userEntity.setId(USER_ID);
        userEntity.setName("Juan");

        walletEntity = new WalletEntity();
        walletEntity.setId(WALLET_ID);
        walletEntity.setUser(userEntity);
        walletEntity.setValue(INITIAL_WALLET_VALUE);

        walletResponse = new WalletResponse(WALLET_ID, INITIAL_WALLET_VALUE);

        paymentEntity = new PaymentEntity();
        paymentEntity.setId(PAYMENT_ID);
    }

    @Test
    void testCreateWallet() throws Exception {
        // Arrange
        ArgumentCaptor<WalletEntity> walletCaptor = ArgumentCaptor.forClass(WalletEntity.class);

        when(walletRepository.existsByUserId(USER_ID)).thenReturn(false);
        when(walletRepository.save(any(WalletEntity.class))).thenAnswer(invocation -> {
            WalletEntity entity = invocation.getArgument(0);
            entity.setId(WALLET_ID);
            return entity;
        });

        // Act
        WalletEntity result = walletService.create(userEntity);

        // Assert
        assertAll(
                () -> verify(walletRepository).save(walletCaptor.capture()),
                () -> assertEquals(USER_ID, walletCaptor.getValue().getUser().getId()),
                () -> assertEquals(WALLET_ID, result.getId()),
                () -> assertEquals(USER_ID, result.getUser().getId())
        );
    }

    @Test
    void testCreateWalletWhenUserAlreadyHasWallet() {
        // Arrange
        when(walletRepository.existsByUserId(USER_ID)).thenReturn(true);

        // Assert
        assertThrows(DuplicatedEntityException.class,
                () -> walletService.create(userEntity));
    }

    @Test
    void testRecharge() throws Exception {
        // Arrange
        RechargeRequest request = new RechargeRequest(
                RECHARGE_AMOUNT,
                LocalDateTime.of(2026, 3, 10, 10, 30)
        );

        WalletServiceImpl spy = spy(walletService);
        ArgumentCaptor<RechargeWalletEntity> historyCaptor = ArgumentCaptor.forClass(RechargeWalletEntity.class);
        ArgumentCaptor<WalletEntity> walletCaptor = ArgumentCaptor.forClass(WalletEntity.class);

        doReturn(walletEntity).when(spy).getByUserId(USER_ID);

        WalletResponse updatedResponse = new WalletResponse(WALLET_ID, INITIAL_WALLET_VALUE + RECHARGE_AMOUNT);
        when(walletRepository.save(any(WalletEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(walletMapper.toResponse(any(WalletEntity.class))).thenReturn(updatedResponse);

        // Act
        WalletResponse result = spy.recharge(request, USER_ID);

        // Assert
        assertAll(
                () -> verify(rechargeRepository).save(historyCaptor.capture()),
                () -> verify(walletRepository).save(walletCaptor.capture()),
                () -> assertEquals(RECHARGE_AMOUNT, historyCaptor.getValue().getAmount()),
                () -> assertEquals(request.getDate(), historyCaptor.getValue().getDate()),
                () -> assertEquals(WALLET_ID, historyCaptor.getValue().getWallet().getId()),
                () -> assertEquals(INITIAL_WALLET_VALUE + RECHARGE_AMOUNT, walletCaptor.getValue().getValue()),
                () -> assertEquals(WALLET_ID, result.getId()),
                () -> assertEquals(INITIAL_WALLET_VALUE + RECHARGE_AMOUNT, result.getValue())
        );
    }

    @Test
    void testGetById() throws Exception {
        // Arrange
        when(walletRepository.findById(WALLET_ID)).thenReturn(Optional.of(walletEntity));

        // Act
        WalletEntity result = walletService.getById(WALLET_ID);

        // Assert
        assertAll(
                () -> assertEquals(WALLET_ID, result.getId()),
                () -> assertEquals(INITIAL_WALLET_VALUE, result.getValue())
        );
    }

    @Test
    void testGetByIdWhenNotFound() {
        // Arrange
        when(walletRepository.findById(WALLET_ID)).thenReturn(Optional.empty());

        // Assert
        assertThrows(NotFoundException.class,
                () -> walletService.getById(WALLET_ID));
    }

    @Test
    void testGetHistoryRechargeByWalletId() {
        // Arrange
        RechargeWalletEntity recharge1 = new RechargeWalletEntity();
        recharge1.setAmount(100.0);
        recharge1.setDate(LocalDateTime.of(2026, 3, 1, 8, 0));

        RechargeWalletEntity recharge2 = new RechargeWalletEntity();
        recharge2.setAmount(200.0);
        recharge2.setDate(LocalDateTime.of(2026, 3, 2, 9, 0));

        when(rechargeRepository.findAllByWalletId(WALLET_ID)).thenReturn(List.of(recharge1, recharge2));

        // Act
        List<RechargeHistory> result = walletService.getHistoryRechargeByWalletId(WALLET_ID);

        // Assert
        assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertEquals(100.0, result.get(0).getAmount()),
                () -> assertEquals(LocalDateTime.of(2026, 3, 1, 8, 0), result.get(0).getDate()),
                () -> assertEquals(200.0, result.get(1).getAmount())
        );
    }

    @Test
    void testGetByUserId() throws Exception {
        // Arrange
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(userEntity));
        when(walletRepository.findByUserId(USER_ID)).thenReturn(Optional.of(walletEntity));

        // Act
        WalletEntity result = walletService.getByUserId(USER_ID);

        // Assert
        assertAll(
                () -> assertEquals(WALLET_ID, result.getId()),
                () -> assertEquals(USER_ID, result.getUser().getId())
        );
    }

    @Test
    void testGetByUserIdWhenUserNotFound() {
        // Arrange
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        // Assert
        assertThrows(NotFoundException.class,
                () -> walletService.getByUserId(USER_ID));
    }

    @Test
    void testGetByUserIdWhenWalletNotFound() {
        // Arrange
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(userEntity));
        when(walletRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

        // Assert
        assertThrows(NotFoundException.class,
                () -> walletService.getByUserId(USER_ID));
    }

    @Test
    void testGetByUserIdResponse() throws Exception {
        // Arrange
        WalletServiceImpl spy = spy(walletService);

        doReturn(walletEntity).when(spy).getByUserId(USER_ID);
        when(walletMapper.toResponse(walletEntity)).thenReturn(walletResponse);

        // Act
        WalletResponse result = spy.getByUserIdResponse(USER_ID);

        // Assert
        assertAll(
                () -> assertEquals(WALLET_ID, result.getId()),
                () -> assertEquals(INITIAL_WALLET_VALUE, result.getValue())
        );
    }

    @Test
    void testCreateTransaction() throws Exception {
        // Arrange
        WalletServiceImpl spy = spy(walletService);
        ArgumentCaptor<WalletTransactionEntity> transactionCaptor = ArgumentCaptor.forClass(WalletTransactionEntity.class);
        ArgumentCaptor<WalletEntity> walletCaptor = ArgumentCaptor.forClass(WalletEntity.class);

        SystemConfigEntity config = new SystemConfigEntity();
        config.setPercentage(SYSTEM_PERCENTAGE);

        doReturn(walletEntity).when(spy).getById(WALLET_ID);
        when(systemConfigService.getConfiguration()).thenReturn(config);

        // Act
        spy.createTransaction(WALLET_ID, PAYMENT_AMOUNT, paymentEntity);

        // Assert
        double systemAmount = PAYMENT_AMOUNT * (SYSTEM_PERCENTAGE / 100);
        double congressAmount = PAYMENT_AMOUNT - systemAmount;
        double newWalletValue = INITIAL_WALLET_VALUE - PAYMENT_AMOUNT;

        assertAll(
                () -> verify(walletTransactionRepository, times(3)).save(transactionCaptor.capture()),
                () -> verify(walletRepository).save(walletCaptor.capture()),
                () -> assertEquals(systemAmount, transactionCaptor.getAllValues().get(0).getAmount()),
                () -> assertEquals(TransactionType.PAYMENT_TO_SYSTEM, transactionCaptor.getAllValues().get(0).getTransactionType()),
                () -> assertEquals(congressAmount, transactionCaptor.getAllValues().get(1).getAmount()),
                () -> assertEquals(TransactionType.PAYMENT_TO_CONGRESS, transactionCaptor.getAllValues().get(1).getTransactionType()),
                () -> assertEquals(PAYMENT_AMOUNT, transactionCaptor.getAllValues().get(2).getAmount()),
                () -> assertEquals(TransactionType.USER_PAYMENT, transactionCaptor.getAllValues().get(2).getTransactionType()),
                () -> assertEquals(LocalDate.now(), transactionCaptor.getAllValues().get(0).getDate()),
                () -> assertEquals(paymentEntity, transactionCaptor.getAllValues().get(0).getPayment()),
                () -> assertEquals(newWalletValue, walletCaptor.getValue().getValue())
        );
    }

    @Test
    void testCreateWalletTransaction() {
        // Arrange
        ArgumentCaptor<WalletTransactionEntity> transactionCaptor = ArgumentCaptor.forClass(WalletTransactionEntity.class);

        // Act
        walletService.createWalletTransaction(75.0, TransactionType.USER_PAYMENT, paymentEntity);

        // Assert
        assertAll(
                () -> verify(walletTransactionRepository).save(transactionCaptor.capture()),
                () -> assertEquals(75.0, transactionCaptor.getValue().getAmount()),
                () -> assertEquals(TransactionType.USER_PAYMENT, transactionCaptor.getValue().getTransactionType()),
                () -> assertEquals(LocalDate.now(), transactionCaptor.getValue().getDate()),
                () -> assertEquals(paymentEntity, transactionCaptor.getValue().getPayment())
        );
    }
}