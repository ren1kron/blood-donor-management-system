package ifmo.se.coursach_back.auth.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import ifmo.se.coursach_back.exception.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class AuthRateLimitFilter extends OncePerRequestFilter {
    private static final String LOGIN_PATH = "/api/auth/login";
    private static final String REGISTER_PATH = "/api/auth/register";

    private final AuthRateLimiter authRateLimiter;
    private final ClientIpResolver clientIpResolver;
    private final ObjectMapper objectMapper;

    public AuthRateLimitFilter(AuthRateLimiter authRateLimiter,
                               ClientIpResolver clientIpResolver,
                               ObjectMapper objectMapper) {
        this.authRateLimiter = authRateLimiter;
        this.clientIpResolver = clientIpResolver;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!HttpMethod.POST.matches(request.getMethod())) {
            return true;
        }
        String path = request.getRequestURI();
        return !LOGIN_PATH.equals(path) && !REGISTER_PATH.equals(path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String clientIp = clientIpResolver.resolve(request);
        String path = request.getRequestURI();
        boolean allowed = LOGIN_PATH.equals(path)
                ? authRateLimiter.allowLogin(clientIp)
                : authRateLimiter.allowRegister(clientIp);

        if (!allowed) {
            writeRateLimitResponse(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void writeRateLimitResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ErrorResponse error = ErrorResponse.of(
                "RATE_LIMITED",
                "Too many requests. Please try again later.",
                null,
                request.getRequestURI()
        );
        response.setStatus(429);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), error);
    }
}
