package ifmo.se.coursach_back.examination;

import ifmo.se.coursach_back.examination.dto.ConfirmExaminationRequest;
import ifmo.se.coursach_back.examination.dto.ConfirmExaminationResponse;
import ifmo.se.coursach_back.examination.dto.CreateExaminationBookingRequest;
import ifmo.se.coursach_back.examination.dto.ExaminationBookingResponse;
import ifmo.se.coursach_back.examination.dto.ExaminationSlotResponse;
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
    
    private final ExaminationService examinationService;
    
    @GetMapping("/slots")
    public List<ExaminationSlotResponse> listSlots(
            @RequestParam(value = "from", required = false) OffsetDateTime from,
            @RequestParam(value = "to", required = false) OffsetDateTime to) {
        return examinationService.listAvailableSlots(from, to);
    }
    
    @PostMapping("/bookings")
    public ResponseEntity<ExaminationBookingResponse> createBooking(
            @AuthenticationPrincipal AccountPrincipal principal,
            @Valid @RequestBody CreateExaminationBookingRequest request) {
        ExaminationBookingResponse response = examinationService.createPendingBooking(
                principal.getId(), request.slotId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/bookings/{bookingId}")
    public ExaminationBookingResponse getBooking(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable UUID bookingId) {
        return examinationService.getBooking(principal.getId(), bookingId);
    }
    
    @PostMapping("/bookings/{bookingId}/confirm")
    public ResponseEntity<ConfirmExaminationResponse> confirmBooking(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable UUID bookingId,
            @Valid @RequestBody ConfirmExaminationRequest request) {
        ConfirmExaminationResponse response = examinationService.confirmBooking(
                principal.getId(), bookingId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @DeleteMapping("/bookings/{bookingId}")
    public ResponseEntity<Void> cancelBooking(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable UUID bookingId) {
        examinationService.cancelBooking(principal.getId(), bookingId);
        return ResponseEntity.noContent().build();
    }
}
