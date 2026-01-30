package ifmo.se.coursach_back.nurse.application.usecase;

import ifmo.se.coursach_back.nurse.application.result.CollectionSessionResult;
import java.util.UUID;

/**
 * Use case interface for retrieving a collection session.
 */
public interface GetCollectionSessionUseCase {
    /**
     * Get details of a collection session by ID.
     * @param sessionId the session ID to retrieve
     * @return result with session details
     */
    CollectionSessionResult execute(UUID sessionId);
}
