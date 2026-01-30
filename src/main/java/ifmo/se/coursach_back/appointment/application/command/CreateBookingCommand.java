package ifmo.se.coursach_back.appointment.application.command;

import java.util.UUID;

/**
 * Command for creating a booking.
 */
public record CreateBookingCommand(
        UUID accountId,
        UUID slotId
) {
}
