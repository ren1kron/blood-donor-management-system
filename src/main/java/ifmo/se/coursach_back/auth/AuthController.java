package ifmo.se.coursach_back.auth;

import ifmo.se.coursach_back.auth.dto.AccountProfileResponse;
import ifmo.se.coursach_back.auth.dto.AuthResponse;
import ifmo.se.coursach_back.auth.dto.LoginRequest;
import ifmo.se.coursach_back.auth.dto.RegisterRequest;
import ifmo.se.coursach_back.security.AccountPrincipal;
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

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public AccountProfileResponse me(@AuthenticationPrincipal AccountPrincipal principal) {
        return authService.currentProfile(principal);
    }
}
