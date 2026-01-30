package ifmo.se.coursach_back.medical.application.usecase;

import ifmo.se.coursach_back.medical.application.command.ReviewExaminationCommand;
import ifmo.se.coursach_back.medical.application.result.ExaminationReviewResult;

/**
 * Use case for reviewing a pending examination.
 */
public interface ReviewExaminationUseCase {
    ExaminationReviewResult execute(ReviewExaminationCommand command);
}
