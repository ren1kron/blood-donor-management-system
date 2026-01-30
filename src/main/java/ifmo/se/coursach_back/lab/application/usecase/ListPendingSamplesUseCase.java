package ifmo.se.coursach_back.lab.application.usecase;

import ifmo.se.coursach_back.lab.application.result.PendingSampleResult;

/**
 * Use case interface for listing pending samples.
 */
public interface ListPendingSamplesUseCase {
    /**
     * List all pending samples waiting for lab processing.
     * @param status optional status filter; if null, defaults to standard pending statuses
     * @return result containing list of pending samples
     */
    PendingSampleResult execute(String status);
}
