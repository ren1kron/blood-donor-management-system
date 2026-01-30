package ifmo.se.coursach_back.appointment.application.usecase;

import ifmo.se.coursach_back.appointment.application.AppointmentService;
import ifmo.se.coursach_back.appointment.application.result.AppointmentSlotResult;
import ifmo.se.coursach_back.appointment.domain.SlotPurpose;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ListAppointmentSlotsService implements ListAppointmentSlotsUseCase {
    private final AppointmentService appointmentService;

    @Override
    public List<AppointmentSlotResult> execute(OffsetDateTime from, SlotPurpose purpose) {
        OffsetDateTime start = from == null ? OffsetDateTime.now() : from;
        return appointmentService.listUpcomingSlots(start, purpose).stream()
                .map(slot -> new AppointmentSlotResult(
                        slot.getId(),
                        slot.getPurpose(),
                        slot.getStartAt(),
                        slot.getEndAt(),
                        slot.getLocation(),
                        slot.getCapacity(),
                        appointmentService.getSlotBookedCount(slot.getId())
                ))
                .toList();
    }
}
