package ifmo.se.coursach_back.admin.application;

import ifmo.se.coursach_back.admin.api.dto.AdminAssignRolesRequest;
import ifmo.se.coursach_back.admin.api.dto.AdminCreateAccountRequest;
import ifmo.se.coursach_back.admin.api.dto.AdminCreateAccountResponse;
import ifmo.se.coursach_back.admin.api.dto.AdminCreateStaffProfileRequest;
import ifmo.se.coursach_back.admin.api.dto.AdminCreateStaffProfileResponse;
import ifmo.se.coursach_back.admin.api.dto.AdminDonorSummaryResponse;
import ifmo.se.coursach_back.admin.api.dto.AdminStaffSummaryResponse;
import ifmo.se.coursach_back.admin.api.dto.AdminUpdateAccountRequest;
import ifmo.se.coursach_back.shared.application.ports.DomainEventPublisher;
import ifmo.se.coursach_back.shared.domain.event.AuditDomainEvent;
import ifmo.se.coursach_back.exception.BadRequestException;
import ifmo.se.coursach_back.exception.ConflictException;
import ifmo.se.coursach_back.exception.NotFoundException;
import ifmo.se.coursach_back.shared.domain.Account;
import ifmo.se.coursach_back.shared.domain.Role;
import ifmo.se.coursach_back.admin.domain.StaffProfile;
import ifmo.se.coursach_back.shared.application.ports.AccountRepositoryPort;
import ifmo.se.coursach_back.donor.application.ports.DonorProfileRepositoryPort;
import ifmo.se.coursach_back.shared.application.ports.RoleRepositoryPort;
import ifmo.se.coursach_back.admin.application.ports.StaffProfileRepositoryPort;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminAccountService {
    private static final Set<String> STAFF_KINDS = Set.of("DOCTOR", "LAB", "NURSE", "ADMIN");

    private final AccountRepositoryPort accountRepository;
    private final RoleRepositoryPort roleRepository;
    private final StaffProfileRepositoryPort staffProfileRepository;
    private final DonorProfileRepositoryPort donorProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final DomainEventPublisher eventPublisher;

    @Transactional
    public AdminCreateAccountResponse createAccount(UUID adminAccountId, AdminCreateAccountRequest request) {
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

        Account account = new Account();
        account.setEmail(email);
        account.setPhone(phone);
        account.setPasswordHash(passwordEncoder.encode(request.password()));
        if (request.isActive() != null) {
            account.setActive(request.isActive());
        }
        Account saved = accountRepository.save(account);
        eventPublisher.publish(AuditDomainEvent.of(adminAccountId, "ACCOUNT_CREATED", "Account", saved.getId()));
        return new AdminCreateAccountResponse(saved.getId());
    }

    @Transactional
    public AdminCreateStaffProfileResponse createStaffProfile(UUID adminAccountId,
                                                              AdminCreateStaffProfileRequest request) {
        Account account = accountRepository.findById(request.accountId())
                .orElseThrow(() -> new NotFoundException("Account not found"));
        if (staffProfileRepository.findByAccountId(account.getId()).isPresent()) {
            throw new ConflictException("Staff profile already exists");
        }
        if (donorProfileRepository.findByAccountId(account.getId()).isPresent()) {
            throw new ConflictException("Account already belongs to a donor profile");
        }

        String staffKind = normalizeRole(request.staffKind());
        if (staffKind == null || !STAFF_KINDS.contains(staffKind)) {
            throw new BadRequestException("Invalid staffKind");
        }

        StaffProfile staff = new StaffProfile();
        staff.setAccount(account);
        staff.setFullName(request.fullName().trim());
        staff.setStaffKind(staffKind);
        StaffProfile saved = staffProfileRepository.save(staff);
        eventPublisher.publish(AuditDomainEvent.of(adminAccountId, "STAFF_PROFILE_CREATED", "StaffProfile", saved.getId(),
                java.util.Map.of("staffKind", staffKind)));
        return new AdminCreateStaffProfileResponse(saved.getId());
    }

    @Transactional
    public AdminStaffSummaryResponse assignRoles(UUID adminAccountId, UUID accountId,
                                                 AdminAssignRolesRequest request) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found"));
        if (request.roles() == null || request.roles().isEmpty()) {
            throw new BadRequestException("Roles are required");
        }

        Set<Role> roles = new HashSet<>();
        for (String roleCode : request.roles()) {
            String normalized = normalizeRole(roleCode);
            if (normalized == null) {
                throw new BadRequestException("Role code is required");
            }
            Role role = roleRepository.findByCode(normalized)
                    .orElseThrow(() -> new NotFoundException("Role not found"));
            roles.add(role);
        }

        account.setRoles(roles);
        Account saved = accountRepository.save(account);
        eventPublisher.publish(AuditDomainEvent.of(adminAccountId, "ACCOUNT_ROLES_UPDATED", "Account", accountId,
                java.util.Map.of("roles", roles.stream().map(Role::getCode).sorted().toList())));

        StaffProfile staff = staffProfileRepository.findByAccountId(accountId).orElse(null);
        return toStaffSummary(staff, saved);
    }

    @Transactional
    public void updateAccount(UUID adminAccountId, UUID accountId, AdminUpdateAccountRequest request) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found"));
        boolean updated = false;
        if (request.isActive() != null) {
            account.setActive(request.isActive());
            updated = true;
        }
        if (request.newPassword() != null && !request.newPassword().isBlank()) {
            account.setPasswordHash(passwordEncoder.encode(request.newPassword()));
            updated = true;
        }
        if (!updated) {
            throw new BadRequestException("No updates provided");
        }
        accountRepository.save(account);
        eventPublisher.publish(AuditDomainEvent.of(adminAccountId, "ACCOUNT_UPDATED", "Account", accountId));
    }

    public List<AdminStaffSummaryResponse> listStaff(String role, String staffKind) {
        String normalizedKind = normalizeRole(staffKind);
        String normalizedRole = normalizeRole(role);

        return staffProfileRepository.findAll().stream()
                .filter(staff -> normalizedKind == null || normalizedKind.equalsIgnoreCase(staff.getStaffKind()))
                .filter(staff -> {
                    if (normalizedRole == null) {
                        return true;
                    }
                    return staff.getAccount().getRoles().stream()
                            .anyMatch(r -> normalizedRole.equalsIgnoreCase(r.getCode()));
                })
                .map(staff -> toStaffSummary(staff, staff.getAccount()))
                .toList();
    }

    public List<AdminDonorSummaryResponse> listDonors() {
        return donorProfileRepository.findDonorSummaries().stream()
                .map(AdminDonorSummaryResponse::from)
                .toList();
    }

    private AdminStaffSummaryResponse toStaffSummary(StaffProfile staff, Account account) {
        List<String> roles = account.getRoles().stream()
                .map(Role::getCode)
                .sorted()
                .collect(Collectors.toList());
        return new AdminStaffSummaryResponse(
                staff != null ? staff.getId() : null,
                account.getId(),
                staff != null ? staff.getFullName() : null,
                staff != null ? staff.getStaffKind() : null,
                account.getEmail(),
                account.getPhone(),
                account.isActive(),
                roles
        );
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String normalizeRole(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed.toUpperCase(Locale.ROOT);
    }
}
