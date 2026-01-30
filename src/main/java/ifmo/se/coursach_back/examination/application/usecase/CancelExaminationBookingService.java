package ifmo.se.coursach_back.examination.application.usecase;

import ifmo.se.coursach_back.examination.application.ExaminationService;
import ifmo.se.coursach_back.examination.application.command.CancelExaminationBookingCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of CancelExaminationBookingUseCase that delegates to ExaminationService.
 */
@Service
@RequiredArgsConstructor
public class CancelExaminationBookingService implements CancelExaminationBookingUseCase {
    private final ExaminationService examinationService;

    @Override
    public void execute(CancelExaminationBookingCommand command) {
        examinationService.cancelBooking(command.accountId(), command.bookingId());
    }
}
