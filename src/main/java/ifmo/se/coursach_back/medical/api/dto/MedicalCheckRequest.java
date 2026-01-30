package ifmo.se.coursach_back.medical.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

public record MedicalCheckRequest(
        UUID bookingId,
        UUID visitId,
        @Positive(message = "Weight must be positive")
        @DecimalMax(value = "300", message = "Weight must not exceed 300 kg")
        BigDecimal weightKg,
        @Positive(message = "Hemoglobin must be positive")
        @DecimalMax(value = "250", message = "Hemoglobin must not exceed 250 g/L")
        BigDecimal hemoglobinGl,
        @Min(value = 50, message = "Systolic pressure must be at least 50 mmHg")
        @Max(value = 300, message = "Systolic pressure must not exceed 300 mmHg")
        Integer systolicMmhg,
        @Min(value = 30, message = "Diastolic pressure must be at least 30 mmHg")
        @Max(value = 200, message = "Diastolic pressure must not exceed 200 mmHg")
        Integer diastolicMmhg,
        @NotBlank(message = "Decision is required")
        String decision,
        @Valid DeferralRequest deferral
) {
}
