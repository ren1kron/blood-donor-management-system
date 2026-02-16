package ifmo.se.coursach_back.config;

import ifmo.se.coursach_back.admin.domain.StaffProfile;
import ifmo.se.coursach_back.admin.infra.jpa.StaffProfileRepository;
import ifmo.se.coursach_back.donor.domain.BloodGroup;
import ifmo.se.coursach_back.donor.domain.DonorProfile;
import ifmo.se.coursach_back.donor.domain.DonorStatus;
import ifmo.se.coursach_back.donor.domain.RhFactor;
import ifmo.se.coursach_back.donor.infra.jpa.DonorProfileRepository;
import ifmo.se.coursach_back.shared.domain.Account;
import ifmo.se.coursach_back.shared.domain.Role;
import ifmo.se.coursach_back.shared.infra.jpa.AccountRepository;
import ifmo.se.coursach_back.shared.infra.jpa.RoleRepository;
import java.time.LocalDate;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DemoDataBootstrap implements ApplicationRunner {
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final DonorProfileRepository donorProfileRepository;
    private final StaffProfileRepository staffProfileRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        ensureAccountWithRole("god@system.local", "big_papa", "GOD");

        Account donor = ensureAccountWithRole("donor@system.local", "donor_pass", "DONOR");
        ensureDonorProfile(donor, "Demo Donor Nikolaevich", LocalDate.of(1995, 5, 15), BloodGroup.I, RhFactor.POSITIVE);

        Account admin = ensureAccountWithRole("admin@system.local", "admin_pass", "ADMIN");
        ensureStaffProfile(admin, "Demo Admin Ignatevich", "ADMIN");

        Account doctor = ensureAccountWithRole("doctor@system.local", "doctor_pass", "DOCTOR");
        ensureStaffProfile(doctor, "Demo Doctor Romanovich", "DOCTOR");

        Account nurse = ensureAccountWithRole("nurse@system.local", "nurse_pass", "NURSE");
        ensureStaffProfile(nurse, "Demo Nurse Ivanovna", "NURSE");

        Account lab = ensureAccountWithRole("lab@system.local", "lab_pass", "LAB");
        ensureStaffProfile(lab, "Demo Lab Tech", "LAB");
    }

    private Account ensureAccountWithRole(String email, String rawPassword, String roleCode) {
        Account account = accountRepository.findByEmailIgnoreCase(email)
                .orElseGet(() -> createAccount(email, rawPassword));
        ensureRole(account, roleCode);
        return account;
    }

    private Account createAccount(String email, String rawPassword) {
        Account account = new Account();
        account.setEmail(email.toLowerCase(Locale.ROOT));
        account.setPasswordHash(passwordEncoder.encode(rawPassword));
        return accountRepository.save(account);
    }

    private void ensureRole(Account account, String roleCode) {
        Role role = roleRepository.findByCode(roleCode)
                .orElseGet(() -> roleRepository.save(new Role(null, roleCode, defaultRoleName(roleCode))));
        boolean hasRole = account.getRoles().stream().anyMatch(existing -> roleCode.equalsIgnoreCase(existing.getCode()));
        if (!hasRole) {
            account.getRoles().add(role);
            accountRepository.save(account);
        }
    }

    private String defaultRoleName(String roleCode) {
        return switch (roleCode) {
            case "GOD" -> "Head Administrator";
            case "ADMIN" -> "Administrator";
            case "DOCTOR" -> "Doctor";
            case "NURSE" -> "Nurse";
            case "LAB" -> "Lab Technician";
            case "DONOR" -> "Donor";
            default -> roleCode;
        };
    }

    private void ensureDonorProfile(Account account, String fullName, LocalDate birthDate, BloodGroup bloodGroup,
                                    RhFactor rhFactor) {
        if (donorProfileRepository.findByAccountId(account.getId()).isPresent()) {
            return;
        }
        DonorProfile profile = new DonorProfile();
        profile.setAccount(account);
        profile.setFullName(fullName);
        profile.setBirthDate(birthDate);
        profile.setBloodGroup(bloodGroup);
        profile.setRhFactor(rhFactor);
        profile.setDonorStatus(DonorStatus.ACTIVE);
        donorProfileRepository.save(profile);
    }

    private void ensureStaffProfile(Account account, String fullName, String staffKind) {
        if (staffProfileRepository.findByAccountId(account.getId()).isPresent()) {
            return;
        }
        StaffProfile profile = new StaffProfile();
        profile.setAccount(account);
        profile.setFullName(fullName);
        profile.setStaffKind(staffKind);
        staffProfileRepository.save(profile);
    }
}
