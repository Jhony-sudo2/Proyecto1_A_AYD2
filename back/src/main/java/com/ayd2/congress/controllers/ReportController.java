package com.ayd2.congress.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ayd2.congress.dtos.Congress.CongressResponse;
import com.ayd2.congress.dtos.reports.AtteendanceReporRequest;
import com.ayd2.congress.dtos.reports.AtteendanceReport;
import com.ayd2.congress.dtos.reports.EarningCongressFilter;
import com.ayd2.congress.dtos.reports.EarningCongressReport;
import com.ayd2.congress.dtos.reports.EarningFilter;
import com.ayd2.congress.dtos.reports.EarningReport;
import com.ayd2.congress.dtos.reports.InscriptionFilter;
import com.ayd2.congress.dtos.reports.InscriptionReport;
import com.ayd2.congress.dtos.reports.WorkshopReport;
import com.ayd2.congress.dtos.reports.WorkshopReportFilter;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.services.reports.ReportService;


@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService service;

    @Autowired
    public ReportController(ReportService service) {
        this.service = service;
    }

    @GetMapping("/earnings")
    public ResponseEntity<List<EarningReport>> getEarningsReport(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(required = false) Long organizationId) throws NotFoundException {

        EarningFilter filter = new EarningFilter(startDate, endDate, organizationId);
        List<EarningReport> report = service.earningsReport(filter);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/organizations/{id}/congress")
    public ResponseEntity<List<CongressResponse>> getCongressByOrganization(@PathVariable Long id) throws NotFoundException {
        List<CongressResponse> congresses = service.congressByOrganizationId(id);
        return ResponseEntity.ok(congresses);

    }

    @PostMapping("/participants")
    public ResponseEntity<List<InscriptionReport>> getParticipantsReport(@RequestBody InscriptionFilter filter)
            throws NotFoundException {

        List<InscriptionReport> report = service.inscriptionReport(filter);

        return ResponseEntity.ok(report);
    }

    @PostMapping("/attendance")
    public ResponseEntity<List<AtteendanceReport>> getAttendanceReport(
            @RequestBody AtteendanceReporRequest request) throws NotFoundException {

        List<AtteendanceReport> report = service.atteendanceReport(request);
        return ResponseEntity.ok(report);
    }

    @PostMapping("/workshops")
    public ResponseEntity<List<WorkshopReport>> getWorkshopReservations(
            @RequestBody WorkshopReportFilter filter) throws NotFoundException {

        List<WorkshopReport> report = service.atteendanceWorkshop(filter);
        return ResponseEntity.ok(report);
    }

    @PostMapping("/congress/earnings")
    public ResponseEntity<List<EarningCongressReport>> getCongressEarnings(
            @RequestBody EarningCongressFilter filter) throws NotFoundException {

        List<EarningCongressReport> report = service.earningsCongressReport(filter);
        return ResponseEntity.ok(report);
    }
}
