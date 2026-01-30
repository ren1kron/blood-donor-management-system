package ifmo.se.coursach_back.lab.application.usecase;

import ifmo.se.coursach_back.lab.application.command.RecordLabResultCommand;
import ifmo.se.coursach_back.lab.application.result.LabTestResultResult;

/**
 * Use case interface for recording a lab test result.
 */
public interface RecordLabResultUseCase {
    /**
     * Record a lab test result for a sample.
     * @param command the command containing result details
     * @return the recorded lab test result
     */
    LabTestResultResult execute(RecordLabResultCommand command);
}
