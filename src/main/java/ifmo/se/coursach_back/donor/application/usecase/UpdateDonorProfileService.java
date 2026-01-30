package ifmo.se.coursach_back.donor.application.usecase;

import ifmo.se.coursach_back.donor.api.dto.UpdateDonorProfileRequest;
import ifmo.se.coursach_back.donor.application.DonorService;
import ifmo.se.coursach_back.donor.application.command.UpdateDonorProfileCommand;
import ifmo.se.coursach_back.donor.application.result.DonorProfileResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateDonorProfileService implements UpdateDonorProfileUseCase {
    private final DonorService donorService;

    @Override
    public DonorProfileResult execute(UpdateDonorProfileCommand command) {
        UpdateDonorProfileRequest request = new UpdateDonorProfileRequest(
                command.fullName(),
                command.birthDate(),
                command.bloodGroup(),
                command.rhFactor(),
                command.email(),
                command.phone()
        );
        var response = donorService.updateProfile(command.accountId(), request);
        return new DonorProfileResult(
                response.accountId(),
                response.donorId(),
                response.fullName(),
                response.birthDate(),
                response.bloodGroup(),
                response.rhFactor(),
                response.donorStatus(),
                response.email(),
                response.phone()
        );
    }
}
