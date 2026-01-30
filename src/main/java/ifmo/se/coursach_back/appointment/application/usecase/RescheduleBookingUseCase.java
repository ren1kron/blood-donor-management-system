package ifmo.se.coursach_back.appointment.application.usecase;

import ifmo.se.coursach_back.appointment.application.command.RescheduleBookingCommand;
import ifmo.se.coursach_back.appointment.application.result.DonorBookingResult;

/**
 * Use case for rescheduling a booking.
 */
public interface RescheduleBookingUseCase {
    DonorBookingResult execute(RescheduleBookingCommand command);
}
