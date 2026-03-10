package com.ayd2.congress.services.certificate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ayd2.congress.dtos.certificate.CertificateResponse;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.mappers.CertificateMapper;
import com.ayd2.congress.models.Attendance.AttendanceEntity;
import com.ayd2.congress.models.Congress.CertificateEntity;
import com.ayd2.congress.models.Congress.InscriptionEntity;
import com.ayd2.congress.models.Enums.AttendanceType;
import com.ayd2.congress.repositories.Congress.CertificateRepository;
import com.ayd2.congress.repositories.Congress.InscriptionRepository;
import com.ayd2.congress.services.User.UserService;
import com.ayd2.congress.services.attendance.AttendanceService;

@Service
public class CertificateServiceImpl implements CertificateService {

    private static final Long ROL_ASISTENTE = 1L;
    private static final Long ROL_INVITADO = 2L;
    private static final Long ROL_PONENTE = 3L;
    private static final Long ROL_TALLERISTA = 4L;
    private static final int MIN_ATTENDANCES_FOR_CERTIFICATE = 3;

    private final CertificateRepository certificateRepository;
    private final UserService userService;
    private final AttendanceService attendanceService;
    private final InscriptionRepository inscriptionRepository;
    private final CertificateMapper mapper;

    public CertificateServiceImpl(
            CertificateRepository certificateRepository,
            UserService userService,
            AttendanceService attendanceService,
            InscriptionRepository inscriptionRepository,
            CertificateMapper mapper) {
        this.certificateRepository = certificateRepository;
        this.userService = userService;
        this.attendanceService = attendanceService;
        this.inscriptionRepository = inscriptionRepository;
        this.mapper = mapper;
    }

    private Optional<CertificateEntity> createCertificateIfEligible(InscriptionEntity inscription) throws NotFoundException {
        Long rolId = inscription.getAttendeeRol().getId();

        if (Objects.equals(rolId, ROL_ASISTENTE)) {
            List<AttendanceEntity> attendances =
                    attendanceService.getAttendanceByUserIdAndCongressId(
                            inscription.getUser().getId(),
                            inscription.getCongress().getId(),AttendanceType.ATTENDANCE);

            if (attendances.size() >= MIN_ATTENDANCES_FOR_CERTIFICATE) {
                return Optional.of(saveCertificate(inscription));
            }
            return Optional.empty();
        }

        if (Objects.equals(rolId, ROL_INVITADO)
                || Objects.equals(rolId, ROL_PONENTE)
                || Objects.equals(rolId, ROL_TALLERISTA)) {
            return Optional.of(saveCertificate(inscription));
        }

        return Optional.empty();
    }

    private CertificateEntity saveCertificate(InscriptionEntity inscription) {
        CertificateEntity certificateEntity = new CertificateEntity();
        certificateEntity.setCongress(inscription.getCongress());
        certificateEntity.setUser(inscription.getUser());
        certificateEntity.setRol(inscription.getAttendeeRol());
        return certificateRepository.save(certificateEntity);
    }

    @Override
    public List<CertificateResponse> getCertificatesByUserId(Long userId, Long congressId) throws NotFoundException {
        userService.getById(userId);

        List<CertificateResponse> certificates = new ArrayList<>();
        List<InscriptionEntity> inscriptions =
                inscriptionRepository.findAllByUserIdAndCongressId(userId, congressId);

        for (InscriptionEntity inscription : inscriptions) {
            Long rolId = inscription.getAttendeeRol().getId();

            Optional<CertificateEntity> optionalCertificate =
                    certificateRepository.findByUserIdAndCongressIdAndRolId(userId, congressId, rolId);

            if (optionalCertificate.isPresent()) {
                certificates.add(mapper.toResponse(optionalCertificate.get()));
            } else {
                Optional<CertificateEntity> createdCertificate = createCertificateIfEligible(inscription);
                createdCertificate.ifPresent(certificate ->
                        certificates.add(mapper.toResponse(certificate)));
            }
        }

        return certificates;
    }
}