package ifmo.se.coursach_back.donor.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Locale;
import java.util.Map;

/**
 * Blood group enum using Russian format (I, II, III, IV).
 * Accepts both Russian and ABO notation for parsing.
 */
public enum BloodGroup {
    I("I"),      // O in ABO
    II("II"),    // A in ABO
    III("III"),  // B in ABO
    IV("IV");    // AB in ABO

    private static final Map<String, BloodGroup> ABO_MAPPING = Map.of(
            "O", I,
            "A", II,
            "B", III,
            "AB", IV
    );

    private final String displayValue;

    BloodGroup(String displayValue) {
        this.displayValue = displayValue;
    }

    @JsonValue
    public String getDisplayValue() {
        return displayValue;
    }

    /**
     * Parses blood group from string, accepting both Russian (I, II, III, IV) 
     * and ABO (O, A, B, AB) notation.
     * 
     * @param value the string value to parse
     * @return the BloodGroup enum value
     * @throws IllegalArgumentException if value is not a valid blood group
     */
    @JsonCreator
    public static BloodGroup fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        
        String upper = value.trim().toUpperCase(Locale.ROOT);
        
        // Try direct match first
        for (BloodGroup bg : values()) {
            if (bg.displayValue.equals(upper) || bg.name().equals(upper)) {
                return bg;
            }
        }
        
        // Try ABO mapping
        BloodGroup mapped = ABO_MAPPING.get(upper);
        if (mapped != null) {
            return mapped;
        }
        
        throw new IllegalArgumentException(
                "Invalid blood group: '" + value + "'. Expected one of: I, II, III, IV (or O, A, B, AB)"
        );
    }

    /**
     * Safe parsing that returns null instead of throwing exception.
     */
    public static BloodGroup fromStringOrNull(String value) {
        try {
            return fromString(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
