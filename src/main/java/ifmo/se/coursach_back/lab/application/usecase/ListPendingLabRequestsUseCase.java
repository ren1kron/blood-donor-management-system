package ifmo.se.coursach_back.lab.application.usecase;

import ifmo.se.coursach_back.lab.application.result.LabExaminationRequestResult;

/**
 * Use case interface for listing pending lab examination requests.
 */
public interface ListPendingLabRequestsUseCase {
    /**
     * List all pending lab examination requests.
     * @return result containing list of pending lab requests
     */
    LabExaminationRequestResult execute();
}
