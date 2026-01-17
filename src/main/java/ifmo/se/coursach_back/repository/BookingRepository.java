package ifmo.se.coursach_back.repository;

import ifmo.se.coursach_back.model.Booking;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    long countBySlot_IdAndStatus(UUID slotId, String status);

    boolean existsByDonor_IdAndSlot_Id(UUID donorId, UUID slotId);

    List<Booking> findByStatusAndSlot_StartAtAfterOrderBySlot_StartAtAsc(String status,
                                                                         java.time.OffsetDateTime startAt);

    List<Booking> findByDonor_Account_IdOrderBySlot_StartAtDesc(UUID accountId);

    Optional<Booking> findByIdAndDonor_Account_Id(UUID bookingId, UUID accountId);
}
