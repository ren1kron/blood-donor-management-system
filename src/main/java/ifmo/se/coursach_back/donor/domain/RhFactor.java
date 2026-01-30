package ifmo.se.coursach_back.donor.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Locale;
import java.util.Set;

/**
 * Rh factor enum (positive or negative).
 * Accepts various formats: +/-, positive/negative, pos/neg.
 */
public enum RhFactor {
    POSITIVE("+"),
    NEGATIVE("-");

    private static final Set<String> POSITIVE_VALUES = Set.of("+", "POSITIVE", "POS", "RH+", "RH_POSITIVE");
    private static final Set<String> NEGATIVE_VALUES = Set.of("-", "NEGATIVE", "NEG", "RH-", "RH_NEGATIVE");

    private final String displayValue;

    RhFactor(String displayValue) {
        this.displayValue = displayValue;
    }

    @JsonValue
    public String getDisplayValue() {
        return displayValue;
    }

    /**
     * Parses Rh factor from string, accepting various formats.
     * 
     * @param value the string value to parse
     * @return the RhFactor enum value
     * @throws IllegalArgumentException if value is not a valid Rh factor
     */
    @JsonCreator
    public static RhFactor fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        
        String upper = value.trim().toUpperCase(Locale.ROOT);
        
        if (POSITIVE_VALUES.contains(upper)) {
            return POSITIVE;
        }
        
        if (NEGATIVE_VALUES.contains(upper)) {
            return NEGATIVE;
        }
        
        throw new IllegalArgumentException(
                "Invalid Rh factor: '" + value + "'. Expected one of: +, -, positive, negative"
        );
    }

    /**
     * Safe parsing that returns null instead of throwing exception.
     */
    public static RhFactor fromStringOrNull(String value) {
        try {
            return fromString(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
