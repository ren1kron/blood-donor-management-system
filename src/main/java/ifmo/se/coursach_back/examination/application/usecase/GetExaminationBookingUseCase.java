package ifmo.se.coursach_back.examination.application.usecase;

import ifmo.se.coursach_back.examination.application.result.ExaminationBookingResult;
import java.util.UUID;

/**
 * Use case interface for retrieving an examination booking.
 */
public interface GetExaminationBookingUseCase {
    /**
     * Get details of a specific examination booking.
     * @param accountId the account ID of the requester
     * @param bookingId the ID of the booking to retrieve
     * @return the booking information
     */
    ExaminationBookingResult execute(UUID accountId, UUID bookingId);
}
