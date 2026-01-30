package ifmo.se.coursach_back.report.api;
import ifmo.se.coursach_back.report.application.ReportRequestService;

import ifmo.se.coursach_back.report.api.dto.ReportRequestCreateRequest;
import ifmo.se.coursach_back.report.api.dto.ReportRequestDetailsResponse;
import ifmo.se.coursach_back.report.api.dto.ReportRequestSummaryResponse;
import ifmo.se.coursach_back.security.AccountPrincipal;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('DOCTOR', 'LAB', 'NURSE')")
public class ReportRequestController {
    private final ReportRequestService reportRequestService;

    @PostMapping("/requests")
    public ResponseEntity<ReportRequestSummaryResponse> createRequest(
            @AuthenticationPrincipal AccountPrincipal principal,
            @Valid @RequestBody ReportRequestCreateRequest request) {
        ReportRequestSummaryResponse response = reportRequestService.createRequest(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/requests/mine")
    public List<ReportRequestSummaryResponse> listMyRequests(
            @AuthenticationPrincipal AccountPrincipal principal) {
        return reportRequestService.listMyRequests(principal.getId());
    }

    @GetMapping("/{requestId}")
    public ReportRequestDetailsResponse getReport(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable UUID requestId) {
        return reportRequestService.getReport(principal.getId(), requestId);
    }
}
