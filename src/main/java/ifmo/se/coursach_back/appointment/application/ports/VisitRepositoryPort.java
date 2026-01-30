package ifmo.se.coursach_back.appointment.application.ports;

import ifmo.se.coursach_back.appointment.domain.Visit;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port interface for Visit repository operations.
 * Application layer depends on this interface, not on JPA repository.
 */
public interface VisitRepositoryPort {
    Optional<Visit> findById(UUID id);
    Optional<Visit> findByBookingId(UUID bookingId);
    List<Visit> findByBookingIds(Collection<UUID> bookingIds);
    Visit save(Visit visit);
}
