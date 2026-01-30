package ifmo.se.coursach_back.medical.application.usecase;

import ifmo.se.coursach_back.medical.application.command.RegisterSampleCommand;
import ifmo.se.coursach_back.medical.application.result.SampleResult;

/**
 * Use case for registering a sample from a donation.
 */
public interface RegisterSampleUseCase {
    SampleResult execute(RegisterSampleCommand command);
}
