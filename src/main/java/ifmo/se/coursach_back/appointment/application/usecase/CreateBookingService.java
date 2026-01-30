package ifmo.se.coursach_back.appointment.application.usecase;

import ifmo.se.coursach_back.appointment.application.AppointmentService;
import ifmo.se.coursach_back.appointment.application.command.CreateBookingCommand;
import ifmo.se.coursach_back.appointment.application.result.BookingResult;
import ifmo.se.coursach_back.appointment.domain.Booking;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateBookingService implements CreateBookingUseCase {
    private final AppointmentService appointmentService;

    @Override
    public BookingResult execute(CreateBookingCommand command) {
        Booking booking = appointmentService.createBooking(command.accountId(), command.slotId());
        return new BookingResult(
                booking.getId(),
                booking.getSlot().getId(),
                booking.getStatus(),
                booking.getCreatedAt()
        );
    }
}
