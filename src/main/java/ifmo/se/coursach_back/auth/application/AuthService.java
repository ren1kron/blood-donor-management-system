package ifmo.se.coursach_back.auth.application;

import ifmo.se.coursach_back.auth.api.dto.AccountProfileResponse;
import ifmo.se.coursach_back.auth.api.dto.AuthResponse;
import ifmo.se.coursach_back.auth.api.dto.LoginRequest;
import ifmo.se.coursach_back.auth.api.dto.RegisterRequest;
import ifmo.se.coursach_back.exception.BadRequestException;
import ifmo.se.coursach_back.exception.ConflictException;
import ifmo.se.coursach_back.exception.NotFoundException;
import ifmo.se.coursach_back.shared.domain.Account;
import ifmo.se.coursach_back.donor.domain.DonorProfile;
import ifmo.se.coursach_back.shared.domain.Role;
import ifmo.se.coursach_back.admin.domain.StaffProfile;
import ifmo.se.coursach_back.shared.application.ports.AccountRepositoryPort;
import ifmo.se.coursach_back.donor.application.ports.DonorProfileRepositoryPort;
import ifmo.se.coursach_back.shared.application.ports.RoleRepositoryPort;
import ifmo.se.coursach_back.admin.application.ports.StaffProfileRepositoryPort;
import ifmo.se.coursach_back.security.AccountPrincipal;
import ifmo.se.coursach_back.security.JwtService;
import ifmo.se.coursach_back.shared.util.BloodGroupNormalizer;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AccountRepositoryPort accountRepository;
    private final RoleRepositoryPort roleRepository;
    private final DonorProfileRepositoryPort donorProfileRepository;
    private final StaffProfileRepositoryPort staffProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = normalize(request.email());
        String phone = normalize(request.phone());

        if (email == null && phone == null) {
            throw new BadRequestException("Email or phone is required");
        }
        if (email != null && accountRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("Email is already in use");
        }
        if (phone != null && accountRepository.existsByPhone(phone)) {
            throw new ConflictException("Phone is already in use");
        }

        Role donorRole = roleRepository.findByCode("DONOR")
                .orElseThrow(() -> new BadRequestException("Role DONOR is not configured"));

        Account account = new Account();
        account.setEmail(email);
        account.setPhone(phone);
        account.setPasswordHash(passwordEncoder.encode(request.password()));
        account.setRoles(new java.util.HashSet<>());
        account.getRoles().add(donorRole);
        Account savedAccount = accountRepository.save(account);

        DonorProfile profile = new DonorProfile();
        profile.setAccount(savedAccount);
        profile.setFullName(request.fullName());
        profile.setBirthDate(request.birthDate());
        profile.setBloodGroup(BloodGroupNormalizer.normalizeNullable(request.bloodGroup()));
        profile.setRhFactor(normalize(request.rhFactor()));
        donorProfileRepository.save(profile);

        return buildAuthResponse(savedAccount);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String identifier = normalize(request.identifier());
        if (identifier == null) {
            throw new BadRequestException("Identifier is required");
        }

        Account account = accountRepository.findByEmailIgnoreCaseOrPhoneWithRoles(identifier)
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), account.getPasswordHash())) {
            throw new BadRequestException("Invalid credentials");
        }
        if (!account.isActive()) {
            throw new BadRequestException("Account is inactive");
        }

        return buildAuthResponse(account);
    }

    @Transactional(readOnly = true)
    public AccountProfileResponse currentProfile(AccountPrincipal principal) {
        Account account = accountRepository.findByIdWithRoles(principal.getId())
                .orElseThrow(() -> new NotFoundException("Account not found"));

        String profileType = "NONE";
        String fullName = null;
        DonorProfile donorProfile = donorProfileRepository.findByAccountId(account.getId()).orElse(null);
        if (donorProfile != null) {
            profileType = "DONOR";
            fullName = donorProfile.getFullName();
        } else {
            StaffProfile staffProfile = staffProfileRepository.findByAccountId(account.getId()).orElse(null);
            if (staffProfile != null) {
                profileType = "STAFF";
                fullName = staffProfile.getFullName();
            }
        }

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
