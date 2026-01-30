package ifmo.se.coursach_back.admin.application.usecase;

import ifmo.se.coursach_back.admin.application.result.ExpiredDocumentResult;
import java.time.LocalDate;
import java.util.List;

/**
 * Use case interface for listing expired documents.
 */
public interface ListExpiredDocumentsUseCase {
    /**
     * List expired documents.
     * @param asOf date to check expiration against (null for current date)
     * @return list of expired document results
     */
    List<ExpiredDocumentResult> execute(LocalDate asOf);
}
