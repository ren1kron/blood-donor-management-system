package ifmo.se.coursach_back.medical.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.UUID;

public record MedicalCheckRequest(
        UUID bookingId,
        UUID visitId,
        BigDecimal weightKg,
        BigDecimal hemoglobinGl,
        Integer systolicMmhg,
        Integer diastolicMmhg,
        @NotBlank String decision,
        @Valid DeferralRequest deferral
) {
}
