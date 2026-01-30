package ifmo.se.coursach_back.report.application.command;

import java.util.UUID;

/**
 * Command object for rejecting a report request.
 */
public record RejectReportRequestCommand(
        UUID adminAccountId,
        UUID requestId,
        String reason
) {
}
