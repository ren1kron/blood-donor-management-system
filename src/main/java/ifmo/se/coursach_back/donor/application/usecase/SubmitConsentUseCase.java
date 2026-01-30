package ifmo.se.coursach_back.donor.application.usecase;

import ifmo.se.coursach_back.donor.application.command.SubmitConsentCommand;
import ifmo.se.coursach_back.donor.application.result.ConsentResult;

/**
 * Use case for submitting donor consent.
 */
public interface SubmitConsentUseCase {
    ConsentResult execute(SubmitConsentCommand command);
}
