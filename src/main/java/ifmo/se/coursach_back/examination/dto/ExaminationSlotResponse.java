package ifmo.se.coursach_back.examination.dto;

import ifmo.se.coursach_back.model.AppointmentSlot;
import ifmo.se.coursach_back.model.SlotPurpose;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ExaminationSlotResponse(
        UUID slotId,
        SlotPurpose purpose,
        OffsetDateTime startAt,
        OffsetDateTime endAt,
        String location,
        int capacity,
        int remainingCapacity
) {
    public static ExaminationSlotResponse from(AppointmentSlot slot, long activeBookings) {
        int remaining = Math.max(0, slot.getCapacity() - (int) activeBookings);
        return new ExaminationSlotResponse(
                slot.getId(),
                slot.getPurpose(),
                slot.getStartAt(),
                slot.getEndAt(),
                slot.getLocation(),
                slot.getCapacity(),
                remaining
        );
    }
}
