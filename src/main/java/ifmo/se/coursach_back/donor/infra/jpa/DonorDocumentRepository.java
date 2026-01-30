package ifmo.se.coursach_back.donor.infra.jpa;

import ifmo.se.coursach_back.admin.api.dto.ExpiredDocumentRow;
import ifmo.se.coursach_back.donor.domain.DonorDocument;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DonorDocumentRepository extends JpaRepository<DonorDocument, UUID> {
    @Query("""
            select new ifmo.se.coursach_back.admin.api.dto.ExpiredDocumentRow(
                document.id,
                donor.id,
                donor.fullName,
                account.phone,
                account.email,
                document.docType,
                document.expiresAt
            )
            from DonorDocument document
            join document.donor donor
            join donor.account account
            where document.expiresAt is not null and document.expiresAt < :asOf
            order by document.expiresAt asc
            """)
    List<ExpiredDocumentRow> findExpiredDocuments(@Param("asOf") LocalDate asOf);
}
