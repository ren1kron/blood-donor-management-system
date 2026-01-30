package ifmo.se.coursach_back.auth.infra;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class ClientIpResolver {
    private static final int MAX_IP_LENGTH = 45;

    public String resolve(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        String fromForwarded = firstValidFromForwarded(forwarded);
        if (fromForwarded != null) {
            return fromForwarded;
        }
        String remote = sanitize(request.getRemoteAddr());
        return remote == null || remote.isBlank() ? "unknown" : remote;
    }

    private String firstValidFromForwarded(String headerValue) {
        if (headerValue == null || headerValue.isBlank()) {
            return null;
        }
        String[] parts = headerValue.split(",");
        for (String part : parts) {
            String candidate = sanitize(part);
            if (isValidIp(candidate)) {
                return candidate;
            }
        }
        return null;
    }

    private String sanitize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        int zoneIndex = trimmed.indexOf('%');
        if (zoneIndex > 0) {
            trimmed = trimmed.substring(0, zoneIndex);
        }
        return trimmed;
    }

    private boolean isValidIp(String value) {
        if (value == null || value.isBlank() || value.length() > MAX_IP_LENGTH) {
            return false;
        }
        if (value.indexOf(':') >= 0) {
            return isValidIpv6(value);
        }
        return isValidIpv4(value);
    }

    private boolean isValidIpv4(String value) {
        String[] parts = value.split("\\.");
        if (parts.length != 4) {
            return false;
        }
        for (String part : parts) {
            if (part.isEmpty() || part.length() > 3) {
                return false;
            }
            for (int i = 0; i < part.length(); i++) {
                if (!Character.isDigit(part.charAt(i))) {
                    return false;
                }
            }
            int octet;
            try {
                octet = Integer.parseInt(part);
            } catch (NumberFormatException ex) {
                return false;
            }
            if (octet < 0 || octet > 255) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidIpv6(String value) {
        boolean hasColon = false;
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if (ch == ':') {
                hasColon = true;
                continue;
            }
            if (ch == '.') {
                continue;
            }
            boolean hex = (ch >= '0' && ch <= '9')
                    || (ch >= 'a' && ch <= 'f')
                    || (ch >= 'A' && ch <= 'F');
            if (!hex) {
                return false;
            }
        }
        return hasColon;
    }
}
