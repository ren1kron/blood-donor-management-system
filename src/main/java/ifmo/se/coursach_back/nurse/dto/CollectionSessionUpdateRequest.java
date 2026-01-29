package ifmo.se.coursach_back.nurse.dto;

public record CollectionSessionUpdateRequest(
        VitalsPayload preVitals,
        VitalsPayload postVitals,
        String notes,
        String complications,
        String interruptionReason
) {
}
