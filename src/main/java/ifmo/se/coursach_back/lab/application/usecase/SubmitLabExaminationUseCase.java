package ifmo.se.coursach_back.lab.application.usecase;

import ifmo.se.coursach_back.lab.application.command.SubmitLabExaminationCommand;
import ifmo.se.coursach_back.lab.application.result.LabExaminationSubmitResult;

/**
 * Use case interface for submitting a lab examination.
 */
public interface SubmitLabExaminationUseCase {
    /**
     * Submit lab examination results.
     * @param command the command containing examination data
     * @return the submission result
     */
    LabExaminationSubmitResult execute(SubmitLabExaminationCommand command);
}
