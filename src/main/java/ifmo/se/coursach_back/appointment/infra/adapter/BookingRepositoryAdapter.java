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
    public long countBySlot_IdAndStatus(UUID slotId, BookingStatus status) {
        return jpaRepository.countBySlot_IdAndStatus(slotId, status);
    }

    @Override
    public boolean existsByDonor_IdAndSlot_Id(UUID donorId, UUID slotId) {
        return jpaRepository.existsByDonor_IdAndSlot_Id(donorId, slotId);
    }

    @Override
    public List<Booking> findByStatusAndSlot_StartAtAfterOrderBySlot_StartAtAsc(BookingStatus status, OffsetDateTime startAt) {
        return jpaRepository.findByStatusAndSlot_StartAtAfterOrderBySlot_StartAtAsc(status, startAt);
    }

    @Override
    public List<Booking> findByStatusInAndSlot_StartAtAfterOrderBySlot_StartAtAsc(List<BookingStatus> statuses, OffsetDateTime startAt) {
        return jpaRepository.findByStatusInAndSlot_StartAtAfterOrderBySlot_StartAtAsc(statuses, startAt);
    }

    @Override
    public List<Booking> findByStatusInAndSlot_PurposeAndSlot_StartAtAfterOrderBySlot_StartAtAsc(
            List<BookingStatus> statuses, SlotPurpose purpose, OffsetDateTime startAt) {
        return jpaRepository.findByStatusInAndSlot_PurposeAndSlot_StartAtAfterOrderBySlot_StartAtAsc(statuses, purpose, startAt);
    }

    @Override
    public List<Booking> findByDonor_Account_IdOrderBySlot_StartAtDesc(UUID accountId) {
        return jpaRepository.findByDonor_Account_IdOrderBySlot_StartAtDesc(accountId);
    }

    @Override
    public Optional<Booking> findByIdAndDonor_Account_Id(UUID bookingId, UUID accountId) {
        return jpaRepository.findByIdAndDonor_Account_Id(bookingId, accountId);
    }

    @Override
    public long countActiveBookingsBySlotId(UUID slotId) {
        return jpaRepository.countActiveBookingsBySlotId(slotId);
    }

    @Override
    public Optional<Booking> findByDonor_IdAndSlot_IdAndStatusAndCancelledAtIsNull(
            UUID donorId, UUID slotId, BookingStatus status) {
        return jpaRepository.findByDonor_IdAndSlot_IdAndStatusAndCancelledAtIsNull(donorId, slotId, status);
    }

    @Override
    public Optional<Booking> findByIdAndDonor_Id(UUID bookingId, UUID donorId) {
        return jpaRepository.findByIdAndDonor_Id(bookingId, donorId);
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
