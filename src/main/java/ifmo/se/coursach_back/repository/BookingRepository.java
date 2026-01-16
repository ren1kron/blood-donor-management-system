package ifmo.se.coursach_back.repository;

import ifmo.se.coursach_back.model.Booking;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    long countBySlot_IdAndStatus(UUID slotId, String status);

    boolean existsByDonor_IdAndSlot_Id(UUID donorId, UUID slotId);
}
