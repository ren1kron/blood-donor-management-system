package ifmo.se.coursach_back.report.application.usecase;

import ifmo.se.coursach_back.report.api.dto.ReportRequestSummaryResponse;
import ifmo.se.coursach_back.report.application.ReportRequestService;
import ifmo.se.coursach_back.report.application.command.TakeReportRequestCommand;
import ifmo.se.coursach_back.report.application.result.ReportRequestSummaryResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of TakeReportRequestUseCase that delegates to ReportRequestService.
 */
@Service
@RequiredArgsConstructor
public class TakeReportRequestService implements TakeReportRequestUseCase {
    private final ReportRequestService reportRequestService;

    @Override
    public ReportRequestSummaryResult execute(TakeReportRequestCommand command) {
        ReportRequestSummaryResponse response = reportRequestService.takeRequest(
                command.adminAccountId(),
                command.requestId()
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
