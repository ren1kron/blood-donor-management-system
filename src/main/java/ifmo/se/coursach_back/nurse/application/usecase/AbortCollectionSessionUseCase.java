package ifmo.se.coursach_back.nurse.application.usecase;

import ifmo.se.coursach_back.nurse.application.command.AbortCollectionSessionCommand;
import ifmo.se.coursach_back.nurse.application.result.CollectionSessionResult;

/**
 * Use case interface for aborting a collection session.
 */
public interface AbortCollectionSessionUseCase {
    /**
     * Abort a blood collection session (due to complications, donor request, etc).
     * @param command command with session ID, abort reason, and notes
     * @return result with aborted session details
     */
    CollectionSessionResult execute(AbortCollectionSessionCommand command);
}
