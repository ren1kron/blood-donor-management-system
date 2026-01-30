package ifmo.se.coursach_back.lab.api.dto;

import java.math.BigDecimal;

public record LabExaminationRequest(
        BigDecimal hemoglobinGl,    // Hemoglobin g/L (50-250)
        BigDecimal hematocritPct,   // Hematocrit % (0-100)
        BigDecimal rbc10e12L        // RBC x10^12/L (1-10)
) {
}
