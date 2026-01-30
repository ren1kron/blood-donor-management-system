package ifmo.se.coursach_back.appointment.application.usecase;

import ifmo.se.coursach_back.appointment.application.command.CreateBookingCommand;
import ifmo.se.coursach_back.appointment.application.result.BookingResult;

/**
 * Use case for creating a booking.
 */
public interface CreateBookingUseCase {
    BookingResult execute(CreateBookingCommand command);
}
