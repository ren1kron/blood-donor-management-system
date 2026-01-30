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
    public List<AppointmentSlot> findByStartAtAfterOrderByStartAtAsc(OffsetDateTime startAt) {
        return jpaRepository.findByStartAtAfterOrderByStartAtAsc(startAt);
    }

    @Override
    public List<AppointmentSlot> findByPurposeAndStartAtAfterOrderByStartAtAsc(SlotPurpose purpose, OffsetDateTime startAt) {
        return jpaRepository.findByPurposeAndStartAtAfterOrderByStartAtAsc(purpose, startAt);
    }

    @Override
    public List<AppointmentSlot> findByPurposeAndStartAtBetweenOrderByStartAtAsc(SlotPurpose purpose, OffsetDateTime from, OffsetDateTime to) {
        return jpaRepository.findByPurposeAndStartAtBetweenOrderByStartAtAsc(purpose, from, to);
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
