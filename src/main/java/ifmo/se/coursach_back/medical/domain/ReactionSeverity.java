package ifmo.se.coursach_back.medical.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Locale;

/**
 * Severity level of adverse reaction during/after donation.
 */
public enum ReactionSeverity {
    MILD("MILD"),
    MODERATE("MODERATE"),
    SEVERE("SEVERE");

    private final String value;

    ReactionSeverity(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ReactionSeverity fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        
        String upper = value.trim().toUpperCase(Locale.ROOT);
        
        for (ReactionSeverity severity : values()) {
            if (severity.value.equals(upper) || severity.name().equals(upper)) {
                return severity;
            }
        }
        
        throw new IllegalArgumentException(
                "Invalid reaction severity: '" + value + "'. Expected one of: MILD, MODERATE, SEVERE"
        );
    }
}
