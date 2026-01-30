package ifmo.se.coursach_back.report.application.command;

import java.util.UUID;

/**
 * Command object for processing a report request and generating the report.
 */
public record ProcessReportRequestCommand(
        UUID adminAccountId,
        UUID requestId,
        String payloadJson
) {
}
