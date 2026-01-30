package ifmo.se.coursach_back.nurse.application.usecase;

import ifmo.se.coursach_back.nurse.api.dto.CollectionSessionResponse;
import ifmo.se.coursach_back.nurse.api.dto.CollectionSessionUpdateRequest;
import ifmo.se.coursach_back.nurse.application.NurseWorkflowService;
import ifmo.se.coursach_back.nurse.application.command.StartCollectionSessionCommand;
import ifmo.se.coursach_back.nurse.application.result.CollectionSessionResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of StartCollectionSessionUseCase that delegates to NurseWorkflowService.
 */
@Service
@RequiredArgsConstructor
public class StartCollectionSessionService implements StartCollectionSessionUseCase {
    private final NurseWorkflowService nurseWorkflowService;

    @Override
    public CollectionSessionResult execute(StartCollectionSessionCommand command) {
        CollectionSessionUpdateRequest request = new CollectionSessionUpdateRequest(
                command.vitals(),
                null,
                command.notes(),
                null,
                null
        );
        CollectionSessionResponse response = nurseWorkflowService.startSession(
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
