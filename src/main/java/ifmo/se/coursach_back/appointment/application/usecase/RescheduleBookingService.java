package ifmo.se.coursach_back.appointment.application.usecase;

import ifmo.se.coursach_back.appointment.application.AppointmentService;
import ifmo.se.coursach_back.appointment.application.command.RescheduleBookingCommand;
import ifmo.se.coursach_back.appointment.application.result.DonorBookingResult;
import ifmo.se.coursach_back.appointment.domain.Booking;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RescheduleBookingService implements RescheduleBookingUseCase {
    private final AppointmentService appointmentService;

    @Override
    public DonorBookingResult execute(RescheduleBookingCommand command) {
        Booking booking = appointmentService.rescheduleBooking(
                command.accountId(), command.bookingId(), command.newSlotId()
        );
        return new DonorBookingResult(
                booking.getId(),
                booking.getSlot().getId(),
                booking.getSlot().getPurpose(),
                booking.getSlot().getStartAt(),
                booking.getSlot().getEndAt(),
                booking.getSlot().getLocation(),
                booking.getStatus(),
                booking.getCreatedAt(),
                booking.getCancelledAt(),
                false
        );
    }
}
