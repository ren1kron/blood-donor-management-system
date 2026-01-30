package ifmo.se.coursach_back.report.api;

import ifmo.se.coursach_back.report.api.dto.ReportRequestCreateRequest;
import ifmo.se.coursach_back.report.api.dto.ReportRequestDetailsResponse;
import ifmo.se.coursach_back.report.api.dto.ReportRequestSummaryResponse;
import ifmo.se.coursach_back.report.application.command.CreateReportRequestCommand;
import ifmo.se.coursach_back.report.application.result.ReportRequestDetailsResult;
import ifmo.se.coursach_back.report.application.result.ReportRequestSummaryResult;
import ifmo.se.coursach_back.report.application.usecase.CreateReportRequestUseCase;
import ifmo.se.coursach_back.report.application.usecase.GetReportUseCase;
import ifmo.se.coursach_back.report.application.usecase.ListMyReportRequestsUseCase;
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
    private final CreateReportRequestUseCase createReportRequestUseCase;
    private final ListMyReportRequestsUseCase listMyReportRequestsUseCase;
    private final GetReportUseCase getReportUseCase;

    @PostMapping("/requests")
    public ResponseEntity<ReportRequestSummaryResponse> createRequest(
            @AuthenticationPrincipal AccountPrincipal principal,
            @Valid @RequestBody ReportRequestCreateRequest request) {
        CreateReportRequestCommand command = new CreateReportRequestCommand(
                principal.getId(), request.donorId(),
                request.reportType(), request.comment()
        );
        ReportRequestSummaryResult result = createReportRequestUseCase.execute(command);
        ReportRequestSummaryResponse response = mapToSummaryResponse(result);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/requests/mine")
    public List<ReportRequestSummaryResponse> listMyRequests(
            @AuthenticationPrincipal AccountPrincipal principal) {
        List<ReportRequestSummaryResult> results = listMyReportRequestsUseCase.execute(principal.getId());
        return results.stream().map(this::mapToSummaryResponse).toList();
    }

    @GetMapping("/{requestId}")
    public ReportRequestDetailsResponse getReport(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable UUID requestId) {
        ReportRequestDetailsResult result = getReportUseCase.execute(principal.getId(), requestId);
        return new ReportRequestDetailsResponse(
                result.requestId(), result.donorId(), result.donorName(),
                result.reportType(), result.status(), result.requestedByName(),
                result.requestedByRole(), result.assignedAdminName(),
                result.createdAt(), result.updatedAt(), result.generatedAt(),
                result.message(), result.payload()
        );
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
