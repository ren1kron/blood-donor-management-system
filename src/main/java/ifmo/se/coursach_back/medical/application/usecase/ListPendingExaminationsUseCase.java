package ifmo.se.coursach_back.medical.application.usecase;

import ifmo.se.coursach_back.medical.application.result.PendingExaminationResult;
import java.util.List;

/**
 * Use case for listing pending examinations that need review.
 */
public interface ListPendingExaminationsUseCase {
    List<PendingExaminationResult> execute();
}
