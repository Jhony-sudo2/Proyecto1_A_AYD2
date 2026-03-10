package com.ayd2.congress.services.Inscription;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.ayd2.congress.services.User.UserService;
import com.ayd2.congress.services.Wallet.WalletService;


@Service
public class InscriptionServiceImpl implements InscriptionService{
    private final UserService userService;
    private final PaymentRepository paymentRepository;
    private final InscriptionRepository inscriptionRepository;
    private final AttendeeRolRepository attendeeRolRepository;
    private final CongressService congressService;
    private final WalletService walletService;
    private final InscriptionMapper inscriptionMapper;
    private final Long ASSISTENT_ROL = 1L;

    public InscriptionServiceImpl(UserService userService, PaymentRepository paymentRepository,
            InscriptionRepository inscriptionRepository, AttendeeRolRepository attendeeRolRepository,CongressService congressService
        , WalletService walletService,InscriptionMapper inscriptionMapper) {
        this.userService = userService;
        this.paymentRepository = paymentRepository;
        this.inscriptionRepository = inscriptionRepository;
        this.attendeeRolRepository = attendeeRolRepository;
        this.congressService = congressService;
        this.walletService = walletService;
        this.inscriptionMapper = inscriptionMapper;
    }

    @Override
    public void enroll(Long userId,Long rolId,Long congressId,boolean ignoreIfExists) throws NotFoundException, DuplicatedEntityException {
        CongressEntity congressEntity = congressService.getById(congressId);
        AttendeeRol rol = getRolById(rolId);
        UserEntity userEntity = userService.getById(userId);
        boolean exists = inscriptionRepository.existsByUserIdAndCongressIdAndAttendeeRolId(userId, congressId, rolId);
        if (exists) {
            if (ignoreIfExists) {
                return;
            }
            throw new DuplicatedEntityException("The User has already registered");
        }
        InscriptionEntity newInscription = new InscriptionEntity();

        AttendeeId attendeeId = new AttendeeId(congressId, userId,rolId);
        newInscription.setId(attendeeId);
        newInscription.setCongress(congressEntity);
        newInscription.setAttendeeRol(rol);
        newInscription.setUser(userEntity);
        inscriptionRepository.save(newInscription);
    }



    public AttendeeRol getRolById(Long rolId) throws NotFoundException{
        return attendeeRolRepository.findById(rolId)
            .orElseThrow(()-> new NotFoundException("ROL NOT FOUND"));
    }
    
    @Transactional(rollbackFor  = Exception.class)
    @Override
    public PayResponse pay(PayRequest payRequest) throws NotFoundException, InsufficientFundsException, DuplicatedEntityException {
        UserEntity userEntity = userService.getById(payRequest.getUserId());
        CongressEntity congressEntity = congressService.getById(payRequest.getCongressId());
        WalletEntity walletEntity = walletService.getByUserId(userEntity.getId());
        boolean exists = paymentRepository.existsByUserIdAndCongressId(userEntity.getId(), congressEntity.getId());

        if (exists) {
            throw new DuplicatedEntityException("The user has already made the payment");
        }
        if (walletEntity.getValue() < congressEntity.getPrice()) {
            throw new InsufficientFundsException("Wallet hast not funds");
        }
        
        PaymentEntity newPayment = new PaymentEntity();
        newPayment.setCongress(congressEntity);
        newPayment.setUser(userEntity);
        newPayment.setDate(payRequest.getDate());
        newPayment.setTotal(congressEntity.getPrice());
        enroll(userEntity.getId(), ASSISTENT_ROL, congressEntity.getId(),false);
        paymentRepository.save(newPayment);
        walletService.createTransaction(userEntity.getId(),congressEntity.getPrice(),newPayment);

        return inscriptionMapper.toPayResponse(newPayment);
    }

    @Override
    public List<PayResponse> getPaymentsByUserId(Long userId) throws NotFoundException {
        userService.getById(userId);
        List<PaymentEntity> list = paymentRepository.findAllByUserId(userId);
        return inscriptionMapper.toPayResponseList(list);

    }

    @Override
    public List<InscriptionResponse> getInscriptionsByUserId(Long userId) throws NotFoundException {
        userService.getById(userId);
        List<InscriptionEntity> entities = inscriptionRepository.findAllByUserId(userId);
        return inscriptionMapper.toInscriptionResponseList(entities);
    }

    @Override
    public List<InscriptionResponse> getInscriptionsByCongressId(Long congressId) throws NotFoundException {
        congressService.getById(congressId);
        List<InscriptionEntity> entities = inscriptionRepository.findAllByCongressId(congressId);
        return inscriptionMapper.toInscriptionResponseList(entities);
    }

    @Override
    public PaymentEntity getPayById(Long id) throws NotFoundException {
        return paymentRepository.findById(id)
            .orElseThrow(()-> new NotFoundException("PAYMENT NOT FOUND"));
    }
    
    @Override
    public InscriptionEntity getInscriptionById(Long id) throws NotFoundException {
        return inscriptionRepository.findById(id)
            .orElseThrow(()-> new NotFoundException("INSCRIPTION NOT FOUND"));
    }

    @Override
    public boolean isUserEnrolledInCongress(Long userId, Long congressId) throws NotFoundException {
        UserEntity userEntity = userService.getById(userId);
        CongressEntity congressEntity = congressService.getById(congressId);
        return inscriptionRepository.existsByUserIdAndCongressId(userEntity.getId(), congressEntity.getId());
    }

    
}
