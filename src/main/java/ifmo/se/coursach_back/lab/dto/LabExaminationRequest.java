package ifmo.se.coursach_back.lab.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record LabExaminationRequest(
        UUID visitId,
        UUID bookingId,
        BigDecimal weightKg,
        BigDecimal hemoglobinGl,
        Integer systolicMmhg,
        Integer diastolicMmhg,
        Integer pulseRate,
        BigDecimal bodyTemperatureC
) {
}
