package ifmo.se.coursach_back.appointment.infra.jpa;

import ifmo.se.coursach_back.appointment.domain.Visit;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitRepository extends JpaRepository<Visit, UUID> {
    Optional<Visit> findByBooking_Id(UUID bookingId);

    List<Visit> findByBooking_IdIn(Collection<UUID> bookingIds);
}
