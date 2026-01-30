package ifmo.se.coursach_back.report.application.usecase;

import ifmo.se.coursach_back.report.api.dto.ReportRequestCreateRequest;
import ifmo.se.coursach_back.report.api.dto.ReportRequestSummaryResponse;
import ifmo.se.coursach_back.report.application.ReportRequestService;
import ifmo.se.coursach_back.report.application.command.CreateReportRequestCommand;
import ifmo.se.coursach_back.report.application.result.ReportRequestSummaryResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of CreateReportRequestUseCase that delegates to ReportRequestService.
 */
@Service
@RequiredArgsConstructor
public class CreateReportRequestService implements CreateReportRequestUseCase {
    private final ReportRequestService reportRequestService;

    @Override
    public ReportRequestSummaryResult execute(CreateReportRequestCommand command) {
        ReportRequestCreateRequest request = new ReportRequestCreateRequest(
                command.donorId(),
                command.reportType(),
                command.comment()
        );
        ReportRequestSummaryResponse response = reportRequestService.createRequest(
                command.accountId(),
                request
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
