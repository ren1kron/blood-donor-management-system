package ifmo.se.coursach_back.admin.api;

import ifmo.se.coursach_back.admin.api.dto.AdminAssignRolesRequest;
import ifmo.se.coursach_back.admin.api.dto.AdminCreateAccountRequest;
import ifmo.se.coursach_back.admin.api.dto.AdminCreateAccountResponse;
import ifmo.se.coursach_back.admin.api.dto.AdminCreateStaffProfileRequest;
import ifmo.se.coursach_back.admin.api.dto.AdminCreateStaffProfileResponse;
import ifmo.se.coursach_back.admin.api.dto.AdminDonorSummaryResponse;
import ifmo.se.coursach_back.admin.api.dto.AdminStaffSummaryResponse;
import ifmo.se.coursach_back.admin.api.dto.AdminUpdateAccountRequest;
import ifmo.se.coursach_back.admin.application.command.AssignRolesCommand;
import ifmo.se.coursach_back.admin.application.command.CreateAccountCommand;
import ifmo.se.coursach_back.admin.application.command.CreateStaffProfileCommand;
import ifmo.se.coursach_back.admin.application.command.UpdateAccountCommand;
import ifmo.se.coursach_back.admin.application.result.CreateAccountResult;
import ifmo.se.coursach_back.admin.application.result.CreateStaffProfileResult;
import ifmo.se.coursach_back.admin.application.result.DonorSummaryResult;
import ifmo.se.coursach_back.admin.application.result.StaffSummaryResult;
import ifmo.se.coursach_back.admin.application.usecase.AssignRolesUseCase;
import ifmo.se.coursach_back.admin.application.usecase.CreateAccountUseCase;
import ifmo.se.coursach_back.admin.application.usecase.CreateStaffProfileUseCase;
import ifmo.se.coursach_back.admin.application.usecase.ListDonorsUseCase;
import ifmo.se.coursach_back.admin.application.usecase.ListStaffUseCase;
import ifmo.se.coursach_back.admin.application.usecase.UpdateAccountUseCase;
import ifmo.se.coursach_back.security.AccountPrincipal;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminAccountsController {
    private final CreateAccountUseCase createAccountUseCase;
    private final CreateStaffProfileUseCase createStaffProfileUseCase;
    private final AssignRolesUseCase assignRolesUseCase;
    private final UpdateAccountUseCase updateAccountUseCase;
    private final ListStaffUseCase listStaffUseCase;
    private final ListDonorsUseCase listDonorsUseCase;

    @PostMapping("/accounts")
    public ResponseEntity<AdminCreateAccountResponse> createAccount(
            @AuthenticationPrincipal AccountPrincipal principal,
            @Valid @RequestBody AdminCreateAccountRequest request) {
        CreateAccountCommand command = new CreateAccountCommand(
                principal.getId(), request.email(), request.phone(),
                request.password(), request.isActive()
        );
        CreateAccountResult result = createAccountUseCase.execute(command);
        AdminCreateAccountResponse response = new AdminCreateAccountResponse(result.accountId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/staff-profiles")
    public ResponseEntity<AdminCreateStaffProfileResponse> createStaffProfile(
            @AuthenticationPrincipal AccountPrincipal principal,
            @Valid @RequestBody AdminCreateStaffProfileRequest request) {
        CreateStaffProfileCommand command = new CreateStaffProfileCommand(
                principal.getId(), request.accountId(),
                request.fullName(), request.staffKind()
        );
        CreateStaffProfileResult result = createStaffProfileUseCase.execute(command);
        AdminCreateStaffProfileResponse response = new AdminCreateStaffProfileResponse(result.staffProfileId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/accounts/{accountId}/roles")
    public ResponseEntity<AdminStaffSummaryResponse> assignRoles(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable UUID accountId,
            @Valid @RequestBody AdminAssignRolesRequest request) {
        AssignRolesCommand command = new AssignRolesCommand(
                principal.getId(), accountId, request.roles()
        );
        StaffSummaryResult result = assignRolesUseCase.execute(command);
        AdminStaffSummaryResponse response = new AdminStaffSummaryResponse(
                result.staffId(), result.accountId(), result.fullName(),
                result.staffKind(), result.email(), result.phone(),
                result.isActive(), result.roles()
        );
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/accounts/{accountId}")
    public ResponseEntity<Void> updateAccount(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable UUID accountId,
            @Valid @RequestBody AdminUpdateAccountRequest request) {
        UpdateAccountCommand command = new UpdateAccountCommand(
                principal.getId(), accountId,
                request.isActive(), request.newPassword()
        );
        updateAccountUseCase.execute(command);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/staff")
    public List<AdminStaffSummaryResponse> listStaff(
            @RequestParam(value = "role", required = false) String role,
            @RequestParam(value = "staffKind", required = false) String staffKind) {
        List<StaffSummaryResult> results = listStaffUseCase.execute(role, staffKind);
        return results.stream()
                .map(r -> new AdminStaffSummaryResponse(
                        r.staffId(), r.accountId(), r.fullName(),
                        r.staffKind(), r.email(), r.phone(),
                        r.isActive(), r.roles()
                ))
                .toList();
    }

    @GetMapping("/donors")
    public List<AdminDonorSummaryResponse> listDonors() {
        List<DonorSummaryResult> results = listDonorsUseCase.execute();
        return results.stream()
                .map(r -> new AdminDonorSummaryResponse(
                        r.donorId(), r.fullName(),
                        r.donorStatus() != null ? 
                            ifmo.se.coursach_back.donor.domain.DonorStatus.valueOf(r.donorStatus()) : null,
                        r.email(), r.phone(),
                        r.lastDonationAt(), r.lastAdmissionAt()
                ))
                .toList();
    }
}
