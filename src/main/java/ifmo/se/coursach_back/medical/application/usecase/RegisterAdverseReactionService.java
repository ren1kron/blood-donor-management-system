package ifmo.se.coursach_back.medical.application.usecase;

import ifmo.se.coursach_back.medical.api.dto.AdverseReactionRequest;
import ifmo.se.coursach_back.medical.application.MedicalWorkflowService;
import ifmo.se.coursach_back.medical.application.command.RegisterReactionCommand;
import ifmo.se.coursach_back.medical.application.result.ReactionResult;
import ifmo.se.coursach_back.medical.domain.AdverseReaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterAdverseReactionService implements RegisterAdverseReactionUseCase {
    private final MedicalWorkflowService medicalWorkflowService;

    @Override
    public ReactionResult execute(RegisterReactionCommand command) {
        AdverseReactionRequest request = new AdverseReactionRequest(
                command.donationId(),
                command.occurredAt(),
                command.severity(),
                command.description()
        );
        
        AdverseReaction reaction = medicalWorkflowService.registerReaction(
                command.accountId(),
                request
        );
        
        return new ReactionResult(
                reaction.getId(),
                reaction.getDonation().getId(),
                reaction.getSeverity(),
                reaction.getDescription(),
                reaction.getOccurredAt()
        );
    }
}
