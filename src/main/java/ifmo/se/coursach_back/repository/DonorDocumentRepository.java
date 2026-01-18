package ifmo.se.coursach_back.repository;

import ifmo.se.coursach_back.admin.dto.ExpiredDocumentProjection;
import ifmo.se.coursach_back.model.DonorDocument;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DonorDocumentRepository extends JpaRepository<DonorDocument, UUID> {
    @Query(value = """
            select
                document_id as documentId,
                donor_id as donorId,
                full_name as fullName,
                phone,
                email,
                doc_type as docType,
                expires_at as expiresAt
            from fn_expired_documents(:asOf)
            """, nativeQuery = true)
    List<ExpiredDocumentProjection> findExpiredDocuments(@Param("asOf") LocalDate asOf);
}
