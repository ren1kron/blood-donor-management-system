package ifmo.se.coursach_back.lab.application.usecase;

import ifmo.se.coursach_back.lab.application.command.ReviewBloodUnitCommand;
import ifmo.se.coursach_back.lab.application.result.BloodUnitResult;

/**
 * Use case for lab to review a blood unit (set characteristics, expiration, quarantine).
 */
public interface ReviewBloodUnitUseCase {
    BloodUnitResult execute(ReviewBloodUnitCommand command);
}
