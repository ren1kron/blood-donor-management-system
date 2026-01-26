package ifmo.se.coursach_back.auth;

import ifmo.se.coursach_back.auth.dto.AccountProfileResponse;
import ifmo.se.coursach_back.auth.dto.AuthResponse;
import ifmo.se.coursach_back.auth.dto.LoginRequest;
import ifmo.se.coursach_back.auth.dto.RegisterRequest;
import ifmo.se.coursach_back.model.Account;
import ifmo.se.coursach_back.model.DonorProfile;
import ifmo.se.coursach_back.model.Role;
import ifmo.se.coursach_back.model.StaffProfile;
import ifmo.se.coursach_back.repository.AccountRepository;
import ifmo.se.coursach_back.repository.DonorProfileRepository;
import ifmo.se.coursach_back.repository.RoleRepository;
import ifmo.se.coursach_back.repository.StaffProfileRepository;
import ifmo.se.coursach_back.security.AccountPrincipal;
import ifmo.se.coursach_back.security.JwtService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final DonorProfileRepository donorProfileRepository;
    private final StaffProfileRepository staffProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {
        String email = normalize(request.email());
        String phone = normalize(request.phone());

        if (email == null && phone == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email or phone is required");
        }
        if (email != null && accountRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already in use");
        }
        if (phone != null && accountRepository.existsByPhone(phone)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Phone is already in use");
        }

        Role donorRole = roleRepository.findByCode("DONOR")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role DONOR is not configured"));

        Account account = new Account();
        account.setEmail(email);
        account.setPhone(phone);
        account.setPasswordHash(passwordEncoder.encode(request.password()));
        account.setFullName(request.fullName().trim());
        account.getRoles().add(donorRole);
        Account savedAccount = accountRepository.save(account);

        DonorProfile profile = new DonorProfile();
        profile.setAccount(savedAccount);
        profile.setBirthDate(request.birthDate());
        profile.setBloodGroup(normalize(request.bloodGroup()));
        profile.setRhFactor(normalize(request.rhFactor()));
        donorProfileRepository.save(profile);

        return buildAuthResponse(savedAccount);
    }

    public AuthResponse login(LoginRequest request) {
        String identifier = normalize(request.identifier());
        if (identifier == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Identifier is required");
        }

        Account account = accountRepository.findByEmailIgnoreCaseOrPhone(identifier, identifier)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), account.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        if (!account.isActive()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account is inactive");
        }

        return buildAuthResponse(account);
    }

    public AccountProfileResponse currentProfile(AccountPrincipal principal) {
        Account account = accountRepository.findById(principal.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        String profileType = "NONE";
        DonorProfile donorProfile = donorProfileRepository.findByAccountId(account.getId()).orElse(null);
        if (donorProfile != null) {
            profileType = "DONOR";
        } else {
            StaffProfile staffProfile = staffProfileRepository.findByAccountId(account.getId()).orElse(null);
            if (staffProfile != null) {
                profileType = "STAFF";
            }
        }

        String fullName = account.getFullName();
        List<String> roles = account.getRoles().stream()
                .map(Role::getCode)
                .sorted()
                .toList();

        return new AccountProfileResponse(account.getId(), account.getEmail(), account.getPhone(), roles, profileType,
                fullName);
    }

    private AuthResponse buildAuthResponse(Account account) {
        List<String> roles = account.getRoles().stream()
                .map(Role::getCode)
                .sorted()
                .toList();
        String token = jwtService.generateToken(account);
        return new AuthResponse(token, account.getId(), roles);
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
