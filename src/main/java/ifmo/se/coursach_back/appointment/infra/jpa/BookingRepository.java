package ifmo.se.coursach_back.appointment.infra.jpa;

import ifmo.se.coursach_back.appointment.domain.Booking;
import ifmo.se.coursach_back.appointment.domain.BookingStatus;
import ifmo.se.coursach_back.appointment.domain.SlotPurpose;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    long countBySlot_IdAndStatus(UUID slotId, BookingStatus status);

    boolean existsByDonor_IdAndSlot_Id(UUID donorId, UUID slotId);

    List<Booking> findByStatusAndSlot_StartAtAfterOrderBySlot_StartAtAsc(BookingStatus status,
                                                                         OffsetDateTime startAt);
    
    List<Booking> findByStatusInAndSlot_StartAtAfterOrderBySlot_StartAtAsc(List<BookingStatus> statuses,
                                                                           OffsetDateTime startAt);

    @Query("""
            select b
            from Booking b
            join fetch b.donor donor
            join fetch b.slot slot
            where b.status in :statuses
              and slot.purpose = :purpose
              and slot.startAt > :startAt
            order by slot.startAt asc
            """)
    List<Booking> findByStatusInAndSlot_PurposeAndSlot_StartAtAfterOrderBySlot_StartAtAsc(
            @Param("statuses") List<BookingStatus> statuses,
            @Param("purpose") SlotPurpose purpose,
            @Param("startAt") OffsetDateTime startAt);

    @Query("""
            select b
            from Booking b
            join fetch b.slot slot
            join fetch b.donor donor
            where donor.account.id = :accountId
            order by slot.startAt desc
            """)
    List<Booking> findByDonor_Account_IdOrderBySlot_StartAtDesc(@Param("accountId") UUID accountId);

    Optional<Booking> findByIdAndDonor_Account_Id(UUID bookingId, UUID accountId);
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.slot.id = :slotId " +
           "AND b.status IN (ifmo.se.coursach_back.appointment.domain.BookingStatus.PENDING_QUESTIONNAIRE, " +
           "ifmo.se.coursach_back.appointment.domain.BookingStatus.CONFIRMED, " +
           "ifmo.se.coursach_back.appointment.domain.BookingStatus.BOOKED) " +
           "AND b.cancelledAt IS NULL")
    long countActiveBookingsBySlotId(@Param("slotId") UUID slotId);
    
    Optional<Booking> findByDonor_IdAndSlot_IdAndStatusAndCancelledAtIsNull(
            UUID donorId, UUID slotId, BookingStatus status);
    
    Optional<Booking> findByIdAndDonor_Id(UUID bookingId, UUID donorId);
    

    @Query("SELECT b FROM Booking b " +
           "JOIN FETCH b.donor " +
           "JOIN FETCH b.slot " +
           "WHERE b.slot.purpose = ifmo.se.coursach_back.appointment.domain.SlotPurpose.EXAMINATION " +
           "AND b.slot.startAt >= :startTime " +
           "ORDER BY b.slot.startAt ASC")
    List<Booking> findExaminationBookingsFrom(@Param("startTime") OffsetDateTime startTime);
}
