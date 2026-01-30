package ifmo.se.coursach_back.donor.infra.adapter;

import ifmo.se.coursach_back.admin.api.dto.ExpiredDocumentRow;
import ifmo.se.coursach_back.donor.application.ports.DonorDocumentRepositoryPort;
import ifmo.se.coursach_back.donor.domain.DonorDocument;
import ifmo.se.coursach_back.donor.infra.jpa.DonorDocumentRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DonorDocumentRepositoryAdapter implements DonorDocumentRepositoryPort {
    private final DonorDocumentRepository jpaRepository;

    @Override
    public Optional<DonorDocument> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<ExpiredDocumentRow> findExpiredDocuments(LocalDate asOf) {
        return jpaRepository.findExpiredDocuments(asOf);
    }

    @Override
    public DonorDocument save(DonorDocument document) {
        return jpaRepository.save(document);
    }
}
