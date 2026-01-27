package ifmo.se.coursach_back.util;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Normalizes blood groups to the Russian format: I, II, III, IV.
 * Also accepts common ABO inputs (O, A, B, AB) for compatibility.
 */
public final class BloodGroupNormalizer {
    private static final Set<String> RUSSIAN_FORMAT = Set.of("I", "II", "III", "IV");
    private static final Map<String, String> ABO_TO_RUSSIAN = Map.of(
            "O", "I",
            "A", "II",
            "B", "III",
            "AB", "IV"
    );

    private BloodGroupNormalizer() {
    }

    public static String normalizeNullable(String value) {
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

        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "bloodGroup must be one of: I, II, III, IV"
        );
    }
}

