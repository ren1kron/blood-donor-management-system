package ifmo.se.coursach_back.examination.dto;

import ifmo.se.coursach_back.model.BookingStatus;
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
