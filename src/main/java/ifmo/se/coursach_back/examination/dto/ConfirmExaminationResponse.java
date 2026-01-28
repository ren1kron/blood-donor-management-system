package ifmo.se.coursach_back.examination.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ConfirmExaminationResponse(
        UUID bookingId,
        UUID visitId,
        String bookingStatus,
        OffsetDateTime slotStartAt,
        OffsetDateTime slotEndAt,
        String location
) {}
