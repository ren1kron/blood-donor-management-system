package ifmo.se.coursach_back.appointment.dto;

import ifmo.se.coursach_back.model.AppointmentSlot;
import java.time.OffsetDateTime;
import java.util.UUID;

public record AppointmentSlotResponse(
        UUID id,
        String purpose,
        OffsetDateTime startAt,
        OffsetDateTime endAt,
        String location,
        Integer capacity
) {
    public static AppointmentSlotResponse from(AppointmentSlot slot) {
        return new AppointmentSlotResponse(
                slot.getId(),
                slot.getPurpose(),
                slot.getStartAt(),
                slot.getEndAt(),
                slot.getLocation(),
                slot.getCapacity()
        );
    }
}
