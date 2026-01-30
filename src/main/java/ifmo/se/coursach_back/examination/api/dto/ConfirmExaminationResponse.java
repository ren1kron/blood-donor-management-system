package ifmo.se.coursach_back.examination.api.dto;

import ifmo.se.coursach_back.appointment.domain.BookingStatus;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ConfirmExaminationResponse(
        UUID bookingId,
        UUID visitId,
        BookingStatus bookingStatus,
        OffsetDateTime slotStartAt,
        OffsetDateTime slotEndAt,
        String location
) {}
