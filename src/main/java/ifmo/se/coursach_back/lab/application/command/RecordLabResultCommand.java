package ifmo.se.coursach_back.lab.application.command;

import java.util.UUID;

/**
 * Command object for recording a lab test result.
 */
public record RecordLabResultCommand(
        UUID accountId,
        UUID sampleId,
        Short testTypeId,
        String resultValue,
        String resultFlag
) {
}
