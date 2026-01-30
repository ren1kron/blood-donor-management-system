package ifmo.se.coursach_back.medical.application.command;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Command for recording a medical check.
 */
public record RecordMedicalCheckCommand(
        UUID accountId,
        UUID bookingId,
        UUID visitId,
        BigDecimal weightKg,
        BigDecimal hemoglobinGl,
        Integer systolicMmhg,
        Integer diastolicMmhg,
        Integer pulseRate,
        BigDecimal bodyTemperatureC,
        String decision,
        DeferralInfo deferral
) {
    /**
     * Deferral information for refused medical checks.
     */
    public record DeferralInfo(
            String deferralType,
            String reason,
            OffsetDateTime endsAt
    ) {
    }
}
