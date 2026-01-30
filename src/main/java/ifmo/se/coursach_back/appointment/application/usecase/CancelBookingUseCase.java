package ifmo.se.coursach_back.appointment.application.usecase;

import java.util.UUID;

/**
 * Use case for cancelling a booking.
 */
public interface CancelBookingUseCase {
    void execute(UUID accountId, UUID bookingId);
}
