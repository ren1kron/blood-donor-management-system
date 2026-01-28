package ifmo.se.coursach_back.admin.dto;

import java.util.Map;

public record ReportsSummaryResponse(
        long donorsActiveCount,
        long donationsCount,
        long samplesCount,
        long publishedResultsCount,
        long eligibleCandidatesCount,
        Map<String, Long> bloodUnitsByGroupRh
) {}
