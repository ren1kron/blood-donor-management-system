package ifmo.se.coursach_back.examination.application.usecase;

import ifmo.se.coursach_back.examination.api.dto.ExaminationBookingResponse;
import ifmo.se.coursach_back.examination.application.ExaminationService;
import ifmo.se.coursach_back.examination.application.result.ExaminationBookingResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Implementation of GetExaminationBookingUseCase that delegates to ExaminationService.
 */
@Service
@RequiredArgsConstructor
public class GetExaminationBookingService implements GetExaminationBookingUseCase {
    private final ExaminationService examinationService;

    @Override
    public ExaminationBookingResult execute(UUID accountId, UUID bookingId) {
        ExaminationBookingResponse response = examinationService.getBooking(accountId, bookingId);
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
                null
        );
    }
}
