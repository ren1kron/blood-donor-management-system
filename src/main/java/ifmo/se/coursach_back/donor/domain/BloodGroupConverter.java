package ifmo.se.coursach_back.donor.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA converter for BloodGroup enum.
 * Converts between database values (I, II, III, IV) and enum values.
 */
@Converter(autoApply = false)
public class BloodGroupConverter implements AttributeConverter<BloodGroup, String> {

    @Override
    public String convertToDatabaseColumn(BloodGroup attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getDisplayValue();
    }

    @Override
    public BloodGroup convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }
        return BloodGroup.fromString(dbData);
    }
}
