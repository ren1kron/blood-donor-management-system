package ifmo.se.coursach_back.donor.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Locale;

/**
 * Type of consent that donor can give or revoke.
 */
public enum ConsentType {
    DONATION("DONATION"),
    DATA_PROCESSING("DATA_PROCESSING"),
    MARKETING("MARKETING"),
    RESEARCH("RESEARCH");

    private final String value;

    ConsentType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ConsentType fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        
        String normalized = value.trim().toUpperCase(Locale.ROOT).replace("-", "_").replace(" ", "_");
        
        for (ConsentType type : values()) {
            if (type.value.equals(normalized) || type.name().equals(normalized)) {
                return type;
            }
        }
        
        throw new IllegalArgumentException(
                "Invalid consent type: '" + value + "'. Expected one of: DONATION, DATA_PROCESSING, MARKETING, RESEARCH"
        );
    }
}
