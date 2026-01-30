package ifmo.se.coursach_back.auth.api;
import ifmo.se.coursach_back.auth.infra.AuthRateLimiter;
import ifmo.se.coursach_back.auth.application.AuthService;

import ifmo.se.coursach_back.auth.api.dto.AccountProfileResponse;
import ifmo.se.coursach_back.auth.api.dto.AuthResponse;
import ifmo.se.coursach_back.auth.api.dto.LoginRequest;
import ifmo.se.coursach_back.auth.api.dto.RegisterRequest;
import ifmo.se.coursach_back.security.AccountPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final AuthRateLimiter authRateLimiter;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(HttpServletRequest httpRequest,
                                                 @Valid @RequestBody RegisterRequest request) {
        String clientKey = resolveClientKey(httpRequest);
        if (!authRateLimiter.allowRegister(clientKey)) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many registration attempts");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public AuthResponse login(HttpServletRequest httpRequest,
                              @Valid @RequestBody LoginRequest request) {
        String clientKey = resolveClientKey(httpRequest);
        if (!authRateLimiter.allowLogin(clientKey)) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many login attempts");
        }
        return authService.login(request);
    }

    @GetMapping("/me")
    public AccountProfileResponse me(@AuthenticationPrincipal AccountPrincipal principal) {
        return authService.currentProfile(principal);
    }

    private String resolveClientKey(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
