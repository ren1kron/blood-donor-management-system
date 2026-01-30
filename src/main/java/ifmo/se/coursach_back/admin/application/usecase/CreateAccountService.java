package ifmo.se.coursach_back.admin.application.usecase;

import ifmo.se.coursach_back.admin.api.dto.AdminCreateAccountRequest;
import ifmo.se.coursach_back.admin.api.dto.AdminCreateAccountResponse;
import ifmo.se.coursach_back.admin.application.AdminAccountService;
import ifmo.se.coursach_back.admin.application.command.CreateAccountCommand;
import ifmo.se.coursach_back.admin.application.result.CreateAccountResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of CreateAccountUseCase that delegates to AdminAccountService.
 */
@Service
@RequiredArgsConstructor
public class CreateAccountService implements CreateAccountUseCase {
    private final AdminAccountService adminAccountService;

    @Override
    public CreateAccountResult execute(CreateAccountCommand command) {
        AdminCreateAccountRequest request = new AdminCreateAccountRequest(
                command.email(),
                command.phone(),
                command.password(),
                command.isActive()
        );
        AdminCreateAccountResponse response = adminAccountService.createAccount(
                command.adminAccountId(),
                request
        );
        return new CreateAccountResult(response.accountId());
    }
}
