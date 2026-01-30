package ifmo.se.coursach_back.report.application.usecase;

import ifmo.se.coursach_back.report.api.dto.ReportRequestSummaryResponse;
import ifmo.se.coursach_back.report.application.ReportRequestService;
import ifmo.se.coursach_back.report.application.result.ReportRequestSummaryResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of ListAllReportRequestsUseCase that delegates to ReportRequestService.
 */
@Service
@RequiredArgsConstructor
public class ListAllReportRequestsService implements ListAllReportRequestsUseCase {
    private final ReportRequestService reportRequestService;

    @Override
    public List<ReportRequestSummaryResult> execute(String status) {
        return reportRequestService.listRequestsForAdmin(status).stream()
                .map(this::toResult)
                .toList();
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
