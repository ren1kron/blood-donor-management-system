package ifmo.se.coursach_back.medical.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Locale;

/**
 * Type of blood donation.
 */
public enum DonationType {
    WHOLE_BLOOD("WHOLE_BLOOD"),
    PLASMA("PLASMA"),
    PLATELETS("PLATELETS"),
    ERYTHROCYTES("ERYTHROCYTES"),
    GRANULOCYTES("GRANULOCYTES");

    private final String value;

    DonationType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static DonationType fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        
        String upper = value.trim().toUpperCase(Locale.ROOT).replace("-", "_").replace(" ", "_");
        
        for (DonationType type : values()) {
            if (type.value.equals(upper) || type.name().equals(upper)) {
                return type;
            }
        }
        
        throw new IllegalArgumentException(
                "Invalid donation type: '" + value + "'. Expected one of: WHOLE_BLOOD, PLASMA, PLATELETS, ERYTHROCYTES, GRANULOCYTES"
        );
    }
}
