package ifmo.se.coursach_back.shared.util;

import ifmo.se.coursach_back.exception.BadRequestException;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Normalizes blood group values to the standard Russian format (I, II, III, IV).
 * Also accepts ABO format (O, A, B, AB) for international compatibility.
 */
public final class BloodGroupUtils {

    private static final Set<String> RUSSIAN_FORMAT = Set.of("I", "II", "III", "IV");
    private static final Map<String, String> ABO_TO_RUSSIAN = Map.of(
            "O", "I",
            "A", "II",
            "B", "III",
            "AB", "IV"
    );

    private BloodGroupUtils() {
        // Utility class - prevent instantiation
    }

    /**
     * Normalizes blood group to Russian format.
     *
     * @param value blood group value (can be null)
     * @return normalized blood group or null
     * @throws BadRequestException if value is invalid
     */
    public static String normalize(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }

        String upper = trimmed.toUpperCase(Locale.ROOT);

        if (RUSSIAN_FORMAT.contains(upper)) {
            return upper;
        }

        String mapped = ABO_TO_RUSSIAN.get(upper);
        if (mapped != null) {
            return mapped;
        }

        throw new BadRequestException(
                "bloodGroup must be one of: I, II, III, IV (or O, A, B, AB)"
        );
    }

    /**
     * Validates if a blood group value is valid.
     *
     * @param value blood group to validate
     * @return true if valid or null
     */
    public static boolean isValid(String value) {
        if (value == null || value.trim().isEmpty()) {
            return true;
        }
        String upper = value.trim().toUpperCase(Locale.ROOT);
        return RUSSIAN_FORMAT.contains(upper) || ABO_TO_RUSSIAN.containsKey(upper);
    }

    /**
     * Returns display name for blood group.
     *
     * @param russianFormat blood group in Russian format (I, II, III, IV)
     * @return display name with ABO equivalent
     */
    public static String getDisplayName(String russianFormat) {
        if (russianFormat == null) {
            return null;
        }
        return switch (russianFormat) {
            case "I" -> "I (O)";
            case "II" -> "II (A)";
            case "III" -> "III (B)";
            case "IV" -> "IV (AB)";
            default -> russianFormat;
        };
    }
}
