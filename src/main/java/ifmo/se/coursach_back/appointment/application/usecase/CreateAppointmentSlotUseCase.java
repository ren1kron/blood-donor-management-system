package ifmo.se.coursach_back.appointment.application.usecase;

import ifmo.se.coursach_back.appointment.application.command.CreateSlotCommand;
import ifmo.se.coursach_back.appointment.application.result.AppointmentSlotResult;

/**
 * Use case for creating an appointment slot.
 */
public interface CreateAppointmentSlotUseCase {
    AppointmentSlotResult execute(CreateSlotCommand command);
}
