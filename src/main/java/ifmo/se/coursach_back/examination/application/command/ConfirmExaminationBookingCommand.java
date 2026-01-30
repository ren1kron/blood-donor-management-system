package ifmo.se.coursach_back.examination.application.command;

import java.util.UUID;

/**
 * Command object for confirming an examination booking with questionnaire data.
 */
public record ConfirmExaminationBookingCommand(
        UUID accountId,
        UUID bookingId,
        QuestionnaireData questionnaireData
) {
    /**
     * Questionnaire data submitted during booking confirmation.
     */
    public record QuestionnaireData(
            String consentType,
            Boolean consentGiven,
            Boolean hasFever,
            Boolean tookAntibioticsLast14Days,
            Boolean hasChronicDiseases,
            String comment
    ) {
    }
}
