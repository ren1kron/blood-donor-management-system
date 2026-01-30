package ifmo.se.coursach_back.admin;

import ifmo.se.coursach_back.admin.application.AdminAccountService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import ifmo.se.coursach_back.admin.api.dto.AdminAssignRolesRequest;
import ifmo.se.coursach_back.admin.api.dto.AdminCreateAccountRequest;
import ifmo.se.coursach_back.admin.api.dto.AdminCreateStaffProfileRequest;
import ifmo.se.coursach_back.audit.application.AuditService;
import ifmo.se.coursach_back.shared.domain.Account;
import ifmo.se.coursach_back.shared.domain.Role;
import ifmo.se.coursach_back.admin.domain.StaffProfile;
import ifmo.se.coursach_back.shared.infra.jpa.AccountRepository;
import ifmo.se.coursach_back.donor.infra.jpa.DonorProfileRepository;
import ifmo.se.coursach_back.shared.infra.jpa.RoleRepository;
import ifmo.se.coursach_back.admin.infra.jpa.StaffProfileRepository;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

class AdminAccountServiceTest {
    @Mock private AccountRepository accountRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private StaffProfileRepository staffProfileRepository;
    @Mock private DonorProfileRepository donorProfileRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuditService auditService;

    private AdminAccountService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new AdminAccountService(
                accountRepository,
                roleRepository,
                staffProfileRepository,
                donorProfileRepository,
                passwordEncoder,
                auditService
        );
    }

    @Test
    void adminCanCreateStaffAndAssignRoles() {
        UUID adminId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();

        when(accountRepository.existsByEmailIgnoreCase("test@example.com")).thenReturn(false);
        when(accountRepository.existsByPhone("+123"))
                .thenReturn(false);
        when(passwordEncoder.encode("secret"))
                .thenReturn("hash");
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> {
            Account account = invocation.getArgument(0);
            account.setId(accountId);
            return account;
        });

        AdminCreateAccountRequest createAccount = new AdminCreateAccountRequest("test@example.com", "+123", "secret", true);
        var accountResponse = service.createAccount(adminId, createAccount);
        assertEquals(accountId, accountResponse.accountId());

        Account account = new Account();
        account.setId(accountId);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(staffProfileRepository.findByAccountId(accountId)).thenReturn(Optional.empty());
        when(donorProfileRepository.findByAccountId(accountId)).thenReturn(Optional.empty());
        when(staffProfileRepository.save(any(StaffProfile.class))).thenAnswer(invocation -> {
            StaffProfile staff = invocation.getArgument(0);
            staff.setId(UUID.randomUUID());
            return staff;
        });

        AdminCreateStaffProfileRequest createStaff = new AdminCreateStaffProfileRequest(accountId, "Dr. Admin", "DOCTOR");
        var staffResponse = service.createStaffProfile(adminId, createStaff);
        assertNotNull(staffResponse.staffId());

        Role doctorRole = new Role();
        doctorRole.setCode("DOCTOR");
        when(roleRepository.findByCode("DOCTOR")).thenReturn(Optional.of(doctorRole));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(staffProfileRepository.findByAccountId(accountId)).thenReturn(Optional.of(new StaffProfile()));

        var summary = service.assignRoles(adminId, accountId, new AdminAssignRolesRequest(java.util.List.of("DOCTOR")));
        assertEquals(Set.of("DOCTOR"), Set.copyOf(summary.roles()));
    }
}
