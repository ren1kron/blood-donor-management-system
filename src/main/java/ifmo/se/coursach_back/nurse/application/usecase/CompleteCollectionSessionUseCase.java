package ifmo.se.coursach_back.nurse.application.usecase;

import ifmo.se.coursach_back.nurse.application.command.CompleteCollectionSessionCommand;
import ifmo.se.coursach_back.nurse.application.result.CollectionSessionResult;

/**
 * Use case interface for completing a collection session.
 */
public interface CompleteCollectionSessionUseCase {
    /**
     * Complete a blood collection session successfully.
     * @param command command with session ID and post-donation vitals
     * @return result with completed session details
     */
    CollectionSessionResult execute(CompleteCollectionSessionCommand command);
}
