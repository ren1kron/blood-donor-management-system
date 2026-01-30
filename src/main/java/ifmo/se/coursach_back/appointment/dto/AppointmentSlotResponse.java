package ifmo.se.coursach_back.appointment.dto;

import ifmo.se.coursach_back.model.AppointmentSlot;
import ifmo.se.coursach_back.model.SlotPurpose;
import java.time.OffsetDateTime;
import java.util.UUID;

public record AppointmentSlotResponse(
        UUID id,
        SlotPurpose purpose,
        OffsetDateTime startAt,
        OffsetDateTime endAt,
        String location,
        Integer capacity,
        Long bookedCount
) {
    public static AppointmentSlotResponse from(AppointmentSlot slot, Long bookedCount) {
        return new AppointmentSlotResponse(
                slot.getId(),
                slot.getPurpose(),
                slot.getStartAt(),
                slot.getEndAt(),
                slot.getLocation(),
                slot.getCapacity(),
                bookedCount
        );
    }

    public static AppointmentSlotResponse from(AppointmentSlot slot) {
        return from(slot, 0L);
    }
}
