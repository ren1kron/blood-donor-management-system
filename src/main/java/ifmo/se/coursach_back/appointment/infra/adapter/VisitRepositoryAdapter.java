package ifmo.se.coursach_back.appointment.infra.adapter;

import ifmo.se.coursach_back.appointment.application.ports.VisitRepositoryPort;
import ifmo.se.coursach_back.appointment.domain.Visit;
import ifmo.se.coursach_back.appointment.infra.jpa.VisitRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class VisitRepositoryAdapter implements VisitRepositoryPort {
    private final VisitRepository jpaRepository;

    @Override
    public Optional<Visit> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<Visit> findByBooking_Id(UUID bookingId) {
        return jpaRepository.findByBooking_Id(bookingId);
    }

    @Override
    public List<Visit> findByBooking_IdIn(Collection<UUID> bookingIds) {
        return jpaRepository.findByBooking_IdIn(bookingIds);
    }

    @Override
    public Visit save(Visit visit) {
        return jpaRepository.save(visit);
    }
}
