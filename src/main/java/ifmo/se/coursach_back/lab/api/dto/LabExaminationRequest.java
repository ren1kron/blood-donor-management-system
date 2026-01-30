package ifmo.se.coursach_back.lab.api.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record LabExaminationRequest(
        @Positive(message = "Hemoglobin must be positive")
        @DecimalMax(value = "250", message = "Hemoglobin must not exceed 250 g/L")
        BigDecimal hemoglobinGl,    // Hemoglobin g/L (50-250)
        
        @DecimalMin(value = "0", message = "Hematocrit cannot be negative")
        @DecimalMax(value = "100", message = "Hematocrit must not exceed 100%")
        BigDecimal hematocritPct,   // Hematocrit % (0-100)
        
        @Positive(message = "RBC must be positive")
        @DecimalMax(value = "10", message = "RBC must not exceed 10 x10^12/L")
        BigDecimal rbc10e12L        // RBC x10^12/L (1-10)
) {
}
