package ifmo.se.coursach_back.appointment.application.usecase;

import ifmo.se.coursach_back.appointment.application.result.DonorBookingResult;
import java.util.List;
import java.util.UUID;

/**
 * Use case for listing donor's bookings.
 */
public interface ListDonorBookingsUseCase {
    List<DonorBookingResult> execute(UUID accountId);
}
