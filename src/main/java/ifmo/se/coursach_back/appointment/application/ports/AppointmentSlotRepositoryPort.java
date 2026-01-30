package ifmo.se.coursach_back.appointment.application.ports;

import ifmo.se.coursach_back.appointment.domain.AppointmentSlot;
import ifmo.se.coursach_back.appointment.domain.SlotPurpose;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port interface for AppointmentSlot repository operations.
 * Application layer depends on this interface, not on JPA repository.
 */
public interface AppointmentSlotRepositoryPort {
    Optional<AppointmentSlot> findById(UUID id);
    List<AppointmentSlot> findSlotsStartingAfter(OffsetDateTime startAt);
    List<AppointmentSlot> findByPurposeStartingAfter(SlotPurpose purpose, OffsetDateTime startAt);
    List<AppointmentSlot> findByPurposeAndTimeRange(SlotPurpose purpose, OffsetDateTime from, OffsetDateTime to);
    AppointmentSlot save(AppointmentSlot slot);
    void deleteById(UUID id);
}
