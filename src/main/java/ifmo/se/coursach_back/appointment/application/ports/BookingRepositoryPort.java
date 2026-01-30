package ifmo.se.coursach_back.appointment.application.ports;

import ifmo.se.coursach_back.appointment.domain.Booking;
import ifmo.se.coursach_back.appointment.domain.BookingStatus;
import ifmo.se.coursach_back.appointment.domain.SlotPurpose;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port interface for Booking repository operations.
 * Application layer depends on this interface, not on JPA repository.
 */
public interface BookingRepositoryPort {
    Optional<Booking> findById(UUID id);
    long countBySlot_IdAndStatus(UUID slotId, BookingStatus status);
    boolean existsByDonor_IdAndSlot_Id(UUID donorId, UUID slotId);
    List<Booking> findByStatusAndSlot_StartAtAfterOrderBySlot_StartAtAsc(BookingStatus status, OffsetDateTime startAt);
    List<Booking> findByStatusInAndSlot_StartAtAfterOrderBySlot_StartAtAsc(List<BookingStatus> statuses, OffsetDateTime startAt);
    List<Booking> findByStatusInAndSlot_PurposeAndSlot_StartAtAfterOrderBySlot_StartAtAsc(List<BookingStatus> statuses, SlotPurpose purpose, OffsetDateTime startAt);
    List<Booking> findByDonor_Account_IdOrderBySlot_StartAtDesc(UUID accountId);
    Optional<Booking> findByIdAndDonor_Account_Id(UUID bookingId, UUID accountId);
    long countActiveBookingsBySlotId(UUID slotId);
    Optional<Booking> findByDonor_IdAndSlot_IdAndStatusAndCancelledAtIsNull(UUID donorId, UUID slotId, BookingStatus status);
    Optional<Booking> findByIdAndDonor_Id(UUID bookingId, UUID donorId);
    List<Booking> findExaminationBookingsFrom(OffsetDateTime startTime);
    Booking save(Booking booking);
}
