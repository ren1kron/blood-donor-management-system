package ifmo.se.coursach_back.examination.application.usecase;

import ifmo.se.coursach_back.examination.application.command.CancelExaminationBookingCommand;

/**
 * Use case interface for cancelling an examination booking.
 */
public interface CancelExaminationBookingUseCase {
    /**
     * Cancel an existing examination booking.
     * @param command command containing account ID and booking ID
     */
    void execute(CancelExaminationBookingCommand command);
}
