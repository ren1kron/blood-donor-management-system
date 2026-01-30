package ifmo.se.coursach_back.appointment.api;

import ifmo.se.coursach_back.appointment.api.dto.AppointmentSlotResponse;
import ifmo.se.coursach_back.appointment.api.dto.BookingResponse;
import ifmo.se.coursach_back.appointment.api.dto.CreateBookingRequest;
import ifmo.se.coursach_back.appointment.api.dto.CreateSlotRequest;
import ifmo.se.coursach_back.appointment.application.command.CreateBookingCommand;
import ifmo.se.coursach_back.appointment.application.command.CreateSlotCommand;
import ifmo.se.coursach_back.appointment.application.command.RescheduleBookingCommand;
import ifmo.se.coursach_back.appointment.application.result.AppointmentSlotResult;
import ifmo.se.coursach_back.appointment.application.result.BookingResult;
import ifmo.se.coursach_back.appointment.application.result.DonorBookingResult;
import ifmo.se.coursach_back.appointment.application.usecase.CancelBookingUseCase;
import ifmo.se.coursach_back.appointment.application.usecase.CreateAppointmentSlotUseCase;
import ifmo.se.coursach_back.appointment.application.usecase.CreateBookingUseCase;
import ifmo.se.coursach_back.appointment.application.usecase.ListAppointmentSlotsUseCase;
import ifmo.se.coursach_back.appointment.application.usecase.ListDonorBookingsUseCase;
import ifmo.se.coursach_back.appointment.application.usecase.RescheduleBookingUseCase;
import ifmo.se.coursach_back.appointment.domain.SlotPurpose;
import ifmo.se.coursach_back.donor.api.dto.DonorBookingResponse;
import ifmo.se.coursach_back.donor.api.dto.RescheduleRequest;
import ifmo.se.coursach_back.security.AccountPrincipal;
import jakarta.validation.Valid;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Slf4j
public class AppointmentController {
    private final ListAppointmentSlotsUseCase listAppointmentSlotsUseCase;
    private final CreateAppointmentSlotUseCase createAppointmentSlotUseCase;
    private final CreateBookingUseCase createBookingUseCase;
    private final ListDonorBookingsUseCase listDonorBookingsUseCase;
    private final CancelBookingUseCase cancelBookingUseCase;
    private final RescheduleBookingUseCase rescheduleBookingUseCase;

    @GetMapping("/slots")
    public List<AppointmentSlotResponse> listSlots(@RequestParam(value = "from", required = false)
                                                   OffsetDateTime from,
                                                   @RequestParam(value = "purpose", required = false)
                                                   SlotPurpose purpose) {
        List<AppointmentSlotResult> results = listAppointmentSlotsUseCase.execute(from, purpose);
        return results.stream()
                .map(r -> new AppointmentSlotResponse(
                        r.id(), r.purpose(), r.startAt(), r.endAt(),
                        r.location(), r.capacity(), r.bookedCount()
                ))
                .toList();
    }

    @PostMapping("/slots")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public ResponseEntity<AppointmentSlotResponse> createSlot(@Valid @RequestBody CreateSlotRequest request) {
        log.info("POST /api/appointments/slots - Creating slot");
        CreateSlotCommand command = new CreateSlotCommand(
                request.purpose(), request.startAt(), request.endAt(),
                request.location(), request.capacity()
        );
        AppointmentSlotResult result = createAppointmentSlotUseCase.execute(command);
        log.info("Slot created with id={}", result.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(new AppointmentSlotResponse(
                result.id(), result.purpose(), result.startAt(), result.endAt(),
                result.location(), result.capacity(), result.bookedCount()
        ));
    }

    @PostMapping("/bookings")
    @PreAuthorize("hasRole('DONOR')")
    public ResponseEntity<BookingResponse> createBooking(@AuthenticationPrincipal AccountPrincipal principal,
                                                         @Valid @RequestBody CreateBookingRequest request) {
        CreateBookingCommand command = new CreateBookingCommand(principal.getId(), request.slotId());
        BookingResult result = createBookingUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(new BookingResponse(
                result.id(), result.slotId(), result.status(), result.createdAt()
        ));
    }

    @GetMapping("/bookings/my")
    @PreAuthorize("hasRole('DONOR')")
    public List<DonorBookingResponse> listMyBookings(
            @AuthenticationPrincipal AccountPrincipal principal) {
        List<DonorBookingResult> results = listDonorBookingsUseCase.execute(principal.getId());
        return results.stream()
                .map(r -> new DonorBookingResponse(
                        r.id(), r.slotId(), r.slotPurpose(),
                        r.slotStartTime(), r.slotEndTime(), r.slotLocation(),
                        r.status(), r.createdAt(), r.cancelledAt(), r.hasVisit()
                ))
                .toList();
    }

    @PostMapping("/bookings/{bookingId}/cancel")
    @PreAuthorize("hasRole('DONOR')")
    public ResponseEntity<Void> cancelBooking(@AuthenticationPrincipal AccountPrincipal principal,
                                              @PathVariable UUID bookingId) {
        cancelBookingUseCase.execute(principal.getId(), bookingId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bookings/{bookingId}/reschedule")
    @PreAuthorize("hasRole('DONOR')")
    public ResponseEntity<DonorBookingResponse> rescheduleBooking(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable UUID bookingId,
            @Valid @RequestBody RescheduleRequest request) {
        RescheduleBookingCommand command = new RescheduleBookingCommand(
                principal.getId(), bookingId, request.newSlotId()
        );
        DonorBookingResult result = rescheduleBookingUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(new DonorBookingResponse(
                result.id(), result.slotId(), result.slotPurpose(),
                result.slotStartTime(), result.slotEndTime(), result.slotLocation(),
                result.status(), result.createdAt(), result.cancelledAt(), result.hasVisit()
        ));
    }
}
