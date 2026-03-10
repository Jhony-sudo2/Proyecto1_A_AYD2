package com.ayd2.congress.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ayd2.congress.compositePrimaryKeys.AttendeeId;
import com.ayd2.congress.dtos.Inscription.InscriptionResponse;
import com.ayd2.congress.dtos.Inscription.PayRequest;
import com.ayd2.congress.dtos.Inscription.PayResponse;
import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.exceptions.InsufficientFundsException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.mappers.InscriptionMapper;
import com.ayd2.congress.models.Congress.AttendeeRol;
import com.ayd2.congress.models.Congress.CongressEntity;
import com.ayd2.congress.models.Congress.InscriptionEntity;
import com.ayd2.congress.models.Pay.PaymentEntity;
import com.ayd2.congress.models.User.UserEntity;
import com.ayd2.congress.models.Wallet.WalletEntity;
import com.ayd2.congress.repositories.Congress.AttendeeRolRepository;
import com.ayd2.congress.repositories.Congress.InscriptionRepository;
import com.ayd2.congress.repositories.Pay.PaymentRepository;
import com.ayd2.congress.services.Congress.CongressService;
import com.ayd2.congress.services.Inscription.InscriptionServiceImpl;
import com.ayd2.congress.services.User.UserService;
import com.ayd2.congress.services.Wallet.WalletService;

@ExtendWith(MockitoExtension.class)
public class InscriptionServiceImplTest {

    private static final Long USER_ID = 1L;
    private static final Long CONGRESS_ID = 2L;
    private static final Long ROL_ID = 3L;
    private static final Long ASSISTENT_ROL_ID = 1L;
    private static final Long PAYMENT_ID = 10L;
    private static final Long INSCRIPTION_ID = 20L;

    private static final String USER_NAME = "Juan Perez";
    private static final String CONGRESS_NAME = "Tech Congress";
    private static final String ROL_NAME = "Speaker";

    private static final Double WALLET_VALUE = 500.0;
    private static final Double CONGRESS_PRICE = 300.0;
    private static final Double INSUFFICIENT_WALLET_VALUE = 100.0;

    private static final LocalDate PAYMENT_DATE = LocalDate.of(2026, 3, 10);

    @Mock
    private UserService userService;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private InscriptionRepository inscriptionRepository;
    @Mock
    private AttendeeRolRepository attendeeRolRepository;
    @Mock
    private CongressService congressService;
    @Mock
    private WalletService walletService;
    @Mock
    private InscriptionMapper inscriptionMapper;

    @InjectMocks
    private InscriptionServiceImpl inscriptionService;

    private UserEntity userEntity;
    private CongressEntity congressEntity;
    private AttendeeRol attendeeRol;
    private WalletEntity walletEntity;
    private PaymentEntity paymentEntity;
    private InscriptionEntity inscriptionEntity;
    private PayRequest payRequest;
    private PayResponse payResponse;
    private InscriptionResponse inscriptionResponse;

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity();
        userEntity.setId(USER_ID);
        userEntity.setName(USER_NAME);

        congressEntity = new CongressEntity();
        congressEntity.setId(CONGRESS_ID);
        congressEntity.setName(CONGRESS_NAME);
        congressEntity.setPrice(CONGRESS_PRICE);

        attendeeRol = new AttendeeRol();
        attendeeRol.setId(ROL_ID);
        attendeeRol.setName(ROL_NAME);

        walletEntity = new WalletEntity();
        walletEntity.setValue(WALLET_VALUE);

        paymentEntity = new PaymentEntity();
        paymentEntity.setId(PAYMENT_ID);
        paymentEntity.setUser(userEntity);
        paymentEntity.setCongress(congressEntity);
        paymentEntity.setDate(PAYMENT_DATE);
        paymentEntity.setTotal(CONGRESS_PRICE);

        inscriptionEntity = new InscriptionEntity();
        inscriptionEntity.setCongress(congressEntity);
        inscriptionEntity.setUser(userEntity);
        inscriptionEntity.setAttendeeRol(attendeeRol);
        inscriptionEntity.setId(new AttendeeId(CONGRESS_ID, USER_ID, ROL_ID));

        payRequest = new PayRequest(USER_ID, CONGRESS_ID, PAYMENT_DATE);

        payResponse = new PayResponse(
                PAYMENT_ID,
                USER_ID,
                USER_NAME,
                CONGRESS_ID,
                CONGRESS_NAME,
                CONGRESS_PRICE,
                PAYMENT_DATE
        );

        inscriptionResponse = new InscriptionResponse(
                CONGRESS_ID,
                CONGRESS_NAME,
                USER_ID,
                USER_NAME,
                ROL_NAME
        );
    }

    @Test
    void testEnroll() throws Exception {
        // Arrange
        ArgumentCaptor<InscriptionEntity> inscriptionCaptor = ArgumentCaptor.forClass(InscriptionEntity.class);

        when(congressService.getById(CONGRESS_ID)).thenReturn(congressEntity);
        when(attendeeRolRepository.findById(ROL_ID)).thenReturn(Optional.of(attendeeRol));
        when(userService.getById(USER_ID)).thenReturn(userEntity);
        when(inscriptionRepository.existsByUserIdAndCongressIdAndAttendeeRolId(USER_ID, CONGRESS_ID, ROL_ID))
                .thenReturn(false);

        // Act
        inscriptionService.enroll(USER_ID, ROL_ID, CONGRESS_ID, false);

        // Assert
        assertAll(
                () -> verify(inscriptionRepository).save(inscriptionCaptor.capture()),
                () -> assertEquals(CONGRESS_ID, inscriptionCaptor.getValue().getId().getCongressId()),
                () -> assertEquals(USER_ID, inscriptionCaptor.getValue().getId().getUserId()),
                () -> assertEquals(ROL_ID, inscriptionCaptor.getValue().getId().getAttendeeRolId()),
                () -> assertEquals(USER_ID, inscriptionCaptor.getValue().getUser().getId()),
                () -> assertEquals(CONGRESS_ID, inscriptionCaptor.getValue().getCongress().getId()),
                () -> assertEquals(ROL_ID, inscriptionCaptor.getValue().getAttendeeRol().getId())
        );
    }

    @Test
    void testEnrollWhenAlreadyExistsAndIgnoreIfExistsTrue() throws Exception {
        // Arrange
        when(congressService.getById(CONGRESS_ID)).thenReturn(congressEntity);
        when(attendeeRolRepository.findById(ROL_ID)).thenReturn(Optional.of(attendeeRol));
        when(userService.getById(USER_ID)).thenReturn(userEntity);
        when(inscriptionRepository.existsByUserIdAndCongressIdAndAttendeeRolId(USER_ID, CONGRESS_ID, ROL_ID))
                .thenReturn(true);

        // Act
        inscriptionService.enroll(USER_ID, ROL_ID, CONGRESS_ID, true);

        // Assert
        // No exception and no save
        verify(inscriptionRepository).existsByUserIdAndCongressIdAndAttendeeRolId(USER_ID, CONGRESS_ID, ROL_ID);
    }

    @Test
    void testEnrollWhenAlreadyExistsAndIgnoreIfExistsFalse() throws Exception {
        // Arrange
        when(congressService.getById(CONGRESS_ID)).thenReturn(congressEntity);
        when(attendeeRolRepository.findById(ROL_ID)).thenReturn(Optional.of(attendeeRol));
        when(userService.getById(USER_ID)).thenReturn(userEntity);
        when(inscriptionRepository.existsByUserIdAndCongressIdAndAttendeeRolId(USER_ID, CONGRESS_ID, ROL_ID))
                .thenReturn(true);

        // Assert
        assertThrows(DuplicatedEntityException.class,
                () -> inscriptionService.enroll(USER_ID, ROL_ID, CONGRESS_ID, false));
    }

    @Test
    void testGetRolById() throws Exception {
        // Arrange
        when(attendeeRolRepository.findById(ROL_ID)).thenReturn(Optional.of(attendeeRol));

        // Act
        AttendeeRol result = inscriptionService.getRolById(ROL_ID);

        // Assert
        assertAll(
                () -> assertEquals(ROL_ID, result.getId()),
                () -> assertEquals(ROL_NAME, result.getName())
        );
    }

    @Test
    void testGetRolByIdWhenNotFound() {
        // Arrange
        when(attendeeRolRepository.findById(ROL_ID)).thenReturn(Optional.empty());

        // Assert
        assertThrows(NotFoundException.class,
                () -> inscriptionService.getRolById(ROL_ID));
    }

    @Test
    void testPay() throws Exception {
        // Arrange
        InscriptionServiceImpl spy = spy(inscriptionService);
        ArgumentCaptor<PaymentEntity> paymentCaptor = ArgumentCaptor.forClass(PaymentEntity.class);

        when(userService.getById(USER_ID)).thenReturn(userEntity);
        when(congressService.getById(CONGRESS_ID)).thenReturn(congressEntity);
        when(walletService.getByUserId(USER_ID)).thenReturn(walletEntity);
        when(paymentRepository.existsByUserIdAndCongressId(USER_ID, CONGRESS_ID)).thenReturn(false);

        doNothing().when(spy).enroll(USER_ID, ASSISTENT_ROL_ID, CONGRESS_ID, false);

        when(paymentRepository.save(any(PaymentEntity.class))).thenAnswer(invocation -> {
            PaymentEntity entity = invocation.getArgument(0);
            entity.setId(PAYMENT_ID);
            return entity;
        });
        when(inscriptionMapper.toPayResponse(any(PaymentEntity.class))).thenReturn(payResponse);

        // Act
        PayResponse result = spy.pay(payRequest);

        // Assert
        assertAll(
                () -> verify(spy).enroll(USER_ID, ASSISTENT_ROL_ID, CONGRESS_ID, false),
                () -> verify(paymentRepository).save(paymentCaptor.capture()),
                () -> verify(walletService).createTransaction(USER_ID, CONGRESS_PRICE, paymentCaptor.getValue()),
                () -> assertEquals(USER_ID, paymentCaptor.getValue().getUser().getId()),
                () -> assertEquals(CONGRESS_ID, paymentCaptor.getValue().getCongress().getId()),
                () -> assertEquals(PAYMENT_DATE, paymentCaptor.getValue().getDate()),
                () -> assertEquals(CONGRESS_PRICE, paymentCaptor.getValue().getTotal()),
                () -> assertEquals(PAYMENT_ID, result.getId()),
                () -> assertEquals(USER_ID, result.getUserId()),
                () -> assertEquals(CONGRESS_ID, result.getCongressId()),
                () -> assertEquals(CONGRESS_PRICE, result.getTotal())
        );
    }

    @Test
    void testPayWhenPaymentAlreadyExists() throws Exception {
        // Arrange
        when(userService.getById(USER_ID)).thenReturn(userEntity);
        when(congressService.getById(CONGRESS_ID)).thenReturn(congressEntity);
        when(walletService.getByUserId(USER_ID)).thenReturn(walletEntity);
        when(paymentRepository.existsByUserIdAndCongressId(USER_ID, CONGRESS_ID)).thenReturn(true);

        // Assert
        assertThrows(DuplicatedEntityException.class,
                () -> inscriptionService.pay(payRequest));
    }

    @Test
    void testPayWhenInsufficientFunds() throws Exception {
        // Arrange
        walletEntity.setValue(INSUFFICIENT_WALLET_VALUE);

        when(userService.getById(USER_ID)).thenReturn(userEntity);
        when(congressService.getById(CONGRESS_ID)).thenReturn(congressEntity);
        when(walletService.getByUserId(USER_ID)).thenReturn(walletEntity);
        when(paymentRepository.existsByUserIdAndCongressId(USER_ID, CONGRESS_ID)).thenReturn(false);

        // Assert
        assertThrows(InsufficientFundsException.class,
                () -> inscriptionService.pay(payRequest));
    }

    @Test
    void testGetPaymentsByUserId() throws Exception {
        // Arrange
        List<PaymentEntity> payments = List.of(paymentEntity);
        List<PayResponse> responses = List.of(payResponse);

        when(userService.getById(USER_ID)).thenReturn(userEntity);
        when(paymentRepository.findAllByUserId(USER_ID)).thenReturn(payments);
        when(inscriptionMapper.toPayResponseList(payments)).thenReturn(responses);

        // Act
        List<PayResponse> result = inscriptionService.getPaymentsByUserId(USER_ID);

        // Assert
        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(PAYMENT_ID, result.get(0).getId()),
                () -> assertEquals(USER_ID, result.get(0).getUserId())
        );
    }

    @Test
    void testGetInscriptionsByUserId() throws Exception {
        // Arrange
        List<InscriptionEntity> inscriptions = List.of(inscriptionEntity);
        List<InscriptionResponse> responses = List.of(inscriptionResponse);

        when(userService.getById(USER_ID)).thenReturn(userEntity);
        when(inscriptionRepository.findAllByUserId(USER_ID)).thenReturn(inscriptions);
        when(inscriptionMapper.toInscriptionResponseList(inscriptions)).thenReturn(responses);

        // Act
        List<InscriptionResponse> result = inscriptionService.getInscriptionsByUserId(USER_ID);

        // Assert
        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(CONGRESS_ID, result.get(0).getCongressId()),
                () -> assertEquals(USER_ID, result.get(0).getUserId())
        );
    }

    @Test
    void testGetInscriptionsByCongressId() throws Exception {
        // Arrange
        List<InscriptionEntity> inscriptions = List.of(inscriptionEntity);
        List<InscriptionResponse> responses = List.of(inscriptionResponse);

        when(congressService.getById(CONGRESS_ID)).thenReturn(congressEntity);
        when(inscriptionRepository.findAllByCongressId(CONGRESS_ID)).thenReturn(inscriptions);
        when(inscriptionMapper.toInscriptionResponseList(inscriptions)).thenReturn(responses);

        // Act
        List<InscriptionResponse> result = inscriptionService.getInscriptionsByCongressId(CONGRESS_ID);

        // Assert
        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(CONGRESS_ID, result.get(0).getCongressId()),
                () -> assertEquals(USER_ID, result.get(0).getUserId())
        );
    }

    @Test
    void testGetPayById() throws Exception {
        // Arrange
        when(paymentRepository.findById(PAYMENT_ID)).thenReturn(Optional.of(paymentEntity));

        // Act
        PaymentEntity result = inscriptionService.getPayById(PAYMENT_ID);

        // Assert
        assertAll(
                () -> assertEquals(PAYMENT_ID, result.getId()),
                () -> assertEquals(CONGRESS_PRICE, result.getTotal())
        );
    }

    @Test
    void testGetPayByIdWhenNotFound() {
        // Arrange
        when(paymentRepository.findById(PAYMENT_ID)).thenReturn(Optional.empty());

        // Assert
        assertThrows(NotFoundException.class,
                () -> inscriptionService.getPayById(PAYMENT_ID));
    }

    @Test
    void testGetInscriptionById() throws Exception {
        // Arrange
        when(inscriptionRepository.findById(INSCRIPTION_ID)).thenReturn(Optional.of(inscriptionEntity));

        // Act
        InscriptionEntity result = inscriptionService.getInscriptionById(INSCRIPTION_ID);

        // Assert
        assertAll(
                () -> assertEquals(USER_ID, result.getUser().getId()),
                () -> assertEquals(CONGRESS_ID, result.getCongress().getId()),
                () -> assertEquals(ROL_ID, result.getAttendeeRol().getId())
        );
    }

    @Test
    void testGetInscriptionByIdWhenNotFound() {
        // Arrange
        when(inscriptionRepository.findById(INSCRIPTION_ID)).thenReturn(Optional.empty());

        // Assert
        assertThrows(NotFoundException.class,
                () -> inscriptionService.getInscriptionById(INSCRIPTION_ID));
    }

    @Test
    void testIsUserEnrolledInCongress() throws Exception {
        // Arrange
        when(userService.getById(USER_ID)).thenReturn(userEntity);
        when(congressService.getById(CONGRESS_ID)).thenReturn(congressEntity);
        when(inscriptionRepository.existsByUserIdAndCongressId(USER_ID, CONGRESS_ID)).thenReturn(true);

        // Act
        boolean result = inscriptionService.isUserEnrolledInCongress(USER_ID, CONGRESS_ID);

        // Assert
        assertTrue(result);
    }
}