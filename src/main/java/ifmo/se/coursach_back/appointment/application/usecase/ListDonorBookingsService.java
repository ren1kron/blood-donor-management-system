package ifmo.se.coursach_back.appointment.application.usecase;

import ifmo.se.coursach_back.appointment.application.AppointmentService;
import ifmo.se.coursach_back.appointment.application.result.DonorBookingResult;
import ifmo.se.coursach_back.donor.api.dto.DonorBookingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ListDonorBookingsService implements ListDonorBookingsUseCase {
    private final AppointmentService appointmentService;

    @Override
    public List<DonorBookingResult> execute(UUID accountId) {
        List<DonorBookingResponse> responses = appointmentService.listDonorBookings(accountId);
        return responses.stream()
                .map(r -> new DonorBookingResult(
                        r.id(),
                        r.slotId(),
                        r.slotPurpose(),
                        r.slotStartTime(),
                        r.slotEndTime(),
                        r.slotLocation(),
                        r.status(),
                        r.createdAt(),
                        r.cancelledAt(),
                        r.hasVisit()
                ))
                .toList();
    }
}
