package ifmo.se.coursach_back.admin.application.usecase;

import ifmo.se.coursach_back.admin.api.dto.ReportsSummaryResponse;
import ifmo.se.coursach_back.admin.application.AdminService;
import ifmo.se.coursach_back.admin.application.result.ReportsSummaryResult;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of GetReportsSummaryUseCase that delegates to AdminService.
 */
@Service
@RequiredArgsConstructor
public class GetReportsSummaryService implements GetReportsSummaryUseCase {
    private final AdminService adminService;

    @Override
    public ReportsSummaryResult execute(OffsetDateTime from, OffsetDateTime to) {
        ReportsSummaryResponse response = adminService.getReportsSummary(from, to);
        return new ReportsSummaryResult(
                response.donorsTotalCount(),
                response.donorsActiveCount(),
                response.donationsCount(),
                response.donationsLastWeek(),
                response.donationsLastMonth(),
                response.samplesCount(),
                response.publishedResultsCount(),
                response.eligibleCandidatesCount(),
                response.pendingReviewCount(),
                response.labQueueCount(),
                response.bloodUnitsByGroupRh()
        );
    }
}
