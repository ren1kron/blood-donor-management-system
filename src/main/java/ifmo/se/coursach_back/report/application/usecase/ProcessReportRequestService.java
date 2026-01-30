package ifmo.se.coursach_back.report.application.usecase;

import ifmo.se.coursach_back.report.api.dto.ReportRequestDetailsResponse;
import ifmo.se.coursach_back.report.application.ReportRequestService;
import ifmo.se.coursach_back.report.application.command.ProcessReportRequestCommand;
import ifmo.se.coursach_back.report.application.result.ReportRequestDetailsResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of ProcessReportRequestUseCase that delegates to ReportRequestService.
 */
@Service
@RequiredArgsConstructor
public class ProcessReportRequestService implements ProcessReportRequestUseCase {
    private final ReportRequestService reportRequestService;

    @Override
    public ReportRequestDetailsResult execute(ProcessReportRequestCommand command) {
        ReportRequestDetailsResponse response = reportRequestService.generateReport(
                command.adminAccountId(),
                command.requestId()
        );
        return toResult(response);
    }

    private ReportRequestDetailsResult toResult(ReportRequestDetailsResponse response) {
        return new ReportRequestDetailsResult(
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
                response.message(),
                response.payload()
        );
    }
}
