package ifmo.se.coursach_back.report.application.usecase;

import ifmo.se.coursach_back.report.api.dto.ReportRequestActionRequest;
import ifmo.se.coursach_back.report.api.dto.ReportRequestSummaryResponse;
import ifmo.se.coursach_back.report.application.ReportRequestService;
import ifmo.se.coursach_back.report.application.command.RejectReportRequestCommand;
import ifmo.se.coursach_back.report.application.result.ReportRequestSummaryResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of RejectReportRequestUseCase that delegates to ReportRequestService.
 */
@Service
@RequiredArgsConstructor
public class RejectReportRequestService implements RejectReportRequestUseCase {
    private final ReportRequestService reportRequestService;

    @Override
    public ReportRequestSummaryResult execute(RejectReportRequestCommand command) {
        ReportRequestActionRequest actionRequest = new ReportRequestActionRequest(command.reason());
        ReportRequestSummaryResponse response = reportRequestService.rejectReport(
                command.adminAccountId(),
                command.requestId(),
                actionRequest
        );
        return toResult(response);
    }

    private ReportRequestSummaryResult toResult(ReportRequestSummaryResponse response) {
        return new ReportRequestSummaryResult(
                response.id(),
                response.donorId(),
                response.donorName(),
                response.reportType(),
                response.status(),
                response.requestedByName(),
                response.requestedByRole(),
                response.assignedAdminName(),
                response.createdAt(),
                response.updatedAt(),
                response.generatedAt(),
                response.message()
        );
    }
}
