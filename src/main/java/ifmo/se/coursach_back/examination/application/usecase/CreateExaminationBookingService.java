package ifmo.se.coursach_back.examination.application.usecase;

import ifmo.se.coursach_back.examination.api.dto.ExaminationBookingResponse;
import ifmo.se.coursach_back.examination.application.ExaminationService;
import ifmo.se.coursach_back.examination.application.command.CreateExaminationBookingCommand;
import ifmo.se.coursach_back.examination.application.result.ExaminationBookingResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of CreateExaminationBookingUseCase that delegates to ExaminationService.
 */
@Service
@RequiredArgsConstructor
public class CreateExaminationBookingService implements CreateExaminationBookingUseCase {
    private final ExaminationService examinationService;

    @Override
    public ExaminationBookingResult execute(CreateExaminationBookingCommand command) {
        ExaminationBookingResponse response = examinationService.createPendingBooking(
                command.accountId(),
                command.slotId()
        );
        return toResult(response);
    }

    private ExaminationBookingResult toResult(ExaminationBookingResponse response) {
        return new ExaminationBookingResult(
                response.bookingId(),
                response.status(),
                response.createdAt(),
                response.expiresAt(),
                new ExaminationBookingResult.SlotInfo(
                        response.slotId(),
                        response.slotStartAt(),
                        response.slotEndAt(),
                        response.location()
                ),
                null // visitId is null for pending bookings
        );
    }
}
