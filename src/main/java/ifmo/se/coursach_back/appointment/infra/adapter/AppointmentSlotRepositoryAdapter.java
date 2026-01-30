package ifmo.se.coursach_back.appointment.infra.adapter;

import ifmo.se.coursach_back.appointment.application.ports.AppointmentSlotRepositoryPort;
import ifmo.se.coursach_back.appointment.domain.AppointmentSlot;
import ifmo.se.coursach_back.appointment.domain.SlotPurpose;
import ifmo.se.coursach_back.appointment.infra.jpa.AppointmentSlotRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AppointmentSlotRepositoryAdapter implements AppointmentSlotRepositoryPort {
    private final AppointmentSlotRepository jpaRepository;

    @Override
    public Optional<AppointmentSlot> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<AppointmentSlot> findSlotsStartingAfter(OffsetDateTime startAt) {
        return jpaRepository.findSlotsStartingAfter(startAt);
    }

    @Override
    public List<AppointmentSlot> findByPurposeStartingAfter(SlotPurpose purpose, OffsetDateTime startAt) {
        return jpaRepository.findByPurposeStartingAfter(purpose, startAt);
    }

    @Override
    public List<AppointmentSlot> findByPurposeAndTimeRange(SlotPurpose purpose, OffsetDateTime from, OffsetDateTime to) {
        return jpaRepository.findByPurposeAndTimeRange(purpose, from, to);
    }

    @Override
    public AppointmentSlot save(AppointmentSlot slot) {
        return jpaRepository.save(slot);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}
