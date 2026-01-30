package ifmo.se.coursach_back.appointment.infra.adapter;

import ifmo.se.coursach_back.appointment.application.ports.BookingRepositoryPort;
import ifmo.se.coursach_back.appointment.domain.Booking;
import ifmo.se.coursach_back.appointment.domain.BookingStatus;
import ifmo.se.coursach_back.appointment.domain.SlotPurpose;
import ifmo.se.coursach_back.appointment.infra.jpa.BookingRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BookingRepositoryAdapter implements BookingRepositoryPort {
    private final BookingRepository jpaRepository;

    @Override
    public Optional<Booking> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public long countBySlotIdAndStatus(UUID slotId, BookingStatus status) {
        return jpaRepository.countBySlotIdAndStatus(slotId, status);
    }

    @Override
    public boolean existsByDonorIdAndSlotId(UUID donorId, UUID slotId) {
        return jpaRepository.existsByDonorIdAndSlotId(donorId, slotId);
    }

    @Override
    public List<Booking> findByStatusAfter(BookingStatus status, OffsetDateTime startAt) {
        return jpaRepository.findByStatusAfter(status, startAt);
    }

    @Override
    public List<Booking> findByStatusesAfter(List<BookingStatus> statuses, OffsetDateTime startAt) {
        return jpaRepository.findByStatusesAfter(statuses, startAt);
    }

    @Override
    public List<Booking> findByStatusesAndPurposeAfter(
            List<BookingStatus> statuses, SlotPurpose purpose, OffsetDateTime startAt) {
        return jpaRepository.findByStatusesAndPurposeAfter(statuses, purpose, startAt);
    }

    @Override
    public List<Booking> findRecentByDonorAccountId(UUID accountId) {
        return jpaRepository.findRecentByDonorAccountId(accountId);
    }

    @Override
    public Optional<Booking> findByIdAndDonorAccountId(UUID bookingId, UUID accountId) {
        return jpaRepository.findByIdAndDonorAccountId(bookingId, accountId);
    }

    @Override
    public long countActiveBookingsBySlotId(UUID slotId) {
        return jpaRepository.countActiveBookingsBySlotId(slotId);
    }

    @Override
    public Optional<Booking> findPendingBookingByDonorAndSlot(
            UUID donorId, UUID slotId, BookingStatus status) {
        return jpaRepository.findPendingBookingByDonorAndSlot(donorId, slotId, status);
    }

    @Override
    public Optional<Booking> findByIdAndDonorId(UUID bookingId, UUID donorId) {
        return jpaRepository.findByIdAndDonorId(bookingId, donorId);
    }

    @Override
    public List<Booking> findExaminationBookingsFrom(OffsetDateTime startTime) {
        return jpaRepository.findExaminationBookingsFrom(startTime);
    }

    @Override
    public Booking save(Booking booking) {
        return jpaRepository.save(booking);
    }
}
