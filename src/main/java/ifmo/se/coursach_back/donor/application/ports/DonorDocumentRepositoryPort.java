package ifmo.se.coursach_back.donor.application.ports;

import ifmo.se.coursach_back.admin.api.dto.ExpiredDocumentRow;
import ifmo.se.coursach_back.donor.domain.DonorDocument;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port interface for DonorDocument repository operations.
 * Application layer depends on this interface, not on JPA repository.
 */
public interface DonorDocumentRepositoryPort {
    Optional<DonorDocument> findById(UUID id);
    List<ExpiredDocumentRow> findExpiredDocuments(LocalDate asOf);
    DonorDocument save(DonorDocument document);
}
