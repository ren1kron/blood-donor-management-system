package ifmo.se.coursach_back.appointment.application.usecase;

import ifmo.se.coursach_back.appointment.api.dto.CreateSlotRequest;
import ifmo.se.coursach_back.appointment.application.AppointmentService;
import ifmo.se.coursach_back.appointment.application.command.CreateSlotCommand;
import ifmo.se.coursach_back.appointment.application.result.AppointmentSlotResult;
import ifmo.se.coursach_back.appointment.domain.AppointmentSlot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateAppointmentSlotService implements CreateAppointmentSlotUseCase {
    private final AppointmentService appointmentService;

    @Override
    public AppointmentSlotResult execute(CreateSlotCommand command) {
        CreateSlotRequest request = new CreateSlotRequest(
                command.purpose(),
                command.startAt(),
                command.endAt(),
                command.location(),
                command.capacity()
        );
        AppointmentSlot slot = appointmentService.createSlot(request);
        return new AppointmentSlotResult(
                slot.getId(),
                slot.getPurpose(),
                slot.getStartAt(),
                slot.getEndAt(),
                slot.getLocation(),
                slot.getCapacity(),
                0L
        );
    }
}
