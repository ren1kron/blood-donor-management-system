package ifmo.se.coursach_back.report.api;

import ifmo.se.coursach_back.report.api.dto.ReportRequestActionRequest;
import ifmo.se.coursach_back.report.api.dto.ReportRequestDetailsResponse;
import ifmo.se.coursach_back.report.api.dto.ReportRequestSummaryResponse;
import ifmo.se.coursach_back.report.application.command.ProcessReportRequestCommand;
import ifmo.se.coursach_back.report.application.command.RejectReportRequestCommand;
import ifmo.se.coursach_back.report.application.command.TakeReportRequestCommand;
import ifmo.se.coursach_back.report.application.result.ReportRequestDetailsResult;
import ifmo.se.coursach_back.report.application.result.ReportRequestSummaryResult;
import ifmo.se.coursach_back.report.application.usecase.ListAllReportRequestsUseCase;
import ifmo.se.coursach_back.report.application.usecase.ProcessReportRequestUseCase;
import ifmo.se.coursach_back.report.application.usecase.RejectReportRequestUseCase;
import ifmo.se.coursach_back.report.application.usecase.TakeReportRequestUseCase;
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
    private final ListAllReportRequestsUseCase listAllReportRequestsUseCase;
    private final TakeReportRequestUseCase takeReportRequestUseCase;
    private final ProcessReportRequestUseCase processReportRequestUseCase;
    private final RejectReportRequestUseCase rejectReportRequestUseCase;

    @GetMapping
    public List<ReportRequestSummaryResponse> listRequests(
            @RequestParam(value = "status", required = false) String status) {
        List<ReportRequestSummaryResult> results = listAllReportRequestsUseCase.execute(status);
        return results.stream().map(this::mapToSummaryResponse).toList();
    }

    @PostMapping("/{requestId}/take")
    public ResponseEntity<ReportRequestSummaryResponse> takeRequest(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable UUID requestId) {
        TakeReportRequestCommand command = new TakeReportRequestCommand(principal.getId(), requestId);
        ReportRequestSummaryResult result = takeReportRequestUseCase.execute(command);
        ReportRequestSummaryResponse response = mapToSummaryResponse(result);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{requestId}/generate")
    public ResponseEntity<ReportRequestDetailsResponse> generate(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable UUID requestId) {
        ProcessReportRequestCommand command = new ProcessReportRequestCommand(
                principal.getId(), requestId, null
        );
        ReportRequestDetailsResult result = processReportRequestUseCase.execute(command);
        ReportRequestDetailsResponse response = new ReportRequestDetailsResponse(
                result.requestId(), result.donorId(), result.donorName(),
                result.reportType(), result.status(), result.requestedByName(),
                result.requestedByRole(), result.assignedAdminName(),
                result.createdAt(), result.updatedAt(), result.generatedAt(),
                result.message(), result.payload()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{requestId}/send")
    public ResponseEntity<ReportRequestSummaryResponse> send(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable UUID requestId) {
        // Send uses the same process command but marks as sent
        ProcessReportRequestCommand command = new ProcessReportRequestCommand(
                principal.getId(), requestId, null
        );
        ReportRequestDetailsResult result = processReportRequestUseCase.execute(command);
        ReportRequestSummaryResponse response = mapToSummaryResponse(new ReportRequestSummaryResult(
                result.requestId(), result.donorId(), result.donorName(),
                result.reportType(), result.status(), result.requestedByName(),
                result.requestedByRole(), result.assignedAdminName(),
                result.createdAt(), result.updatedAt(), result.generatedAt(),
                result.message()
        ));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{requestId}/reject")
    public ResponseEntity<ReportRequestSummaryResponse> reject(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable UUID requestId,
            @Valid @RequestBody(required = false) ReportRequestActionRequest request) {
        RejectReportRequestCommand command = new RejectReportRequestCommand(
                principal.getId(), requestId,
                request != null ? request.message() : null
        );
        ReportRequestSummaryResult result = rejectReportRequestUseCase.execute(command);
        ReportRequestSummaryResponse response = mapToSummaryResponse(result);
        return ResponseEntity.ok(response);
    }

    private ReportRequestSummaryResponse mapToSummaryResponse(ReportRequestSummaryResult result) {
        return new ReportRequestSummaryResponse(
                result.requestId(), result.donorId(), result.donorName(),
                result.reportType(), result.status(), result.requestedByName(),
                result.requestedByRole(), result.assignedAdminName(),
                result.createdAt(), result.updatedAt(), result.generatedAt(),
                result.message()
        );
    }
}
