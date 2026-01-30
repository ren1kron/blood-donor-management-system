package ifmo.se.coursach_back.shared.util;

import ifmo.se.coursach_back.exception.BadRequestException;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;

/**
 * Utility methods for parsing and validating enum values.
 */
public final class EnumUtils {

    private EnumUtils() {
        // Utility class - prevent instantiation
    }

    /**
     * Parses a string to an enum value (case-insensitive).
     *
     * @param enumClass the enum class
     * @param value     the string value to parse
     * @param <T>       enum type
     * @return parsed enum value
     * @throws BadRequestException if value is invalid
     */
    public static <T extends Enum<T>> T parse(Class<T> enumClass, String value) {
        return parseNullable(enumClass, value)
                .orElseThrow(() -> new BadRequestException(
                        String.format("Invalid %s value: '%s'. Allowed values: %s",
                                enumClass.getSimpleName(),
                                value,
                                Arrays.toString(enumClass.getEnumConstants()))
                ));
    }

    /**
     * Parses a string to an enum value, returning Optional.
     *
     * @param enumClass the enum class
     * @param value     the string value to parse (can be null)
     * @param <T>       enum type
     * @return Optional containing parsed enum or empty if null/invalid
     */
    public static <T extends Enum<T>> Optional<T> parseNullable(Class<T> enumClass, String value) {
        if (value == null || value.trim().isEmpty()) {
            return Optional.empty();
        }

        String normalized = value.trim().toUpperCase(Locale.ROOT);

        for (T constant : enumClass.getEnumConstants()) {
            if (constant.name().equals(normalized)) {
                return Optional.of(constant);
            }
        }

        return Optional.empty();
    }

    /**
     * Parses a string to an enum value with a custom name extractor.
     * Useful for enums with custom display names.
     *
     * @param enumClass    the enum class
     * @param value        the string value to parse
     * @param nameExtractor function to extract the matching name from enum
     * @param <T>          enum type
     * @return Optional containing parsed enum or empty
     */
    public static <T extends Enum<T>> Optional<T> parseWithExtractor(
            Class<T> enumClass,
            String value,
            Function<T, String> nameExtractor
    ) {
        if (value == null || value.trim().isEmpty()) {
            return Optional.empty();
        }

        String normalized = value.trim().toUpperCase(Locale.ROOT);

        for (T constant : enumClass.getEnumConstants()) {
            String extractedName = nameExtractor.apply(constant);
            if (extractedName != null && extractedName.toUpperCase(Locale.ROOT).equals(normalized)) {
                return Optional.of(constant);
            }
            if (constant.name().equals(normalized)) {
                return Optional.of(constant);
            }
        }

        return Optional.empty();
    }

    /**
     * Validates if a string is a valid enum value.
     *
     * @param enumClass the enum class
     * @param value     the string value to validate
     * @param <T>       enum type
     * @return true if valid or null
     */
    public static <T extends Enum<T>> boolean isValid(Class<T> enumClass, String value) {
        return value == null || value.trim().isEmpty() || parseNullable(enumClass, value).isPresent();
    }

    /**
     * Returns a comma-separated list of allowed values.
     *
     * @param enumClass the enum class
     * @param <T>       enum type
     * @return string of allowed values
     */
    public static <T extends Enum<T>> String allowedValues(Class<T> enumClass) {
        return String.join(", ", Arrays.stream(enumClass.getEnumConstants())
                .map(Enum::name)
                .toList());
    }
}
