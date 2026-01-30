package ifmo.se.coursach_back.nurse.application.usecase;

import ifmo.se.coursach_back.nurse.api.dto.CollectionSessionResponse;
import ifmo.se.coursach_back.nurse.application.NurseWorkflowService;
import ifmo.se.coursach_back.nurse.application.result.CollectionSessionResult;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of GetCollectionSessionUseCase that delegates to NurseWorkflowService.
 */
@Service
@RequiredArgsConstructor
public class GetCollectionSessionService implements GetCollectionSessionUseCase {
    private final NurseWorkflowService nurseWorkflowService;

    @Override
    public CollectionSessionResult execute(UUID sessionId) {
        CollectionSessionResponse response = nurseWorkflowService.getSession(sessionId);
        return new CollectionSessionResult(
                response.id(),
                response.visitId(),
                response.nurseId(),
                response.nurseName(),
                response.status(),
                response.startedAt(),
                response.endedAt(),
                response.preVitals(),
                response.postVitals(),
                response.notes(),
                response.complications(),
                response.interruptionReason(),
                response.createdAt(),
                response.updatedAt()
        );
    }
}
