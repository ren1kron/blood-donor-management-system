package ifmo.se.coursach_back.appointment.api;
import ifmo.se.coursach_back.appointment.application.AppointmentService;

import ifmo.se.coursach_back.appointment.api.dto.AppointmentSlotResponse;
import ifmo.se.coursach_back.appointment.api.dto.BookingResponse;
import ifmo.se.coursach_back.appointment.api.dto.CreateBookingRequest;
import ifmo.se.coursach_back.appointment.api.dto.CreateSlotRequest;
import ifmo.se.coursach_back.donor.api.dto.DonorBookingResponse;
import ifmo.se.coursach_back.donor.api.dto.RescheduleRequest;
import ifmo.se.coursach_back.appointment.domain.AppointmentSlot;
import ifmo.se.coursach_back.appointment.domain.Booking;
import ifmo.se.coursach_back.appointment.domain.SlotPurpose;
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
    private final AppointmentService appointmentService;

    @GetMapping("/slots")
    public List<AppointmentSlotResponse> listSlots(@RequestParam(value = "from", required = false)
                                                   OffsetDateTime from,
                                                   @RequestParam(value = "purpose", required = false)
                                                   SlotPurpose purpose) {
        OffsetDateTime start = from == null ? OffsetDateTime.now() : from;
        return appointmentService.listUpcomingSlots(start, purpose).stream()
                .map(slot -> AppointmentSlotResponse.from(slot, appointmentService.getSlotBookedCount(slot.getId())))
                .toList();
    }

    @PostMapping("/slots")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public ResponseEntity<AppointmentSlotResponse> createSlot(@Valid @RequestBody CreateSlotRequest request) {
        log.info("POST /api/appointments/slots - Creating slot");
        AppointmentSlot slot = appointmentService.createSlot(request);
        log.info("Slot created with id={}", slot.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(AppointmentSlotResponse.from(slot, 0L));
    }

    @PostMapping("/bookings")
    @PreAuthorize("hasRole('DONOR')")
    public ResponseEntity<BookingResponse> createBooking(@AuthenticationPrincipal AccountPrincipal principal,
                                                         @Valid @RequestBody CreateBookingRequest request) {
        Booking booking = appointmentService.createBooking(principal.getId(), request.slotId());
        return ResponseEntity.status(HttpStatus.CREATED).body(BookingResponse.from(booking));
    }

    @GetMapping("/bookings/my")
    @PreAuthorize("hasRole('DONOR')")
    public List<DonorBookingResponse> listMyBookings(
            @AuthenticationPrincipal AccountPrincipal principal) {
        return appointmentService.listDonorBookings(principal.getId());
    }

    @PostMapping("/bookings/{bookingId}/cancel")
    @PreAuthorize("hasRole('DONOR')")
    public ResponseEntity<Void> cancelBooking(@AuthenticationPrincipal AccountPrincipal principal,
                                              @PathVariable UUID bookingId) {
        appointmentService.cancelBooking(principal.getId(), bookingId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bookings/{bookingId}/reschedule")
    @PreAuthorize("hasRole('DONOR')")
    public ResponseEntity<DonorBookingResponse> rescheduleBooking(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable UUID bookingId,
            @Valid @RequestBody RescheduleRequest request) {
        Booking booking = appointmentService.rescheduleBooking(principal.getId(), bookingId, request.newSlotId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(DonorBookingResponse.from(booking, false));
    }
}
