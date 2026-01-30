package ifmo.se.coursach_back.nurse.application.usecase;

import ifmo.se.coursach_back.nurse.application.command.CreateCollectionSessionCommand;
import ifmo.se.coursach_back.nurse.application.result.CollectionSessionResult;

/**
 * Use case interface for creating a new collection session.
 */
public interface CreateCollectionSessionUseCase {
    /**
     * Create a new blood collection session for a donor visit.
     * @param command command with visit/booking info and pre-donation vitals
     * @return result with created session details
     */
    CollectionSessionResult execute(CreateCollectionSessionCommand command);
}
