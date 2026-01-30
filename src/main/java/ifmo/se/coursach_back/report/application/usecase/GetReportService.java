package ifmo.se.coursach_back.report.application.usecase;

import ifmo.se.coursach_back.report.api.dto.ReportRequestDetailsResponse;
import ifmo.se.coursach_back.report.application.ReportRequestService;
import ifmo.se.coursach_back.report.application.result.ReportRequestDetailsResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Implementation of GetReportUseCase that delegates to ReportRequestService.
 */
@Service
@RequiredArgsConstructor
public class GetReportService implements GetReportUseCase {
    private final ReportRequestService reportRequestService;

    @Override
    public ReportRequestDetailsResult execute(UUID accountId, UUID requestId) {
        ReportRequestDetailsResponse response = reportRequestService.getReport(accountId, requestId);
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
