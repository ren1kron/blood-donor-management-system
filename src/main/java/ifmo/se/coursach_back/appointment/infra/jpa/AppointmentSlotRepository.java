package ifmo.se.coursach_back.appointment.infra.jpa;

import ifmo.se.coursach_back.appointment.domain.AppointmentSlot;
import ifmo.se.coursach_back.appointment.domain.SlotPurpose;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AppointmentSlotRepository extends JpaRepository<AppointmentSlot, UUID> {
    
    @Query("""
            SELECT s FROM AppointmentSlot s
            WHERE s.startAt > :startAt
            ORDER BY s.startAt ASC
            """)
    List<AppointmentSlot> findSlotsStartingAfter(@Param("startAt") OffsetDateTime startAt);

    @Query("""
            SELECT s FROM AppointmentSlot s
            WHERE s.purpose = :purpose
              AND s.startAt > :startAt
            ORDER BY s.startAt ASC
            """)
    List<AppointmentSlot> findByPurposeStartingAfter(
            @Param("purpose") SlotPurpose purpose,
            @Param("startAt") OffsetDateTime startAt);
    
    @Query("""
            SELECT s FROM AppointmentSlot s
            WHERE s.purpose = :purpose
              AND s.startAt >= :from
              AND s.startAt <= :to
            ORDER BY s.startAt ASC
            """)
    List<AppointmentSlot> findByPurposeAndTimeRange(
            @Param("purpose") SlotPurpose purpose,
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to);
}
