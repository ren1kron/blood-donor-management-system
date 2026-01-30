package ifmo.se.coursach_back.medical.domain;

/**
 * Decision/status for medical checks during donor examinations.
 */
public enum MedicalCheckDecision {
    /**
     * Awaiting review from doctor after lab examination is complete.
     */
    PENDING_REVIEW,

    /**
     * Donor is admitted for donation.
     */
    ADMITTED,

    /**
     * Donor is refused/deferred from donation.
     */
    REFUSED
}
