package ifmo.se.coursach_back.nurse.application.usecase;

import ifmo.se.coursach_back.nurse.api.dto.CollectionSessionResponse;
import ifmo.se.coursach_back.nurse.api.dto.CollectionSessionUpdateRequest;
import ifmo.se.coursach_back.nurse.application.NurseWorkflowService;
import ifmo.se.coursach_back.nurse.application.command.CompleteCollectionSessionCommand;
import ifmo.se.coursach_back.nurse.application.result.CollectionSessionResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of CompleteCollectionSessionUseCase that delegates to NurseWorkflowService.
 */
@Service
@RequiredArgsConstructor
public class CompleteCollectionSessionService implements CompleteCollectionSessionUseCase {
    private final NurseWorkflowService nurseWorkflowService;

    @Override
    public CollectionSessionResult execute(CompleteCollectionSessionCommand command) {
        CollectionSessionUpdateRequest request = new CollectionSessionUpdateRequest(
                null,
                command.postVitals(),
                command.notes(),
                null,
                null
        );
        CollectionSessionResponse response = nurseWorkflowService.completeSession(
                command.accountId(),
                command.sessionId(),
                request
        );
        return toResult(response);
    }

    private CollectionSessionResult toResult(CollectionSessionResponse response) {
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
