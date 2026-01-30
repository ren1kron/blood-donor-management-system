package ifmo.se.coursach_back.admin.api.dto;

import java.util.Map;

public record ReportsSummaryResponse(
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
) {}
