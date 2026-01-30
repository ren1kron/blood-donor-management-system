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
    long countBySlotIdAndStatus(UUID slotId, BookingStatus status);

    boolean existsByDonorIdAndSlotId(UUID donorId, UUID slotId);

    @Query("""
            SELECT b FROM Booking b
            JOIN FETCH b.slot slot
            WHERE b.status = :status
              AND slot.startAt > :startAt
            ORDER BY slot.startAt ASC
            """)
    List<Booking> findByStatusAfter(
            @Param("status") BookingStatus status,
            @Param("startAt") OffsetDateTime startAt);
    
    @Query("""
            SELECT b FROM Booking b
            JOIN FETCH b.slot slot
            WHERE b.status IN :statuses
              AND slot.startAt > :startAt
            ORDER BY slot.startAt ASC
            """)
    List<Booking> findByStatusesAfter(
            @Param("statuses") List<BookingStatus> statuses,
            @Param("startAt") OffsetDateTime startAt);

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
    List<Booking> findByStatusesAndPurposeAfter(
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
    List<Booking> findRecentByDonorAccountId(@Param("accountId") UUID accountId);

    @Query("""
            SELECT b FROM Booking b
            JOIN FETCH b.slot
            JOIN FETCH b.donor donor
            WHERE b.id = :bookingId
              AND donor.account.id = :accountId
            """)
    Optional<Booking> findByIdAndDonorAccountId(
            @Param("bookingId") UUID bookingId,
            @Param("accountId") UUID accountId);
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.slot.id = :slotId " +
           "AND b.status IN (ifmo.se.coursach_back.appointment.domain.BookingStatus.PENDING_QUESTIONNAIRE, " +
           "ifmo.se.coursach_back.appointment.domain.BookingStatus.CONFIRMED, " +
           "ifmo.se.coursach_back.appointment.domain.BookingStatus.BOOKED) " +
           "AND b.cancelledAt IS NULL")
    long countActiveBookingsBySlotId(@Param("slotId") UUID slotId);
    
    @Query("""
            SELECT b FROM Booking b
            WHERE b.donor.id = :donorId
              AND b.slot.id = :slotId
              AND b.status = :status
              AND b.cancelledAt IS NULL
            """)
    Optional<Booking> findPendingBookingByDonorAndSlot(
            @Param("donorId") UUID donorId,
            @Param("slotId") UUID slotId,
            @Param("status") BookingStatus status);
    
    Optional<Booking> findByIdAndDonorId(UUID bookingId, UUID donorId);
    

    @Query("SELECT b FROM Booking b " +
           "JOIN FETCH b.donor " +
           "JOIN FETCH b.slot " +
           "WHERE b.slot.purpose = ifmo.se.coursach_back.appointment.domain.SlotPurpose.EXAMINATION " +
           "AND b.slot.startAt >= :startTime " +
           "ORDER BY b.slot.startAt ASC")
    List<Booking> findExaminationBookingsFrom(@Param("startTime") OffsetDateTime startTime);
}
