package ifmo.se.coursach_back.shared.util;

import ifmo.se.coursach_back.exception.BadRequestException;

import java.util.Locale;
import java.util.Set;

/**
 * Normalizes Rh factor values to standard format (+, -).
 */
public final class RhFactorUtils {

    private static final Set<String> POSITIVE_VALUES = Set.of("+", "POSITIVE", "POS", "RH+", "RH+");
    private static final Set<String> NEGATIVE_VALUES = Set.of("-", "NEGATIVE", "NEG", "RH-", "RH-");

    private RhFactorUtils() {
        // Utility class - prevent instantiation
    }

    /**
     * Normalizes Rh factor to standard format (+ or -).
     *
     * @param value Rh factor value (can be null)
     * @return normalized Rh factor (+ or -) or null
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

        if (POSITIVE_VALUES.contains(upper)) {
            return "+";
        }

        if (NEGATIVE_VALUES.contains(upper)) {
            return "-";
        }

        throw new BadRequestException("rhFactor must be one of: +, -, positive, negative");
    }

    /**
     * Validates if Rh factor value is valid.
     *
     * @param value Rh factor to validate
     * @return true if valid or null
     */
    public static boolean isValid(String value) {
        if (value == null || value.trim().isEmpty()) {
            return true;
        }
        String upper = value.trim().toUpperCase(Locale.ROOT);
        return POSITIVE_VALUES.contains(upper) || NEGATIVE_VALUES.contains(upper);
    }

    /**
     * Returns display name for Rh factor.
     *
     * @param value normalized Rh factor (+ or -)
     * @return display name
     */
    public static String getDisplayName(String value) {
        if (value == null) {
            return null;
        }
        return switch (value) {
            case "+" -> "Rh+";
            case "-" -> "Rh-";
            default -> value;
        };
    }
}
