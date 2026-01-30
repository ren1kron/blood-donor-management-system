package ifmo.se.coursach_back.appointment.application.command;

import ifmo.se.coursach_back.appointment.domain.SlotPurpose;
import java.time.OffsetDateTime;

/**
 * Command for creating an appointment slot.
 */
public record CreateSlotCommand(
        SlotPurpose purpose,
        OffsetDateTime startAt,
        OffsetDateTime endAt,
        String location,
        int capacity
) {
}
