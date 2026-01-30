package ifmo.se.coursach_back.repository;

import ifmo.se.coursach_back.model.AppointmentSlot;
import ifmo.se.coursach_back.model.SlotPurpose;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentSlotRepository extends JpaRepository<AppointmentSlot, UUID> {
    List<AppointmentSlot> findByStartAtAfterOrderByStartAtAsc(OffsetDateTime startAt);

    List<AppointmentSlot> findByPurposeAndStartAtAfterOrderByStartAtAsc(SlotPurpose purpose,
                                                                         OffsetDateTime startAt);
    
    List<AppointmentSlot> findByPurposeAndStartAtBetweenOrderByStartAtAsc(
            SlotPurpose purpose, OffsetDateTime from, OffsetDateTime to);
}
