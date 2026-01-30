package ifmo.se.coursach_back.auth.infra;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class AuthRateLimiter {
    private static final Duration LOGIN_WINDOW = Duration.ofMinutes(1);
    private static final int LOGIN_LIMIT = 10;
    private static final Duration REGISTER_WINDOW = Duration.ofMinutes(5);
    private static final int REGISTER_LIMIT = 5;

    private final Map<String, Window> windows = new ConcurrentHashMap<>();

    public boolean allowLogin(String clientKey) {
        return allow("login:" + clientKey, LOGIN_LIMIT, LOGIN_WINDOW);
    }

    public boolean allowRegister(String clientKey) {
        return allow("register:" + clientKey, REGISTER_LIMIT, REGISTER_WINDOW);
    }

    private boolean allow(String key, int limit, Duration window) {
        Instant now = Instant.now();
        Window state = windows.compute(key, (k, existing) -> {
            if (existing == null || existing.windowStart.plus(window).isBefore(now)) {
                return new Window(now, 1);
            }
            if (existing.count < limit) {
                existing.count += 1;
            }
            return existing;
        });

        return state.count <= limit;
    }

    private static final class Window {
        private final Instant windowStart;
        private int count;

        private Window(Instant windowStart, int count) {
            this.windowStart = windowStart;
            this.count = count;
        }
    }
}