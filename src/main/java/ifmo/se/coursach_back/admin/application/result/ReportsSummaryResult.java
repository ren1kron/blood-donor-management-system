package ifmo.se.coursach_back.admin.application.result;

import java.util.Map;

/**
 * Result object for reports summary.
 */
public record ReportsSummaryResult(
        long donorsTotalCount,
        long donorsActiveCount,
        long donationsCount,
        long donationsLastWeek,
        long donationsLastMonth,
        long samplesCount,
        long publishedResultsCount,
        long eligibleCandidatesCount,
        long pendingReviewCount,
        long labQueueCount,
        Map<String, Long> bloodUnitsByGroupRh
) {
}
