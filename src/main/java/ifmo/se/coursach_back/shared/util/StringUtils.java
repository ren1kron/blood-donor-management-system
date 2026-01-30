package ifmo.se.coursach_back.shared.util;

import java.util.Optional;

/**
 * Shared string manipulation utilities.
 * Provides null-safe operations for text normalization and transformation.
 */
public final class StringUtils {

    private StringUtils() {
        // Utility class - prevent instantiation
    }

    /**
     * Normalizes a string by trimming whitespace and returning null for empty strings.
     *
     * @param value the string to normalize
     * @return normalized string or null if empty/null
     */
    public static String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    /**
     * Normalizes a string, returning an Optional.
     *
     * @param value the string to normalize
     * @return Optional containing normalized string, or empty if null/blank
     */
    public static Optional<String> normalizeOptional(String value) {
        return Optional.ofNullable(normalize(value));
    }

    /**
     * Normalizes and converts to lowercase.
     *
     * @param value the string to normalize
     * @return normalized lowercase string or null
     */
    public static String normalizeToLowerCase(String value) {
        String normalized = normalize(value);
        return normalized != null ? normalized.toLowerCase() : null;
    }

    /**
     * Normalizes and converts to uppercase.
     *
     * @param value the string to normalize
     * @return normalized uppercase string or null
     */
    public static String normalizeToUpperCase(String value) {
        String normalized = normalize(value);
        return normalized != null ? normalized.toUpperCase() : null;
    }

    /**
     * Checks if a string is blank (null, empty, or whitespace only).
     *
     * @param value the string to check
     * @return true if blank
     */
    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * Checks if a string is not blank.
     *
     * @param value the string to check
     * @return true if not blank
     */
    public static boolean isNotBlank(String value) {
        return !isBlank(value);
    }

    /**
     * Returns the first non-blank value, or null if all are blank.
     *
     * @param values the values to check
     * @return first non-blank value or null
     */
    public static String firstNonBlank(String... values) {
        for (String value : values) {
            if (isNotBlank(value)) {
                return normalize(value);
            }
        }
        return null;
    }

    /**
     * Truncates a string to maximum length with ellipsis.
     *
     * @param value     the string to truncate
     * @param maxLength maximum length including ellipsis
     * @return truncated string
     */
    public static String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        if (maxLength <= 3) {
            return value.substring(0, maxLength);
        }
        return value.substring(0, maxLength - 3) + "...";
    }
}
