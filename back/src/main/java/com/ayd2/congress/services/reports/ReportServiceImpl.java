package com.ayd2.congress.services.reports;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

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
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.models.Activities.ActivityEntity;
import com.ayd2.congress.repositories.Attendance.AttendanceRepository;
import com.ayd2.congress.repositories.Congress.InscriptionRepository;
import com.ayd2.congress.repositories.Wallet.WalletTransactionRepository;
import com.ayd2.congress.services.Congress.CongressService;
import com.ayd2.congress.services.Organization.OrganizationService;

@Service
public class ReportServiceImpl implements ReportService {
    private final WalletTransactionRepository walletTransactionRepository;
    private final InscriptionRepository inscriptionRepository;
    private final AttendanceRepository attendanceRepository;
    private final CongressService congressService;
    private final OrganizationService organizationService;

    public ReportServiceImpl(WalletTransactionRepository walletTransactionRepository,
            InscriptionRepository inscriptionRepository, CongressService congressService,
            AttendanceRepository attendanceRepository, OrganizationService organizationService) {
        this.walletTransactionRepository = walletTransactionRepository;
        this.inscriptionRepository = inscriptionRepository;
        this.attendanceRepository = attendanceRepository;
        this.congressService = congressService;
        this.organizationService = organizationService;
    }

    @Override
    public List<EarningReport> earningsReport(EarningFilter filter) throws NotFoundException {
        if (filter.getOrganizationId() != null) 
            organizationService.getById(filter.getOrganizationId());
        return walletTransactionRepository.getEarningsReport(filter.getStartDate(), filter.getEndDate(),
                filter.getOrganizationId());
    }

    @Override
    public List<CongressResponse> congressByOrganizationId(Long id) throws NotFoundException {
        organizationService.getById(id);
        return congressService.getAllByOrganizationId(id);
    }

    @Override
    public List<InscriptionReport> inscriptionReport(InscriptionFilter filter) throws NotFoundException {
        congressService.getById(filter.getCongressId());
        return inscriptionRepository.getInscriptionReport(
                filter.getCongressId(),
                filter.getAtteendeType());
    }

    @Override
    public List<AtteendanceReport> atteendanceReport(AtteendanceReporRequest filter) throws NotFoundException {
        return attendanceRepository.getAttendanceReport(
                filter.getActivityId(),
                filter.getRoomId(),
                filter.getStartDate(),
                filter.getEndDate());
    }

    @Override
    public List<WorkshopReport> atteendanceWorkshop(WorkshopReportFilter filter) throws NotFoundException {
        congressService.getById(filter.getCongressId());
        List<ActivityEntity> workshops = attendanceRepository.getWorkshops(
                filter.getCongressId(), filter.getActivityId());

        return workshops.stream().map(a -> {
            List<WorkshopParticipant> participants = attendanceRepository.getWorkshopParticipants(a.getId());
            long total = participants.size();
            long available = a.getCapacity() - total;
            return new WorkshopReport(a.getName(), a.getCapacity(), total, available, participants);
        }).collect(Collectors.toList());
    }

    @Override
    public List<EarningCongressReport> earningsCongressReport(EarningCongressFilter filter) throws NotFoundException {
        List<EarningCongressRaw> raw = walletTransactionRepository.getEarningsCongressReport(
                filter.getCongressId(),
                filter.getStartDate(),
                filter.getEndDate());

        List<EarningCongressReport> result = new ArrayList<>();
        for (EarningCongressRaw r : raw) {
            CongressResponse congressResponse = congressService.getByIdResponse(r.getCongressId());
            result.add(new EarningCongressReport(congressResponse, r.getTotal(), r.getCommission(), r.getEarning()));
        }
        return result;
    }

}
