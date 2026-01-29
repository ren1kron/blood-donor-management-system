package ifmo.se.coursach_back.report;

import ifmo.se.coursach_back.report.dto.ReportRequestActionRequest;
import ifmo.se.coursach_back.report.dto.ReportRequestDetailsResponse;
import ifmo.se.coursach_back.report.dto.ReportRequestSummaryResponse;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/reports/requests")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminReportRequestController {
    private final ReportRequestService reportRequestService;

    @GetMapping
    public List<ReportRequestSummaryResponse> listRequests(
            @RequestParam(value = "status", required = false) String status) {
        return reportRequestService.listRequestsForAdmin(status);
    }

    @PostMapping("/{requestId}/take")
    public ResponseEntity<ReportRequestSummaryResponse> takeRequest(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable UUID requestId) {
        ReportRequestSummaryResponse response = reportRequestService.takeRequest(principal.getId(), requestId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{requestId}/generate")
    public ResponseEntity<ReportRequestDetailsResponse> generate(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable UUID requestId) {
        ReportRequestDetailsResponse response = reportRequestService.generateReport(principal.getId(), requestId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{requestId}/send")
    public ResponseEntity<ReportRequestSummaryResponse> send(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable UUID requestId) {
        ReportRequestSummaryResponse response = reportRequestService.sendReport(principal.getId(), requestId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{requestId}/reject")
    public ResponseEntity<ReportRequestSummaryResponse> reject(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable UUID requestId,
            @Valid @RequestBody(required = false) ReportRequestActionRequest request) {
        ReportRequestSummaryResponse response = reportRequestService.rejectReport(principal.getId(), requestId, request);
        return ResponseEntity.ok(response);
    }
}
