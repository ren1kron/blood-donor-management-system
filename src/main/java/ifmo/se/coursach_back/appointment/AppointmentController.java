package ifmo.se.coursach_back.appointment;

import ifmo.se.coursach_back.appointment.dto.AppointmentSlotResponse;
import ifmo.se.coursach_back.appointment.dto.BookingResponse;
import ifmo.se.coursach_back.appointment.dto.CreateBookingRequest;
import ifmo.se.coursach_back.appointment.dto.CreateSlotRequest;
import ifmo.se.coursach_back.model.AppointmentSlot;
import ifmo.se.coursach_back.model.Booking;
import ifmo.se.coursach_back.security.AccountPrincipal;
import jakarta.validation.Valid;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService appointmentService;

    @GetMapping("/slots")
    public List<AppointmentSlotResponse> listSlots(@RequestParam(value = "from", required = false)
                                                   OffsetDateTime from) {
        OffsetDateTime start = from == null ? OffsetDateTime.now() : from;
        return appointmentService.listUpcomingSlots(start).stream()
                .map(AppointmentSlotResponse::from)
                .toList();
    }

    @PostMapping("/slots")
    @PreAuthorize("hasRole('ADMIN') or hasRole('REGISTRAR')")
    public ResponseEntity<AppointmentSlotResponse> createSlot(@Valid @RequestBody CreateSlotRequest request) {
        AppointmentSlot slot = appointmentService.createSlot(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(AppointmentSlotResponse.from(slot));
    }

    @PostMapping("/bookings")
    @PreAuthorize("hasRole('DONOR')")
    public ResponseEntity<BookingResponse> createBooking(@AuthenticationPrincipal AccountPrincipal principal,
                                                         @Valid @RequestBody CreateBookingRequest request) {
        Booking booking = appointmentService.createBooking(principal.getId(), request.slotId());
        return ResponseEntity.status(HttpStatus.CREATED).body(BookingResponse.from(booking));
    }
}
