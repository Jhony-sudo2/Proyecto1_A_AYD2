package com.ayd2.congress.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ayd2.congress.dtos.Wallet.RechargeRequest;
import com.ayd2.congress.dtos.Wallet.WalletResponse;
import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.models.User.UserEntity;
import com.ayd2.congress.models.Wallet.RechargeWalletEntity;
import com.ayd2.congress.models.Wallet.WalletEntity;
import com.ayd2.congress.repositories.Wallet.RechargeWalletRepository;
import com.ayd2.congress.repositories.Wallet.WalletRepository;
import com.ayd2.congress.services.Wallet.WalletServiceImpl;

@ExtendWith(MockitoExtension.class)
public class WalletServiceImplTest {
    private final static Long USER_ID = 1L;
    private final static Long WALLET_ID = 1L;
    private static final Double INITIAL_VALUE = 50.0;
    private static final Double AMOUNT = 20.0;
    private static final Double EXPECTED_VALUE = 70.0;
    private static final LocalDateTime DATE = LocalDateTime.of(2026, 2, 25, 10, 0);

    @Mock
    private WalletRepository walletRepository;
    @Mock
    private RechargeWalletRepository rechargeRepository;
    @InjectMocks
    private WalletServiceImpl service;

    @Test
    void createWalletTest() throws DuplicatedEntityException {
        // ARRANGE
        UserEntity userEntity = new UserEntity();
        userEntity.setId(USER_ID);
        WalletEntity walletEntity = new WalletEntity();
        walletEntity.setId(WALLET_ID);
        walletEntity.setUser(userEntity);
        walletEntity.setValue(0.0);
        ArgumentCaptor<WalletEntity> captor = ArgumentCaptor.forClass(WalletEntity.class);
        when(walletRepository.save(any(WalletEntity.class))).thenReturn(walletEntity);

        // ACT
        WalletEntity result = service.create(userEntity);
        // ASSERTS
        assertAll(
                () -> verify(walletRepository).save(captor.capture()),
                () -> assertEquals(walletEntity.getId(), result.getId()),
                () -> assertEquals(walletEntity.getUser().getId(), result.getUser().getId()),
                () -> assertEquals(walletEntity.getValue(), result.getValue()));
    }

    @Test
    void createWallet_when_UserDuplicated() {
        // ARRANGE
        UserEntity userEntity = new UserEntity();
        userEntity.setId(USER_ID);
        WalletEntity walletEntity = new WalletEntity();
        walletEntity.setId(WALLET_ID);
        walletEntity.setUser(userEntity);
        walletEntity.setValue(0.0);
        when(walletRepository.existByUserId(USER_ID)).thenReturn(true);
        assertThrows(DuplicatedEntityException.class,
                () -> service.create(userEntity));
    }

    @Test
    void getWalletByIdTest() throws NotFoundException {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(USER_ID);
        WalletEntity walletEntity = new WalletEntity();
        walletEntity.setId(WALLET_ID);
        walletEntity.setUser(userEntity);
        walletEntity.setValue(0.0);
        when(walletRepository.findById(WALLET_ID)).thenReturn(Optional.of(walletEntity));

        WalletEntity result = service.getById(WALLET_ID);

        assertAll(
                () -> assertEquals(walletEntity.getId(), result.getId()),
                () -> assertEquals(walletEntity.getUser().getId(), result.getUser().getId()),
                () -> assertEquals(walletEntity.getValue(), result.getValue()));
    }

    @Test
    void getWalletById_NotFoundTest(){
        when(walletRepository.findById(WALLET_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, 
            ()-> service.getById(WALLET_ID));
    }

    @Test
    void rechargeTest() throws NotFoundException {
        //ARRANGE
        RechargeRequest request = new RechargeRequest(WALLET_ID, AMOUNT, DATE);

        WalletEntity wallet = new WalletEntity();
        wallet.setId(WALLET_ID);
        wallet.setValue(INITIAL_VALUE);

        when(walletRepository.findById(WALLET_ID)).thenReturn(Optional.of(wallet));

        ArgumentCaptor<RechargeWalletEntity> historyCaptor = ArgumentCaptor.forClass(RechargeWalletEntity.class);

        ArgumentCaptor<WalletEntity> walletCaptor = ArgumentCaptor.forClass(WalletEntity.class);

        when(rechargeRepository.save(any(RechargeWalletEntity.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        when(walletRepository.save(any(WalletEntity.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        //ACT
        WalletResponse response = service.recharge(request);

        //ASSERT
        verify(walletRepository).findById(WALLET_ID);

        verify(rechargeRepository).save(historyCaptor.capture());
        RechargeWalletEntity savedHistory = historyCaptor.getValue();

        verify(walletRepository).save(walletCaptor.capture());
        WalletEntity savedWallet = walletCaptor.getValue();

        assertAll(
                () -> assertEquals(EXPECTED_VALUE, savedWallet.getValue(), 0.0001),
                () -> assertEquals(WALLET_ID, savedWallet.getId()),

                () -> assertNotNull(savedHistory),
                () -> assertEquals(WALLET_ID, savedHistory.getWallet().getId()),
                () -> assertEquals(AMOUNT, savedHistory.getAmount(), 0.0001),
                () -> assertEquals(DATE, savedHistory.getDate()),

                () -> assertEquals(WALLET_ID, response.getId()),
                () -> assertEquals(EXPECTED_VALUE, response.getValue(), 0.0001));

        verifyNoMoreInteractions(walletRepository, rechargeRepository);
    }
}
