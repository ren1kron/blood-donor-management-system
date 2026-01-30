package ifmo.se.coursach_back.admin.application.usecase;

import ifmo.se.coursach_back.admin.api.dto.AdminRegisterDonorRequest;
import ifmo.se.coursach_back.admin.api.dto.AdminRegisterDonorResponse;
import ifmo.se.coursach_back.admin.application.AdminService;
import ifmo.se.coursach_back.admin.application.command.RegisterDonorByPhoneCommand;
import ifmo.se.coursach_back.admin.application.result.RegisterDonorResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of RegisterDonorByPhoneUseCase that delegates to AdminService.
 */
@Service
@RequiredArgsConstructor
public class RegisterDonorByPhoneService implements RegisterDonorByPhoneUseCase {
    private final AdminService adminService;

    @Override
    public RegisterDonorResult execute(RegisterDonorByPhoneCommand command) {
        AdminRegisterDonorRequest request = new AdminRegisterDonorRequest(
                command.fullName(),
                command.phone(),
                command.email(),
                command.password(),
                command.birthDate(),
                command.bloodGroup(),
                command.rhFactor()
        );
        AdminRegisterDonorResponse response = adminService.registerDonorByPhone(request);
        return new RegisterDonorResult(response.accountId(), response.donorId());
    }
}
