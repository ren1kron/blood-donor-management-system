package ifmo.se.coursach_back.appointment.infra.jpa;

import ifmo.se.coursach_back.appointment.domain.Visit;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VisitRepository extends JpaRepository<Visit, UUID> {
    @Query("select v from Visit v where v.booking.id = :bookingId")
    Optional<Visit> findByBookingId(@Param("bookingId") UUID bookingId);

    @Query("select v from Visit v where v.booking.id in :bookingIds")
    List<Visit> findByBookingIds(@Param("bookingIds") Collection<UUID> bookingIds);
}
