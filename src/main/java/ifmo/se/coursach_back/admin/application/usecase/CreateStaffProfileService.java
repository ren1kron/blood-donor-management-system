package ifmo.se.coursach_back.admin.application.usecase;

import ifmo.se.coursach_back.admin.api.dto.AdminCreateStaffProfileRequest;
import ifmo.se.coursach_back.admin.api.dto.AdminCreateStaffProfileResponse;
import ifmo.se.coursach_back.admin.application.AdminAccountService;
import ifmo.se.coursach_back.admin.application.command.CreateStaffProfileCommand;
import ifmo.se.coursach_back.admin.application.result.CreateStaffProfileResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of CreateStaffProfileUseCase that delegates to AdminAccountService.
 */
@Service
@RequiredArgsConstructor
public class CreateStaffProfileService implements CreateStaffProfileUseCase {
    private final AdminAccountService adminAccountService;

    @Override
    public CreateStaffProfileResult execute(CreateStaffProfileCommand command) {
        AdminCreateStaffProfileRequest request = new AdminCreateStaffProfileRequest(
                command.accountId(),
                command.fullName(),
                command.staffKind()
        );
        AdminCreateStaffProfileResponse response = adminAccountService.createStaffProfile(
                command.adminAccountId(),
                request
        );
        return new CreateStaffProfileResult(response.staffId());
    }
}
