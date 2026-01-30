package ifmo.se.coursach_back.examination.application.usecase;

import ifmo.se.coursach_back.examination.application.command.CreateExaminationBookingCommand;
import ifmo.se.coursach_back.examination.application.result.ExaminationBookingResult;

/**
 * Use case interface for creating an examination booking.
 */
public interface CreateExaminationBookingUseCase {
    /**
     * Create a pending examination booking for a donor.
     * @param command command containing account ID and slot ID
     * @return the created booking information
     */
    ExaminationBookingResult execute(CreateExaminationBookingCommand command);
}
