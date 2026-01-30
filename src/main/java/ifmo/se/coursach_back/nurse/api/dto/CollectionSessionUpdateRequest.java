package ifmo.se.coursach_back.nurse.api.dto;

public record CollectionSessionUpdateRequest(
        VitalsPayload preVitals,
        VitalsPayload postVitals,
        String notes,
        String complications,
        String interruptionReason
) {
}
