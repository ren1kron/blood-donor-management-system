package ifmo.se.coursach_back.examination.application.usecase;

import ifmo.se.coursach_back.examination.application.command.ConfirmExaminationBookingCommand;
import ifmo.se.coursach_back.examination.application.result.ExaminationBookingResult;

/**
 * Use case interface for confirming an examination booking.
 */
public interface ConfirmExaminationBookingUseCase {
    /**
     * Confirm a pending examination booking with questionnaire and consent data.
     * @param command command containing booking ID and questionnaire data
     * @return the updated booking information
     */
    ExaminationBookingResult execute(ConfirmExaminationBookingCommand command);
}
