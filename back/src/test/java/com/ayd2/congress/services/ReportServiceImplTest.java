package com.ayd2.congress.services;


import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ayd2.congress.dtos.Congress.CongressResponse;
import com.ayd2.congress.dtos.reports.AtteendanceReporRequest;
import com.ayd2.congress.dtos.reports.AtteendanceReport;
import com.ayd2.congress.dtos.reports.EarningCongressFilter;
import com.ayd2.congress.dtos.reports.EarningCongressRaw;
import com.ayd2.congress.dtos.reports.EarningCongressReport;
import com.ayd2.congress.dtos.reports.EarningFilter;
import com.ayd2.congress.dtos.reports.EarningReport;
import com.ayd2.congress.dtos.reports.InscriptionFilter;
import com.ayd2.congress.dtos.reports.InscriptionReport;
import com.ayd2.congress.dtos.reports.WorkshopParticipant;
import com.ayd2.congress.dtos.reports.WorkshopReport;
import com.ayd2.congress.dtos.reports.WorkshopReportFilter;
import com.ayd2.congress.models.Activities.ActivityEntity;
import com.ayd2.congress.repositories.Attendance.AttendanceRepository;
import com.ayd2.congress.repositories.Congress.InscriptionRepository;
import com.ayd2.congress.repositories.Wallet.WalletTransactionRepository;
import com.ayd2.congress.services.Congress.CongressService;
import com.ayd2.congress.services.Organization.OrganizationService;
import com.ayd2.congress.services.reports.ReportServiceImpl;

@ExtendWith(MockitoExtension.class)
public class ReportServiceImplTest {

    private static final Long ORGANIZATION_ID = 1L;
    private static final Long CONGRESS_ID = 2L;
    private static final Long ACTIVITY_ID = 3L;
    private static final Long ROOM_ID = 4L;

    @Mock
    private WalletTransactionRepository walletTransactionRepository;
    @Mock
    private InscriptionRepository inscriptionRepository;
    @Mock
    private AttendanceRepository attendanceRepository;
    @Mock
    private CongressService congressService;
    @Mock
    private OrganizationService organizationService;

    @InjectMocks
    private ReportServiceImpl reportService;

    private EarningFilter earningFilter;
    private CongressResponse congressResponse;

    @BeforeEach
    void setUp() {
        earningFilter = new EarningFilter(
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2026, 3, 31),
                ORGANIZATION_ID);

        congressResponse = new CongressResponse(
                CONGRESS_ID,
                "Congress 2026",
                "Description",
                100.0,
                "image",
                LocalDate.of(2026, 3, 10),
                LocalDate.of(2026, 3, 12),
                LocalDate.of(2026, 3, 1),
                "OpenAI",
                "Centro",
                ROOM_ID);
    }

    @Test
    void testEarningsReportWithOrganizationId() throws Exception {
        // Arrange
        List<EarningReport> expected = List.of(
                new EarningReport(
                        "Congress 2026",
                        LocalDateTime.of(2026, 3, 12, 18, 0),
                        LocalDateTime.of(2026, 3, 10, 8, 0),
                        "Centro",
                        "OpenAI",
                        1500.0,
                        1200.0));

        when(walletTransactionRepository.getEarningsReport(
                earningFilter.getStartDate(),
                earningFilter.getEndDate(),
                earningFilter.getOrganizationId()))
                .thenReturn(expected);

        // Act
        List<EarningReport> result = reportService.earningsReport(earningFilter);

        // Assert
        assertAll(
                () -> verify(organizationService).getById(ORGANIZATION_ID),
                () -> assertEquals(1, result.size()),
                () -> assertEquals("Congress 2026", result.get(0).getCongressName()),
                () -> assertEquals(1500.0, result.get(0).getTotalCollected()));
    }

    @Test
    void testEarningsReportWithoutOrganizationId() throws Exception {
        // Arrange
        EarningFilter filter = new EarningFilter(
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2026, 3, 31),
                null);

        List<EarningReport> expected = List.of(
                new EarningReport(
                        "Congress 2026",
                        LocalDateTime.of(2026, 3, 12, 18, 0),
                        LocalDateTime.of(2026, 3, 10, 8, 0),
                        "Centro",
                        "OpenAI",
                        1500.0,
                        1200.0));

        when(walletTransactionRepository.getEarningsReport(
                filter.getStartDate(),
                filter.getEndDate(),
                null))
                .thenReturn(expected);

        // Act
        List<EarningReport> result = reportService.earningsReport(filter);

        // Assert
        assertAll(
                () -> verify(organizationService, never()).getById(ORGANIZATION_ID),
                () -> assertEquals(1, result.size()),
                () -> assertEquals("Congress 2026", result.get(0).getCongressName()));
    }

    @Test
    void testCongressByOrganizationId() throws Exception {
        // Arrange
        List<CongressResponse> expected = List.of(congressResponse);

        when(congressService.getAllByOrganizationId(ORGANIZATION_ID)).thenReturn(expected);

        // Act
        List<CongressResponse> result = reportService.congressByOrganizationId(ORGANIZATION_ID);

        // Assert
        assertAll(
                () -> verify(organizationService).getById(ORGANIZATION_ID),
                () -> verify(congressService).getAllByOrganizationId(ORGANIZATION_ID),
                () -> assertEquals(1, result.size()),
                () -> assertEquals(CONGRESS_ID, result.get(0).getId()));
    }

    @Test
    void testInscriptionReport() throws Exception {
        // Arrange
        InscriptionFilter filter = new InscriptionFilter(CONGRESS_ID, 1L);

        List<InscriptionReport> expected = List.of(
                new InscriptionReport(
                        "1234567890101",
                        "Juan Perez",
                        "OpenAI",
                        "juan@mail.com",
                        "55555555",
                        "Asistente"));

        when(inscriptionRepository.getInscriptionReport(CONGRESS_ID, 1L)).thenReturn(expected);

        // Act
        List<InscriptionReport> result = reportService.inscriptionReport(filter);

        // Assert
        assertAll(
                () -> verify(congressService).getById(CONGRESS_ID),
                () -> assertEquals(1, result.size()),
                () -> assertEquals("Juan Perez", result.get(0).getName()));
    }

    @Test
    void testAtteendanceReport() throws Exception {
        // Arrange
        AtteendanceReporRequest filter = new AtteendanceReporRequest(
                ACTIVITY_ID,
                ROOM_ID,
                LocalDateTime.of(2026, 3, 10, 8, 0),
                LocalDateTime.of(2026, 3, 10, 18, 0));

        List<AtteendanceReport> expected = List.of(
                new AtteendanceReport(
                        "Workshop Spring",
                        "Room A",
                        LocalDateTime.of(2026, 3, 10, 10, 0),
                        25L));

        when(attendanceRepository.getAttendanceReport(
                filter.getActivityId(),
                filter.getRoomId(),
                filter.getStartDate(),
                filter.getEndDate()))
                .thenReturn(expected);

        // Act
        List<AtteendanceReport> result = reportService.atteendanceReport(filter);

        // Assert
        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals("Workshop Spring", result.get(0).getActivityName()),
                () -> assertEquals(25L, result.get(0).getAtteendances()));
    }

    @Test
    void testAtteendanceWorkshop() throws Exception {
        // Arrange
        WorkshopReportFilter filter = new WorkshopReportFilter(CONGRESS_ID, ACTIVITY_ID);

        ActivityEntity workshop = new ActivityEntity();
        workshop.setId(ACTIVITY_ID);
        workshop.setName("Workshop Spring");
        workshop.setCapacity(10);

        WorkshopParticipant participant1 = mock(WorkshopParticipant.class);
        WorkshopParticipant participant2 = mock(WorkshopParticipant.class);

        when(attendanceRepository.getWorkshops(CONGRESS_ID, ACTIVITY_ID)).thenReturn(List.of(workshop));
        when(attendanceRepository.getWorkshopParticipants(ACTIVITY_ID)).thenReturn(List.of(participant1, participant2));

        // Act
        List<WorkshopReport> result = reportService.atteendanceWorkshop(filter);

        // Assert
        assertAll(
                () -> verify(congressService).getById(CONGRESS_ID),
                () -> assertEquals(1, result.size()),
                () -> assertEquals("Workshop Spring", result.get(0).getWorkshopName()),
                () -> assertEquals(10L, result.get(0).getCapacity()),
                () -> assertEquals(2L, result.get(0).getTotal()),
                () -> assertEquals(8L, result.get(0).getAvailable()),
                () -> assertEquals(2, result.get(0).getParticipants().size()));
    }

    @Test
    void testAtteendanceWorkshopWithMultipleWorkshops() throws Exception {
        // Arrange
        WorkshopReportFilter filter = new WorkshopReportFilter(CONGRESS_ID, null);

        ActivityEntity workshop1 = new ActivityEntity();
        workshop1.setId(10L);
        workshop1.setName("Workshop 1");
        workshop1.setCapacity(5);

        ActivityEntity workshop2 = new ActivityEntity();
        workshop2.setId(20L);
        workshop2.setName("Workshop 2");
        workshop2.setCapacity(3);

        WorkshopParticipant participant = mock(WorkshopParticipant.class);

        when(attendanceRepository.getWorkshops(CONGRESS_ID, null)).thenReturn(List.of(workshop1, workshop2));
        when(attendanceRepository.getWorkshopParticipants(10L)).thenReturn(List.of(participant));
        when(attendanceRepository.getWorkshopParticipants(20L)).thenReturn(List.of());

        // Act
        List<WorkshopReport> result = reportService.atteendanceWorkshop(filter);

        // Assert
        assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertEquals("Workshop 1", result.get(0).getWorkshopName()),
                () -> assertEquals(4L, result.get(0).getAvailable()),
                () -> assertEquals("Workshop 2", result.get(1).getWorkshopName()),
                () -> assertEquals(3L, result.get(1).getAvailable()));
    }

    @Test
    void testEarningsCongressReport() throws Exception {
        // Arrange
        EarningCongressFilter filter = new EarningCongressFilter(
                CONGRESS_ID,
                LocalDateTime.of(2026, 3, 1, 0, 0),
                LocalDateTime.of(2026, 3, 31, 23, 59));

        EarningCongressRaw raw = mock(EarningCongressRaw.class);
        when(raw.getCongressId()).thenReturn(CONGRESS_ID);
        when(raw.getTotal()).thenReturn(2000.0);
        when(raw.getCommission()).thenReturn(300.0);
        when(raw.getEarning()).thenReturn(1700.0);

        when(walletTransactionRepository.getEarningsCongressReport(
                filter.getCongressId(),
                filter.getStartDate(),
                filter.getEndDate()))
                .thenReturn(List.of(raw));

        when(congressService.getByIdResponse(CONGRESS_ID)).thenReturn(congressResponse);

        // Act
        List<EarningCongressReport> result = reportService.earningsCongressReport(filter);

        // Assert
        assertAll(
                () -> verify(congressService).getByIdResponse(CONGRESS_ID),
                () -> assertEquals(1, result.size()),
                () -> assertEquals(CONGRESS_ID, result.get(0).getCongress().getId()),
                () -> assertEquals(2000.0, result.get(0).getTotal()),
                () -> assertEquals(300.0, result.get(0).getCommission()),
                () -> assertEquals(1700.0, result.get(0).getEarning()));
    }

    @Test
    void testEarningsCongressReportWithMultipleRows() throws Exception {
        // Arrange
        EarningCongressFilter filter = new EarningCongressFilter(
                null,
                LocalDateTime.of(2026, 3, 1, 0, 0),
                LocalDateTime.of(2026, 3, 31, 23, 59));

        CongressResponse congressResponse2 = new CongressResponse(
                99L,
                "Congress 2",
                "Description 2",
                150.0,
                "image2",
                LocalDate.of(2026, 4, 1),
                LocalDate.of(2026, 4, 3),
                LocalDate.of(2026, 3, 20),
                "Org 2",
                "Location 2",
                9L);

        EarningCongressRaw raw1 = mock(EarningCongressRaw.class);
        when(raw1.getCongressId()).thenReturn(CONGRESS_ID);
        when(raw1.getTotal()).thenReturn(1000.0);
        when(raw1.getCommission()).thenReturn(100.0);
        when(raw1.getEarning()).thenReturn(900.0);

        EarningCongressRaw raw2 = mock(EarningCongressRaw.class);
        when(raw2.getCongressId()).thenReturn(99L);
        when(raw2.getTotal()).thenReturn(500.0);
        when(raw2.getCommission()).thenReturn(50.0);
        when(raw2.getEarning()).thenReturn(450.0);

        when(walletTransactionRepository.getEarningsCongressReport(
                filter.getCongressId(),
                filter.getStartDate(),
                filter.getEndDate()))
                .thenReturn(List.of(raw1, raw2));

        when(congressService.getByIdResponse(CONGRESS_ID)).thenReturn(congressResponse);
        when(congressService.getByIdResponse(99L)).thenReturn(congressResponse2);

        // Act
        List<EarningCongressReport> result = reportService.earningsCongressReport(filter);

        // Assert
        assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertEquals("Congress 2026", result.get(0).getCongress().getName()),
                () -> assertEquals("Congress 2", result.get(1).getCongress().getName()));
    }
}