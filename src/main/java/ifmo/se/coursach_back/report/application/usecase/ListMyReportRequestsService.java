package ifmo.se.coursach_back.report.application.usecase;

import ifmo.se.coursach_back.report.api.dto.ReportRequestSummaryResponse;
import ifmo.se.coursach_back.report.application.ReportRequestService;
import ifmo.se.coursach_back.report.application.result.ReportRequestSummaryResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Implementation of ListMyReportRequestsUseCase that delegates to ReportRequestService.
 */
@Service
@RequiredArgsConstructor
public class ListMyReportRequestsService implements ListMyReportRequestsUseCase {
    private final ReportRequestService reportRequestService;

    @Override
    public List<ReportRequestSummaryResult> execute(UUID accountId) {
        return reportRequestService.listMyRequests(accountId).stream()
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
