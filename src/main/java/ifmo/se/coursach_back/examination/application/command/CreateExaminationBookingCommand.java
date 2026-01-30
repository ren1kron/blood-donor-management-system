package ifmo.se.coursach_back.examination.application.command;

import java.util.UUID;

/**
 * Command object for creating an examination booking.
 */
public record CreateExaminationBookingCommand(
        UUID accountId,
        UUID slotId
) {
}
