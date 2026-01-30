package ifmo.se.coursach_back.report.application.command;

import ifmo.se.coursach_back.report.domain.ReportType;
import java.util.UUID;

/**
 * Command object for creating a report request.
 */
public record CreateReportRequestCommand(
        UUID accountId,
        UUID donorId,
        ReportType reportType,
        String comment
) {
}
