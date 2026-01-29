package ifmo.se.coursach_back.nurse.dto;

import java.util.UUID;

public record CollectionSessionCreateRequest(
        UUID visitId,
        UUID bookingId,
        VitalsPayload preVitals,
        String notes
) {
}
