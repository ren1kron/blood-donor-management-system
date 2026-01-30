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
    long countBySlotIdAndStatus(UUID slotId, BookingStatus status);
    boolean existsByDonorIdAndSlotId(UUID donorId, UUID slotId);
    List<Booking> findByStatusAfter(BookingStatus status, OffsetDateTime startAt);
    List<Booking> findByStatusesAfter(List<BookingStatus> statuses, OffsetDateTime startAt);
    List<Booking> findByStatusesAndPurposeAfter(List<BookingStatus> statuses, SlotPurpose purpose, OffsetDateTime startAt);
    List<Booking> findRecentByDonorAccountId(UUID accountId);
    Optional<Booking> findByIdAndDonorAccountId(UUID bookingId, UUID accountId);
    long countActiveBookingsBySlotId(UUID slotId);
    Optional<Booking> findPendingBookingByDonorAndSlot(UUID donorId, UUID slotId, BookingStatus status);
    Optional<Booking> findByIdAndDonorId(UUID bookingId, UUID donorId);
    List<Booking> findExaminationBookingsFrom(OffsetDateTime startTime);
    Booking save(Booking booking);
}
