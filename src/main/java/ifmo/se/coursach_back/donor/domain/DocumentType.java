package ifmo.se.coursach_back.donor.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Locale;

/**
 * Type of identity document.
 */
public enum DocumentType {
    PASSPORT("PASSPORT"),
    SNILS("SNILS"),
    DRIVING_LICENSE("DRIVING_LICENSE"),
    MILITARY_ID("MILITARY_ID"),
    FOREIGN_PASSPORT("FOREIGN_PASSPORT"),
    OTHER("OTHER");

    private final String value;

    DocumentType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static DocumentType fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        
        String normalized = value.trim().toUpperCase(Locale.ROOT)
                .replace("-", "_")
                .replace(" ", "_");
        
        for (DocumentType type : values()) {
            if (type.value.equals(normalized) || type.name().equals(normalized)) {
                return type;
            }
        }
        
        throw new IllegalArgumentException(
                "Invalid document type: '" + value + "'. Expected one of: PASSPORT, SNILS, DRIVING_LICENSE, MILITARY_ID, FOREIGN_PASSPORT, OTHER"
        );
    }
}
