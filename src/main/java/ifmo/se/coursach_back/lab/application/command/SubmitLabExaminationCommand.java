package ifmo.se.coursach_back.lab.application.command;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Command object for submitting a lab examination.
 */
public record SubmitLabExaminationCommand(
        UUID accountId,
        UUID requestId,
        BigDecimal hemoglobinGl,
        BigDecimal hematocritPct,
        BigDecimal rbc10e12L
) {
}
