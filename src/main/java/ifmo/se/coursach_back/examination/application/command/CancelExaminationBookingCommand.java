package ifmo.se.coursach_back.examination.application.command;

import java.util.UUID;

/**
 * Command object for cancelling an examination booking.
 */
public record CancelExaminationBookingCommand(
        UUID accountId,
        UUID bookingId
) {
}
