package ifmo.se.coursach_back.medical.application.usecase;

import ifmo.se.coursach_back.medical.application.command.RegisterReactionCommand;
import ifmo.se.coursach_back.medical.application.result.ReactionResult;

/**
 * Use case for registering an adverse reaction.
 */
public interface RegisterAdverseReactionUseCase {
    ReactionResult execute(RegisterReactionCommand command);
}
