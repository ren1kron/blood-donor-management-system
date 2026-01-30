package ifmo.se.coursach_back.donor.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA converter for RhFactor enum.
 * Converts between database values (+/-) and enum values (POSITIVE/NEGATIVE).
 */
@Converter(autoApply = false)
public class RhFactorConverter implements AttributeConverter<RhFactor, String> {

    @Override
    public String convertToDatabaseColumn(RhFactor attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getDisplayValue();
    }

    @Override
    public RhFactor convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }
        return RhFactor.fromString(dbData);
    }
}
