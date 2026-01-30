package ifmo.se.coursach_back.admin.application.usecase;

import ifmo.se.coursach_back.admin.api.dto.AdminUpdateAccountRequest;
import ifmo.se.coursach_back.admin.application.AdminAccountService;
import ifmo.se.coursach_back.admin.application.command.UpdateAccountCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of UpdateAccountUseCase that delegates to AdminAccountService.
 */
@Service
@RequiredArgsConstructor
public class UpdateAccountService implements UpdateAccountUseCase {
    private final AdminAccountService adminAccountService;

    @Override
    public void execute(UpdateAccountCommand command) {
        AdminUpdateAccountRequest request = new AdminUpdateAccountRequest(
                command.isActive(),
                command.newPassword()
        );
        adminAccountService.updateAccount(
                command.adminAccountId(),
                command.accountId(),
                request
        );
    }
}
