package ifmo.se.coursach_back.admin.application.usecase;

import ifmo.se.coursach_back.admin.api.dto.ExpiredDocumentResponse;
import ifmo.se.coursach_back.admin.application.AdminService;
import ifmo.se.coursach_back.admin.application.result.ExpiredDocumentResult;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of ListExpiredDocumentsUseCase that delegates to AdminService.
 */
@Service
@RequiredArgsConstructor
public class ListExpiredDocumentsService implements ListExpiredDocumentsUseCase {
    private final AdminService adminService;

    @Override
    public List<ExpiredDocumentResult> execute(LocalDate asOf) {
        List<ExpiredDocumentResponse> responses = adminService.listExpiredDocuments(asOf);
        return responses.stream()
                .map(response -> new ExpiredDocumentResult(
                        response.documentId(),
                        response.donorId(),
                        response.fullName(),
                        response.phone(),
                        response.email(),
                        response.docType(),
                        response.expiresAt()
                ))
                .toList();
    }
}
