package ifmo.se.coursach_back.notification.domain;

/**
 * Centralized constants for notification topics.
 * Ensures consistency across all notification-related code.
 */
public final class NotificationTopics {

    private NotificationTopics() {
        // Utility class - prevent instantiation
    }

    // === Donation-related topics ===

    /**
     * Notification sent after successful donation.
     */
    public static final String DONATION_COMPLETE = "donation-complete";

    /**
     * Notification sent when donation results are published.
     */
    public static final String DONATION_RESULTS = "donation-results";

    /**
     * Notification for post-donation care instructions.
     */
    public static final String POST_DONATION_CARE = "post-donation-care";

    // === Eligibility topics ===

    /**
     * Notification sent when donor is eligible for next donation.
     */
    public static final String ELIGIBILITY = "eligibility";

    /**
     * Invitation to return for repeat donation.
     */
    public static final String REVISIT = "revisit-reminder";

    // === Medical topics ===

    /**
     * Notification about medical check result.
     */
    public static final String MEDICAL_CHECK = "medical-check";

    /**
     * Notification about adverse reaction.
     */
    public static final String ADVERSE_REACTION = "adverse-reaction";

    /**
     * Notification about deferral.
     */
    public static final String DEFERRAL = "deferral";

    // === Report topics ===

    /**
     * Notification about new report request.
     */
    public static final String REPORT_REQUEST = "report-request";

    /**
     * Notification that report is ready.
     */
    public static final String REPORT_READY = "report-ready";

    // === Document topics ===

    /**
     * Notification about expired documents.
     */
    public static final String EXPIRED_DOCUMENTS = "expired-docs";

    // === Appointment topics ===

    /**
     * Reminder about upcoming appointment.
     */
    public static final String APPOINTMENT_REMINDER = "appointment-reminder";

    /**
     * Confirmation of appointment booking.
     */
    public static final String APPOINTMENT_CONFIRMED = "appointment-confirmed";

    /**
     * Notification about appointment cancellation.
     */
    public static final String APPOINTMENT_CANCELLED = "appointment-cancelled";

    // === Lab topics ===

    /**
     * Notification that lab results are ready.
     */
    public static final String LAB_RESULTS_READY = "lab-results-ready";
}
