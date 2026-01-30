package ifmo.se.coursach_back.examination.api;

import ifmo.se.coursach_back.examination.api.dto.ConfirmExaminationRequest;
import ifmo.se.coursach_back.examination.api.dto.ConfirmExaminationResponse;
import ifmo.se.coursach_back.examination.api.dto.CreateExaminationBookingRequest;
import ifmo.se.coursach_back.examination.api.dto.ExaminationBookingResponse;
import ifmo.se.coursach_back.examination.api.dto.ExaminationSlotResponse;
import ifmo.se.coursach_back.examination.application.command.CancelExaminationBookingCommand;
import ifmo.se.coursach_back.examination.application.command.ConfirmExaminationBookingCommand;
import ifmo.se.coursach_back.examination.application.command.CreateExaminationBookingCommand;
import ifmo.se.coursach_back.examination.application.result.ExaminationBookingResult;
import ifmo.se.coursach_back.examination.application.result.ExaminationSlotResult;
import ifmo.se.coursach_back.examination.application.usecase.CancelExaminationBookingUseCase;
import ifmo.se.coursach_back.examination.application.usecase.ConfirmExaminationBookingUseCase;
import ifmo.se.coursach_back.examination.application.usecase.CreateExaminationBookingUseCase;
import ifmo.se.coursach_back.examination.application.usecase.GetExaminationBookingUseCase;
import ifmo.se.coursach_back.examination.application.usecase.ListExaminationSlotsUseCase;
import ifmo.se.coursach_back.appointment.domain.SlotPurpose;
import ifmo.se.coursach_back.security.AccountPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/donor/examination")
@RequiredArgsConstructor
@PreAuthorize("hasRole('DONOR')")
public class ExaminationController {
    
    private final ListExaminationSlotsUseCase listExaminationSlotsUseCase;
    private final CreateExaminationBookingUseCase createExaminationBookingUseCase;
    private final GetExaminationBookingUseCase getExaminationBookingUseCase;
    private final ConfirmExaminationBookingUseCase confirmExaminationBookingUseCase;
    private final CancelExaminationBookingUseCase cancelExaminationBookingUseCase;
    
    @GetMapping("/slots")
    public List<ExaminationSlotResponse> listSlots(
            @RequestParam(value = "from", required = false) OffsetDateTime from,
            @RequestParam(value = "to", required = false) OffsetDateTime to) {
        List<ExaminationSlotResult> results = listExaminationSlotsUseCase.execute(from, to);
        return results.stream()
                .map(r -> new ExaminationSlotResponse(
                        r.slotId(), SlotPurpose.EXAMINATION,
                        r.startAt(), r.endAt(), r.location(),
                        r.capacity(), r.availableCapacity()
                ))
                .toList();
    }
    
    @PostMapping("/bookings")
    public ResponseEntity<ExaminationBookingResponse> createBooking(
            @AuthenticationPrincipal AccountPrincipal principal,
            @Valid @RequestBody CreateExaminationBookingRequest request) {
        CreateExaminationBookingCommand command = new CreateExaminationBookingCommand(
                principal.getId(), request.slotId()
        );
        ExaminationBookingResult result = createExaminationBookingUseCase.execute(command);
        ExaminationBookingResponse response = mapToBookingResponse(result);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/bookings/{bookingId}")
    public ExaminationBookingResponse getBooking(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable UUID bookingId) {
        ExaminationBookingResult result = getExaminationBookingUseCase.execute(principal.getId(), bookingId);
        return mapToBookingResponse(result);
    }
    
    @PostMapping("/bookings/{bookingId}/confirm")
    public ResponseEntity<ConfirmExaminationResponse> confirmBooking(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable UUID bookingId,
            @Valid @RequestBody ConfirmExaminationRequest request) {
        ConfirmExaminationBookingCommand command = new ConfirmExaminationBookingCommand(
                principal.getId(), bookingId,
                new ConfirmExaminationBookingCommand.QuestionnaireData(
                        request.consentType(),
                        request.consentGiven(),
                        request.questionnairePayload() != null ? request.questionnairePayload().hasFever() : null,
                        request.questionnairePayload() != null ? request.questionnairePayload().tookAntibioticsLast14Days() : null,
                        request.questionnairePayload() != null ? request.questionnairePayload().hasChronicDiseases() : null,
                        request.questionnairePayload() != null ? request.questionnairePayload().comment() : null
                )
        );
        ExaminationBookingResult result = confirmExaminationBookingUseCase.execute(command);
        ConfirmExaminationResponse response = new ConfirmExaminationResponse(
                result.bookingId(), result.visitId(), result.status(),
                result.slotInfo().startAt(), result.slotInfo().endAt(),
                result.slotInfo().location()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @DeleteMapping("/bookings/{bookingId}")
    public ResponseEntity<Void> cancelBooking(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable UUID bookingId) {
        CancelExaminationBookingCommand command = new CancelExaminationBookingCommand(
                principal.getId(), bookingId
        );
        cancelExaminationBookingUseCase.execute(command);
        return ResponseEntity.noContent().build();
    }

    private ExaminationBookingResponse mapToBookingResponse(ExaminationBookingResult result) {
        return new ExaminationBookingResponse(
                result.bookingId(), result.status(), result.createdAt(), result.expiresAt(),
                result.slotInfo().slotId(), result.slotInfo().startAt(),
                result.slotInfo().endAt(), result.slotInfo().location()
        );
    }
}
