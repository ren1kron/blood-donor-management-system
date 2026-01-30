package ifmo.se.coursach_back.report.application.command;

import java.util.UUID;

/**
 * Command object for taking/assigning a report request to an admin.
 */
public record TakeReportRequestCommand(
        UUID adminAccountId,
        UUID requestId
) {
}
