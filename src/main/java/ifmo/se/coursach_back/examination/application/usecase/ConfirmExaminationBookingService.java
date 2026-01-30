package ifmo.se.coursach_back.examination.application.usecase;

import ifmo.se.coursach_back.examination.api.dto.ConfirmExaminationRequest;
import ifmo.se.coursach_back.examination.api.dto.ConfirmExaminationResponse;
import ifmo.se.coursach_back.examination.api.dto.QuestionnairePayload;
import ifmo.se.coursach_back.examination.application.ExaminationService;
import ifmo.se.coursach_back.examination.application.command.ConfirmExaminationBookingCommand;
import ifmo.se.coursach_back.examination.application.result.ExaminationBookingResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of ConfirmExaminationBookingUseCase that delegates to ExaminationService.
 */
@Service
@RequiredArgsConstructor
public class ConfirmExaminationBookingService implements ConfirmExaminationBookingUseCase {
    private final ExaminationService examinationService;

    @Override
    public ExaminationBookingResult execute(ConfirmExaminationBookingCommand command) {
        ConfirmExaminationBookingCommand.QuestionnaireData data = command.questionnaireData();
        
        QuestionnairePayload payload = new QuestionnairePayload(
                data.hasFever(),
                data.tookAntibioticsLast14Days(),
                data.hasChronicDiseases(),
                data.comment()
        );
        
        ConfirmExaminationRequest request = new ConfirmExaminationRequest(
                data.consentType(),
                payload,
                data.consentGiven()
        );
        
        ConfirmExaminationResponse response = examinationService.confirmBooking(
                command.accountId(),
                command.bookingId(),
                request
        );
        
        return toResult(response);
    }

    private ExaminationBookingResult toResult(ConfirmExaminationResponse response) {
        return new ExaminationBookingResult(
                response.bookingId(),
                response.bookingStatus(),
                null, // createdAt not available in confirm response
                null, // expiresAt not relevant for confirmed bookings
                new ExaminationBookingResult.SlotInfo(
                        null, // slotId not available in confirm response
                        response.slotStartAt(),
                        response.slotEndAt(),
                        response.location()
                ),
                response.visitId()
        );
    }
}
