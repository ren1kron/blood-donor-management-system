package ifmo.se.coursach_back.lab.application.command;

import java.util.UUID;

/**
 * Command object for publishing a lab test result.
 */
public record PublishLabResultCommand(
        UUID accountId,
        UUID resultId
) {
}
