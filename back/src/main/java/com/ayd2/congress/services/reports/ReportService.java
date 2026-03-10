package com.ayd2.congress.services.reports;

import java.util.List;

import com.ayd2.congress.dtos.Congress.CongressResponse;
import com.ayd2.congress.dtos.reports.AtteendanceReporRequest;
import com.ayd2.congress.dtos.reports.AtteendanceReport;
import com.ayd2.congress.dtos.reports.EarningCongressReport;
import com.ayd2.congress.dtos.reports.EarningFilter;
import com.ayd2.congress.dtos.reports.EarningReport;
import com.ayd2.congress.dtos.reports.EarningCongressFilter;
import com.ayd2.congress.dtos.reports.InscriptionFilter;
import com.ayd2.congress.dtos.reports.InscriptionReport;
import com.ayd2.congress.dtos.reports.WorkshopReport;
import com.ayd2.congress.dtos.reports.WorkshopReportFilter;
import com.ayd2.congress.exceptions.NotFoundException;

public interface ReportService {
    //admin reports
   List<EarningReport> earningsReport(EarningFilter filter) throws NotFoundException;
    List<CongressResponse> congressByOrganizationId(Long id) throws NotFoundException;

    //congress admin reports
    List<InscriptionReport> inscriptionReport(InscriptionFilter filter) throws NotFoundException;
    List<AtteendanceReport> atteendanceReport(AtteendanceReporRequest reportRequest) throws NotFoundException;
    List<WorkshopReport> atteendanceWorkshop(WorkshopReportFilter filter) throws NotFoundException;
    List<EarningCongressReport> earningsCongressReport(EarningCongressFilter filter) throws NotFoundException;

}
