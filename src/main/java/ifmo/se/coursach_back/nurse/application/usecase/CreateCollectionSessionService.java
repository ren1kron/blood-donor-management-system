package ifmo.se.coursach_back.nurse.application.usecase;

import ifmo.se.coursach_back.nurse.api.dto.CollectionSessionCreateRequest;
import ifmo.se.coursach_back.nurse.api.dto.CollectionSessionResponse;
import ifmo.se.coursach_back.nurse.application.NurseWorkflowService;
import ifmo.se.coursach_back.nurse.application.command.CreateCollectionSessionCommand;
import ifmo.se.coursach_back.nurse.application.result.CollectionSessionResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of CreateCollectionSessionUseCase that delegates to NurseWorkflowService.
 */
@Service
@RequiredArgsConstructor
public class CreateCollectionSessionService implements CreateCollectionSessionUseCase {
    private final NurseWorkflowService nurseWorkflowService;

    @Override
    public CollectionSessionResult execute(CreateCollectionSessionCommand command) {
        CollectionSessionCreateRequest request = new CollectionSessionCreateRequest(
                command.visitId(),
                command.bookingId(),
                command.preVitals(),
                command.notes()
        );
        CollectionSessionResponse response = nurseWorkflowService.createSession(command.accountId(), request);
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
