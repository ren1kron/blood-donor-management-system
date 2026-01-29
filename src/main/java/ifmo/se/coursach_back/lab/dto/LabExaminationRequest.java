package ifmo.se.coursach_back.lab.dto;

import java.math.BigDecimal;

public record LabExaminationRequest(
        BigDecimal weightKg,
        BigDecimal hemoglobinGl,
        Integer systolicMmhg,
        Integer diastolicMmhg,
        Integer pulseRate,
        BigDecimal bodyTemperatureC
) {
}
