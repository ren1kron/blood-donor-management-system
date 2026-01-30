package ifmo.se.coursach_back.appointment.application.usecase;

import ifmo.se.coursach_back.appointment.application.result.AppointmentSlotResult;
import ifmo.se.coursach_back.appointment.domain.SlotPurpose;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Use case for listing available appointment slots.
 */
public interface ListAppointmentSlotsUseCase {
    List<AppointmentSlotResult> execute(OffsetDateTime from, SlotPurpose purpose);
}
