package ifmo.se.coursach_back.appointment.application.command;

import java.util.UUID;

/**
 * Command for rescheduling a booking.
 */
public record RescheduleBookingCommand(
        UUID accountId,
        UUID bookingId,
        UUID newSlotId
) {
}
