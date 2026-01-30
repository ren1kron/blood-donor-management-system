package ifmo.se.coursach_back.nurse.application.usecase;

import ifmo.se.coursach_back.nurse.application.command.StartCollectionSessionCommand;
import ifmo.se.coursach_back.nurse.application.result.CollectionSessionResult;

/**
 * Use case interface for starting a collection session.
 */
public interface StartCollectionSessionUseCase {
    /**
     * Start a blood collection session (begin the actual donation process).
     * @param command command with session ID and vitals
     * @return result with updated session details
     */
    CollectionSessionResult execute(StartCollectionSessionCommand command);
}
