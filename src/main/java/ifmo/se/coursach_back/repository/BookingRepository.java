package ifmo.se.coursach_back.repository;

import ifmo.se.coursach_back.model.Booking;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    long countBySlot_IdAndStatus(UUID slotId, String status);

    boolean existsByDonor_IdAndSlot_Id(UUID donorId, UUID slotId);

    List<Booking> findByStatusAndSlot_StartAtAfterOrderBySlot_StartAtAsc(String status,
                                                                         java.time.OffsetDateTime startAt);
    
    List<Booking> findByStatusInAndSlot_StartAtAfterOrderBySlot_StartAtAsc(List<String> statuses,
                                                                           java.time.OffsetDateTime startAt);

    List<Booking> findByDonor_Account_IdOrderBySlot_StartAtDesc(UUID accountId);

    Optional<Booking> findByIdAndDonor_Account_Id(UUID bookingId, UUID accountId);
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.slot.id = :slotId " +
           "AND b.status IN ('PENDING_QUESTIONNAIRE', 'CONFIRMED', 'BOOKED') " +
           "AND b.cancelledAt IS NULL")
    long countActiveBookingsBySlotId(@Param("slotId") UUID slotId);
    
    Optional<Booking> findByDonor_IdAndSlot_IdAndStatusAndCancelledAtIsNull(
            UUID donorId, UUID slotId, String status);
    
    Optional<Booking> findByIdAndDonor_Id(UUID bookingId, UUID donorId);
    

    @Query("SELECT b FROM Booking b " +
           "JOIN FETCH b.donor " +
           "JOIN FETCH b.slot " +
           "WHERE b.slot.purpose = 'EXAMINATION' " +
           "AND b.slot.startAt >= :startTime " +
           "ORDER BY b.slot.startAt ASC")
    List<Booking> findExaminationBookingsFrom(@Param("startTime") OffsetDateTime startTime);
}
