package ifmo.se.coursach_back.lab.application.usecase;

import ifmo.se.coursach_back.lab.application.command.PublishLabResultCommand;
import ifmo.se.coursach_back.lab.application.result.LabTestResultResult;

/**
 * Use case interface for publishing a lab test result.
 */
public interface PublishLabResultUseCase {
    /**
     * Publish a lab test result to make it available.
     * @param command the command containing the result ID to publish
     * @return the published lab test result
     */
    LabTestResultResult execute(PublishLabResultCommand command);
}
