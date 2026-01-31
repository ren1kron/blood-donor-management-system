package ifmo.se.coursach_back.auth.infra;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class AuthRateLimiter {
    private static final Bandwidth LOGIN_LIMIT = Bandwidth.classic(
            5, Refill.intervally(5, Duration.ofMinutes(1)));
    private static final Bandwidth REGISTER_LIMIT = Bandwidth.classic(
            3, Refill.intervally(3, Duration.ofMinutes(1)));

    private final Map<String, Bucket> loginBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> registerBuckets = new ConcurrentHashMap<>();

    public boolean allowLogin(String clientKey) {
        return resolveBucket(loginBuckets, clientKey, LOGIN_LIMIT).tryConsume(1);
    }

    public boolean allowRegister(String clientKey) {
        return resolveBucket(registerBuckets, clientKey, REGISTER_LIMIT).tryConsume(1);
    }

    private Bucket resolveBucket(Map<String, Bucket> buckets, String key, Bandwidth limit) {
        return buckets.computeIfAbsent(key, k -> Bucket.builder().addLimit(limit).build());
    }
}
