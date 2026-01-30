package ifmo.se.coursach_back.appointment.application.usecase;

import ifmo.se.coursach_back.appointment.application.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CancelBookingService implements CancelBookingUseCase {
    private final AppointmentService appointmentService;

    @Override
    public void execute(UUID accountId, UUID bookingId) {
        appointmentService.cancelBooking(accountId, bookingId);
    }
}
