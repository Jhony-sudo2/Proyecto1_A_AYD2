package com.ayd2.congress.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ayd2.congress.dtos.certificate.CertificateResponse;
import com.ayd2.congress.mappers.CertificateMapper;
import com.ayd2.congress.models.Attendance.AttendanceEntity;
import com.ayd2.congress.models.Congress.AttendeeRol;
import com.ayd2.congress.models.Congress.CertificateEntity;
import com.ayd2.congress.models.Congress.CongressEntity;
import com.ayd2.congress.models.Congress.InscriptionEntity;
import com.ayd2.congress.models.Enums.AttendanceType;
import com.ayd2.congress.models.User.UserEntity;
import com.ayd2.congress.repositories.Congress.CertificateRepository;
import com.ayd2.congress.repositories.Congress.InscriptionRepository;
import com.ayd2.congress.services.User.UserService;
import com.ayd2.congress.services.attendance.AttendanceService;
import com.ayd2.congress.services.certificate.CertificateServiceImpl;

@ExtendWith(MockitoExtension.class)
public class CertificateServiceImplTest {

    private static final Long USER_ID = 1L;
    private static final Long CONGRESS_ID = 2L;

    private static final Long ROL_ASISTENTE = 1L;
    private static final Long ROL_INVITADO = 2L;
    private static final Long ROL_PONENTE = 3L;
    private static final Long ROL_TALLERISTA = 4L;
    private static final Long ROL_UNKNOWN = 99L;

    @Mock
    private CertificateRepository certificateRepository;

    @Mock
    private UserService userService;

    @Mock
    private AttendanceService attendanceService;

    @Mock
    private InscriptionRepository inscriptionRepository;

    @Mock
    private CertificateMapper mapper;

    @InjectMocks
    private CertificateServiceImpl certificateService;

    private UserEntity userEntity;
    private CongressEntity congressEntity;
    private AttendeeRol rolEntity;
    private InscriptionEntity inscriptionEntity;
    private CertificateEntity certificateEntity;
    private CertificateResponse certificateResponse;

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity();
        userEntity.setId(USER_ID);
        userEntity.setName("Juan");
        userEntity.setLastName("Perez");

        congressEntity = new CongressEntity();
        congressEntity.setId(CONGRESS_ID);
        congressEntity.setName("Congress 2026");
        congressEntity.setStartDate(LocalDateTime.of(2026, 3, 10, 8, 0));
        congressEntity.setEndDate(LocalDateTime.of(2026, 3, 12, 18, 0));

        rolEntity = new AttendeeRol();
        rolEntity.setId(ROL_ASISTENTE);
        rolEntity.setName("Asistente");

        inscriptionEntity = new InscriptionEntity();
        inscriptionEntity.setUser(userEntity);
        inscriptionEntity.setCongress(congressEntity);
        inscriptionEntity.setAttendeeRol(rolEntity);

        certificateEntity = new CertificateEntity();
        certificateEntity.setUser(userEntity);
        certificateEntity.setCongress(congressEntity);
        certificateEntity.setRol(rolEntity);

        certificateResponse = new CertificateResponse(
                "Congress 2026",
                congressEntity.getStartDate(),
                congressEntity.getEndDate(),
                "Centro de Convenciones",
                "Juan",
                "Perez",
                LocalDateTime.of(2026, 3, 12, 18, 0),
                "OpenAI",
                "Asistente"
        );
    }

    @Test
    void testGetCertificatesByUserIdWhenCertificateAlreadyExists() throws Exception {
        // Arrange
        when(userService.getById(USER_ID)).thenReturn(userEntity);
        when(inscriptionRepository.findAllByUserIdAndCongressId(USER_ID, CONGRESS_ID))
                .thenReturn(List.of(inscriptionEntity));
        when(certificateRepository.findByUserIdAndCongressIdAndRolId(USER_ID, CONGRESS_ID, ROL_ASISTENTE))
                .thenReturn(Optional.of(certificateEntity));
        when(mapper.toResponse(certificateEntity)).thenReturn(certificateResponse);

        // Act
        List<CertificateResponse> result = certificateService.getCertificatesByUserId(USER_ID, CONGRESS_ID);

        // Assert
        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals("Congress 2026", result.get(0).getCongressName()),
                () -> assertEquals("Juan", result.get(0).getName())
        );

        verify(certificateRepository, never()).save(org.mockito.ArgumentMatchers.any(CertificateEntity.class));
    }

    @Test
    void testGetCertificatesByUserIdCreatesCertificateForAssistantWithEnoughAttendances() throws Exception {
        // Arrange
        rolEntity.setId(ROL_ASISTENTE);
        inscriptionEntity.setAttendeeRol(rolEntity);

        List<AttendanceEntity> attendances = List.of(
                new AttendanceEntity(),
                new AttendanceEntity(),
                new AttendanceEntity()
        );

        when(userService.getById(USER_ID)).thenReturn(userEntity);
        when(inscriptionRepository.findAllByUserIdAndCongressId(USER_ID, CONGRESS_ID))
                .thenReturn(List.of(inscriptionEntity));
        when(certificateRepository.findByUserIdAndCongressIdAndRolId(USER_ID, CONGRESS_ID, ROL_ASISTENTE))
                .thenReturn(Optional.empty());
        when(attendanceService.getAttendanceByUserIdAndCongressId(USER_ID, CONGRESS_ID, AttendanceType.ATTENDANCE))
                .thenReturn(attendances);
        when(certificateRepository.save(org.mockito.ArgumentMatchers.any(CertificateEntity.class)))
                .thenReturn(certificateEntity);
        when(mapper.toResponse(certificateEntity)).thenReturn(certificateResponse);

        // Act
        List<CertificateResponse> result = certificateService.getCertificatesByUserId(USER_ID, CONGRESS_ID);

        // Assert
        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals("Congress 2026", result.get(0).getCongressName()),
                () -> assertEquals("Juan", result.get(0).getName())
        );

        verify(certificateRepository).save(org.mockito.ArgumentMatchers.any(CertificateEntity.class));
    }

    @Test
    void testGetCertificatesByUserIdDoesNotCreateCertificateForAssistantWithoutEnoughAttendances() throws Exception {
        // Arrange
        rolEntity.setId(ROL_ASISTENTE);
        inscriptionEntity.setAttendeeRol(rolEntity);

        List<AttendanceEntity> attendances = List.of(
                new AttendanceEntity(),
                new AttendanceEntity()
        );

        when(userService.getById(USER_ID)).thenReturn(userEntity);
        when(inscriptionRepository.findAllByUserIdAndCongressId(USER_ID, CONGRESS_ID))
                .thenReturn(List.of(inscriptionEntity));
        when(certificateRepository.findByUserIdAndCongressIdAndRolId(USER_ID, CONGRESS_ID, ROL_ASISTENTE))
                .thenReturn(Optional.empty());
        when(attendanceService.getAttendanceByUserIdAndCongressId(USER_ID, CONGRESS_ID, AttendanceType.ATTENDANCE))
                .thenReturn(attendances);

        // Act
        List<CertificateResponse> result = certificateService.getCertificatesByUserId(USER_ID, CONGRESS_ID);

        // Assert
        assertEquals(0, result.size());
        verify(certificateRepository, never()).save(org.mockito.ArgumentMatchers.any(CertificateEntity.class));
    }

    @Test
    void testGetCertificatesByUserIdCreatesCertificateForInvitado() throws Exception {
        // Arrange
        rolEntity.setId(ROL_INVITADO);
        rolEntity.setName("Invitado");
        inscriptionEntity.setAttendeeRol(rolEntity);

        certificateEntity.setRol(rolEntity);

        CertificateResponse invitadoResponse = new CertificateResponse(
                "Congress 2026",
                congressEntity.getStartDate(),
                congressEntity.getEndDate(),
                "Centro de Convenciones",
                "Juan",
                "Perez",
                LocalDateTime.of(2026, 3, 12, 18, 0),
                "OpenAI",
                "Invitado"
        );

        when(userService.getById(USER_ID)).thenReturn(userEntity);
        when(inscriptionRepository.findAllByUserIdAndCongressId(USER_ID, CONGRESS_ID))
                .thenReturn(List.of(inscriptionEntity));
        when(certificateRepository.findByUserIdAndCongressIdAndRolId(USER_ID, CONGRESS_ID, ROL_INVITADO))
                .thenReturn(Optional.empty());
        when(certificateRepository.save(org.mockito.ArgumentMatchers.any(CertificateEntity.class)))
                .thenReturn(certificateEntity);
        when(mapper.toResponse(certificateEntity)).thenReturn(invitadoResponse);

        // Act
        List<CertificateResponse> result = certificateService.getCertificatesByUserId(USER_ID, CONGRESS_ID);

        // Assert
        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals("Invitado", result.get(0).getAssitantType())
        );
    }

    @Test
    void testGetCertificatesByUserIdCreatesCertificateForPonente() throws Exception {
        // Arrange
        rolEntity.setId(ROL_PONENTE);
        inscriptionEntity.setAttendeeRol(rolEntity);

        when(userService.getById(USER_ID)).thenReturn(userEntity);
        when(inscriptionRepository.findAllByUserIdAndCongressId(USER_ID, CONGRESS_ID))
                .thenReturn(List.of(inscriptionEntity));
        when(certificateRepository.findByUserIdAndCongressIdAndRolId(USER_ID, CONGRESS_ID, ROL_PONENTE))
                .thenReturn(Optional.empty());
        when(certificateRepository.save(org.mockito.ArgumentMatchers.any(CertificateEntity.class)))
                .thenReturn(certificateEntity);
        when(mapper.toResponse(certificateEntity)).thenReturn(certificateResponse);

        // Act
        List<CertificateResponse> result = certificateService.getCertificatesByUserId(USER_ID, CONGRESS_ID);

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    void testGetCertificatesByUserIdCreatesCertificateForTallerista() throws Exception {
        // Arrange
        rolEntity.setId(ROL_TALLERISTA);
        inscriptionEntity.setAttendeeRol(rolEntity);

        when(userService.getById(USER_ID)).thenReturn(userEntity);
        when(inscriptionRepository.findAllByUserIdAndCongressId(USER_ID, CONGRESS_ID))
                .thenReturn(List.of(inscriptionEntity));
        when(certificateRepository.findByUserIdAndCongressIdAndRolId(USER_ID, CONGRESS_ID, ROL_TALLERISTA))
                .thenReturn(Optional.empty());
        when(certificateRepository.save(org.mockito.ArgumentMatchers.any(CertificateEntity.class)))
                .thenReturn(certificateEntity);
        when(mapper.toResponse(certificateEntity)).thenReturn(certificateResponse);

        // Act
        List<CertificateResponse> result = certificateService.getCertificatesByUserId(USER_ID, CONGRESS_ID);

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    void testGetCertificatesByUserIdDoesNotCreateCertificateForUnknownRole() throws Exception {
        // Arrange
        rolEntity.setId(ROL_UNKNOWN);
        inscriptionEntity.setAttendeeRol(rolEntity);

        when(userService.getById(USER_ID)).thenReturn(userEntity);
        when(inscriptionRepository.findAllByUserIdAndCongressId(USER_ID, CONGRESS_ID))
                .thenReturn(List.of(inscriptionEntity));
        when(certificateRepository.findByUserIdAndCongressIdAndRolId(USER_ID, CONGRESS_ID, ROL_UNKNOWN))
                .thenReturn(Optional.empty());

        // Act
        List<CertificateResponse> result = certificateService.getCertificatesByUserId(USER_ID, CONGRESS_ID);

        // Assert
        assertEquals(0, result.size());
        verify(certificateRepository, never()).save(org.mockito.ArgumentMatchers.any(CertificateEntity.class));
    }

 
}
