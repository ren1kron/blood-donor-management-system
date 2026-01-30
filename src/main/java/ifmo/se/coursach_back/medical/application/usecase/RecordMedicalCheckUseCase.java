package ifmo.se.coursach_back.medical.application.usecase;

import ifmo.se.coursach_back.medical.application.command.RecordMedicalCheckCommand;
import ifmo.se.coursach_back.medical.application.result.MedicalCheckResult;

/**
 * Use case for recording a medical check.
 */
public interface RecordMedicalCheckUseCase {
    MedicalCheckResult execute(RecordMedicalCheckCommand command);
}
